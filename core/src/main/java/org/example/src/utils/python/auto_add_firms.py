#!/usr/bin/env python3
"""
auto_add_firms.py — Prepara o input para o Gemini Chat automaticamente.

Fluxo:
  1. Le byNewPage.json e coleta os primeiros BATCH_SIZE objetos validos
     (ignora entradas com chave "sep", com link vazio, e com "generated": true)
  2. Selenium headless extrai HTML da secao de lawyers de cada firma
  3. Classifica automaticamente: BYPAGE se o HTML da listagem ja expoe
     email + role + nome (dados suficientes sem abrir perfil individual);
     caso contrario BYNEWPAGE
  4. Salva newFirmsToMake.txt no formato esperado pelo Gemini Chat

Uso:
  python auto_add_firms.py

Dependencias:
  pip install selenium beautifulsoup4
"""

import json
import re
import sys
import time
from pathlib import Path

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
# Script location: core/src/main/java/org/example/src/utils/python/
# parents[0]=utils  [1]=src(example)  [2]=example  [3]=org  [4]=java
# [5]=main  [6]=src(core)  [7]=core  [8]=project root
SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parents[8]

JSON_PATH = PROJECT_ROOT / "core/src/main/resources/todos/byNewPage.json"
OUTPUT_PATH = PROJECT_ROOT / "newFirmsToMake.txt"

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------
BATCH_SIZE = 20        # how many firms to process per run
MAX_HTML_LINES = 120
MAX_HTML_CHARS = 10_000
PAGE_LOAD_WAIT = 30     # seconds

# Ordered by specificity — stops at first element with enough content
LAWYER_SELECTORS = [
    "#team", "#our-team", "#attorneys", "#lawyers", "#people", "#staff",
    ".team-members", ".our-team", ".lawyers-list", ".attorneys-list", ".people-list",
    "[class*='attorney']", "[class*='lawyer']",
    "[class*='people-list']", "[class*='team-member']",
    "[class*='our-team']", "[class*='staff-list']",
    "main",
]
MIN_HTML_LEN = 300  # minimum chars to consider a section "found"

# Signals used to classify a firm as BYPAGE
_EMAIL_RE = re.compile(r'[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}')
_ROLE_KEYWORDS = [
    "partner", "counsel", "associate", "director", "solicitor",
    "barrister", "advocate", "attorney", "principal", "senior",
]


# ===========================================================================
# Step 1 — Load first N valid firms from backlog
# ===========================================================================

def load_first_n(json_path: Path, n: int) -> list:
    with open(json_path, encoding="utf-8") as f:
        raw = f.read()
    # Remove trailing commas before ] or } (invalid JSON but common in hand-edited files)
    raw = re.sub(r",\s*([}\]])", r"\1", raw)
    data = json.loads(raw)

    firms = []
    for entry in data:
        if len(firms) >= n:
            break
        if "sep" in entry:
            continue
        if not entry.get("link", "").strip():
            continue
        if entry.get("generated"):
            continue
        firms.append(entry)
    return firms


# ===========================================================================
# Step 2 — Extract HTML with Selenium headless
# ===========================================================================

def get_driver():
    from selenium import webdriver
    from selenium.webdriver.chrome.options import Options

    opts = Options()
    opts.add_argument("--headless=new")
    opts.add_argument("--no-sandbox")
    opts.add_argument("--disable-dev-shm-usage")
    opts.add_argument("--disable-gpu")
    opts.add_argument("--window-size=1280,900")
    opts.add_argument("--log-level=3")
    opts.add_experimental_option("excludeSwitches", ["enable-logging"])
    return webdriver.Chrome(options=opts)


def extract_html(firm: dict, driver) -> str:
    from selenium.webdriver.common.by import By
    from bs4 import BeautifulSoup

    url = firm.get("link", "")
    print(f"    Acessando: {url}")
    try:
        driver.get(url)
        time.sleep(PAGE_LOAD_WAIT)
    except Exception as e:
        return f"(erro ao carregar: {e})"

    for selector in LAWYER_SELECTORS:
        try:
            elements = driver.find_elements(By.CSS_SELECTOR, selector)
            if not elements:
                continue
            html = elements[0].get_attribute("outerHTML") or ""
            if len(html.strip()) >= MIN_HTML_LEN:
                return _limit_html(html)
        except Exception:
            continue

    # Final fallback: full body
    soup = BeautifulSoup(driver.page_source, "html.parser")
    body = soup.find("body")
    if body:
        return _limit_html(str(body))
    return "(nao foi possivel extrair HTML)"


def _limit_html(html: str) -> str:
    lines = html.splitlines()
    if len(lines) > MAX_HTML_LINES:
        html = "\n".join(lines[:MAX_HTML_LINES]) + "\n... (truncado)"
    if len(html) > MAX_HTML_CHARS:
        html = html[:MAX_HTML_CHARS] + "\n... (truncado)"
    return html


# ===========================================================================
# Step 3 — Classify firm as BYNEWPAGE or BYPAGE based on extracted HTML
# ===========================================================================

def classify_html(html: str) -> str:
    """
    Returns 'BYPAGE' when the listing page already exposes the key lawyer
    data fields (email, role, name) — meaning there is no need to navigate
    to individual profile pages.

    Criteria (all must be true):
      - An email address is present (mailto: link OR inline email pattern)
      - At least one role keyword is present (partner, counsel, associate…)

    Otherwise returns 'BYNEWPAGE'.
    """
    html_lower = html.lower()

    has_email = "mailto:" in html_lower or bool(_EMAIL_RE.search(html))
    has_role = any(kw in html_lower for kw in _ROLE_KEYWORDS)

    if has_email and has_role:
        return "BYPAGE"
    return "BYNEWPAGE"


# ===========================================================================
# Step 4 — Build and save newFirmsToMake.txt
# ===========================================================================

def _format_firm_block(firm: dict, html: str) -> str:
    json_entry = {k: v for k, v in firm.items() if k != "generated"}
    json_str = json.dumps(json_entry, ensure_ascii=False, indent=2)
    name = firm.get("name") or firm.get("link", "firma")
    return f"--- {name} ---\nJSON:\n{json_str}\nHTML:\n{html}\n"


def build_and_save(results: list, output_path: Path) -> None:
    """results: list of (firm, tipo, html)"""
    bynewpage_blocks = []
    bypage_blocks = []

    for firm, tipo, html in results:
        block = _format_firm_block(firm, html)
        if tipo == "BYPAGE":
            bypage_blocks.append(block)
        else:
            bynewpage_blocks.append(block)

    parts = ["BYNEWPAGE\n\n"]
    parts.extend(bynewpage_blocks)
    parts.append("\nBYPAGE\n\n")
    parts.extend(bypage_blocks)

    output_path.write_text("\n".join(parts), encoding="utf-8")


# ===========================================================================
# Step 5 — Remove processed firms from backlog
# ===========================================================================

def remove_processed_from_backlog(json_path: Path, processed: list) -> None:
    with open(json_path, encoding="utf-8") as f:
        raw = f.read()
    raw = re.sub(r",\s*([}\]])", r"\1", raw)
    data = json.loads(raw)

    processed_links = {f.get("link", "") for f in processed}

    filtered = [
        entry for entry in data
        if "sep" in entry or entry.get("link", "") not in processed_links
    ]

    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(filtered, f, ensure_ascii=False, indent=2)


# ===========================================================================
# Main
# ===========================================================================

def main():
    print("\n=== auto_add_firms ===")
    print(f"Projeto: {PROJECT_ROOT}")

    if not JSON_PATH.exists():
        print(f"ERRO: {JSON_PATH} nao encontrado.")
        sys.exit(1)

    firms = load_first_n(JSON_PATH, BATCH_SIZE)
    if not firms:
        print("Nenhuma firma disponivel no backlog.")
        sys.exit(0)

    print(f"\n{len(firms)} firmas carregadas. Iniciando extracao de HTML...\n")

    driver = get_driver()
    results = []
    try:
        for i, firm in enumerate(firms, 1):
            name = firm.get("name") or firm.get("link", "")
            html = extract_html(firm, driver)
            tipo = classify_html(html)
            print(f"  [{i:2d}/{len(firms)}] {name}  ->  {tipo}")
            results.append((firm, tipo, html))
    finally:
        driver.quit()

    build_and_save(results, OUTPUT_PATH)
    remove_processed_from_backlog(JSON_PATH, firms)

    n_new = sum(1 for _, t, _ in results if t == "BYNEWPAGE")
    n_page = sum(1 for _, t, _ in results if t == "BYPAGE")
    print(f"\nArquivo gerado: {OUTPUT_PATH}")
    print(f"  {n_new} ByNewPage  |  {n_page} ByPage")
    print(f"  {len(firms)} firma(s) removida(s) de {JSON_PATH.name}")
    print("\nCole o conteudo de newFirmsToMake.txt no Gemini Chat.")


if __name__ == "__main__":
    main()

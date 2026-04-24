#!/usr/bin/env python3
"""
Moves .java firm files from:
  core/src/main/java/org/example/src/sites/to_test/{continent}/
to:
  firms/src/main/java/org/example/src/sites/{continent}/

Also updates the package declaration in each moved file.
Script is at: core/src/main/java/org/example/src/utils/python/
Project root is 9 levels up.
"""

import os
import shutil

_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

CONTINENTS = ["africa", "americas", "asia", "europe", "mundial", "oceania"]

SOURCE_BASE = os.path.join(
    PROJECT_ROOT,
    "core", "src", "main", "java", "org", "example", "src", "sites", "to_test"
)
DEST_BASE = os.path.join(
    PROJECT_ROOT,
    "firms", "src", "main", "java", "org", "example", "src", "sites"
)


def list_java_files(continent: str) -> list[str]:
    folder = os.path.join(SOURCE_BASE, continent)
    if not os.path.isdir(folder):
        return []
    return sorted(f for f in os.listdir(folder) if f.endswith(".java"))


def move_firm(filename: str, continent: str) -> str:
    src = os.path.join(SOURCE_BASE, continent, filename)
    dst_dir = os.path.join(DEST_BASE, continent)
    dst = os.path.join(dst_dir, filename)

    with open(src, "r", encoding="utf-8") as f:
        content = f.read()

    old_pkg = f"package org.example.src.sites.to_test.{continent};"
    new_pkg = f"package org.example.src.sites.{continent};"
    content = content.replace(old_pkg, new_pkg, 1)

    os.makedirs(dst_dir, exist_ok=True)
    with open(dst, "w", encoding="utf-8") as f:
        f.write(content)

    os.remove(src)
    return dst


def _box(title: str, rows: list[tuple[str, str]], footer: list[str], width: int = 58) -> str:
    inner = width - 2
    top    = "┌" + "─" * inner + "┐"
    sep    = "├" + "─" * inner + "┤"
    bot    = "└" + "─" * inner + "┘"

    def row(left: str, right: str = "") -> str:
        gap = inner - len(left) - len(right) - 2
        return "│ " + left + " " * max(gap, 1) + right + " │"

    lines = [top, row(title), sep]
    for left, right in rows:
        lines.append(row(left, right))
    lines.append(sep)
    for line in footer:
        lines.append(row(line))
    lines.append(bot)
    return "\n".join(lines)


def pick_continents() -> list[str]:
    rows, footer = [], []
    for i, c in enumerate(CONTINENTS, 1):
        count = len(list_java_files(c))
        right = "— vazio" if count == 0 else f"{count} arquivo{'s' if count != 1 else ''}"
        rows.append((f"[{i}] {c}", right))
    footer.append("[A] Todos os continentes")
    footer.append("[0] Cancelar")
    print("\n" + _box("Continentes disponíveis em to_test:", rows, footer))

    while True:
        raw = input("\nEscolha: ").strip().upper()
        if raw == "0":
            return []
        if raw == "A":
            return [c for c in CONTINENTS if list_java_files(c)]
        parts = raw.split()
        chosen = []
        valid = True
        for p in parts:
            if p.isdigit() and 1 <= int(p) <= len(CONTINENTS):
                c = CONTINENTS[int(p) - 1]
                if c not in chosen:
                    chosen.append(c)
            else:
                print(f"  Opção inválida: '{p}'")
                valid = False
                break
        if valid and chosen:
            return chosen


def pick_files(continent: str) -> list[str]:
    files = list_java_files(continent)
    if not files:
        print(f"  Nenhum arquivo .java em '{continent}'.")
        return []

    print(f"\n  Arquivos em to_test/{continent}:")
    for i, f in enumerate(files, 1):
        print(f"    [{i:>3}] {f}")
    print(f"    [  A] Todos ({len(files)} arquivos)")
    print(f"    [  0] Pular este continente")

    while True:
        raw = input(f"  Mover quais arquivos de '{continent}'? : ").strip().upper()
        if raw == "0":
            return []
        if raw == "A":
            return files
        parts = raw.split()
        chosen = []
        valid = True
        for p in parts:
            if p.isdigit() and 1 <= int(p) <= len(files):
                f = files[int(p) - 1]
                if f not in chosen:
                    chosen.append(f)
            else:
                print(f"    Opção inválida: '{p}'")
                valid = False
                break
        if valid and chosen:
            return chosen


def confirm(moves: dict[str, list[str]]) -> bool:
    total = sum(len(v) for v in moves.values())
    print(f"\nResumo — {total} arquivo(s) a mover:")
    for continent, files in moves.items():
        print(f"  {continent}: {len(files)} arquivo(s)")
        for f in files:
            src_rel = os.path.relpath(os.path.join(SOURCE_BASE, continent, f), PROJECT_ROOT)
            dst_rel = os.path.relpath(os.path.join(DEST_BASE, continent, f), PROJECT_ROOT)
            print(f"    {src_rel}  →  {dst_rel}")
    answer = input("\nConfirmar? [s/N]: ").strip().lower()
    return answer == "s"


def main():
    print("=" * 60)
    print("  Mover firmas: to_test → firms")
    print("=" * 60)

    continents = pick_continents()
    if not continents:
        print("Operação cancelada.")
        return

    moves: dict[str, list[str]] = {c: list_java_files(c) for c in continents if list_java_files(c)}

    if not moves:
        print("Nenhum arquivo selecionado. Operação cancelada.")
        return

    if not confirm(moves):
        print("Operação cancelada.")
        return

    print()
    errors = []
    for continent, files in moves.items():
        for filename in files:
            try:
                dst = move_firm(filename, continent)
                print(f"  ✓  {filename}  ({continent})")
            except Exception as e:
                errors.append((filename, continent, str(e)))
                print(f"  ✗  {filename}  ({continent}) — ERRO: {e}")

    print()
    if errors:
        print(f"Concluído com {len(errors)} erro(s).")
    else:
        total = sum(len(v) for v in moves.values())
        print(f"Concluído. {total} arquivo(s) movido(s) com sucesso.")


if __name__ == "__main__":
    main()

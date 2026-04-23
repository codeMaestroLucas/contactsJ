import io
import os
import zipfile

# Script is 9 levels deep inside the project root (same logic as sort_builders.py).
_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

PACKAGES = {
    # ── Pacote 1: Hierarquia base de scraping ────────────────────────────────
    # Classes fundamentais que todo scraper herda ou usa diretamente.
    "01documentacao": [
        "core/src/main/java/org/example/src/entities/BaseSites/ByNewPage.java",
        "core/src/main/java/org/example/src/entities/BaseSites/ByPage.java",
        "core/src/main/java/org/example/src/entities/BaseSites/Site.java",
        "core/src/main/java/org/example/src/entities/BaseSites/SiteUtils.java",
        "core/src/main/java/org/example/src/entities/Lawyer.java",
        "core/src/main/java/org/example/src/entities/MyDriver.java",
        "core/src/main/java/org/example/src/utils/TreatLawyerParams.java",
        "core/src/main/java/org/example/src/utils/Validations.java",
    ],

    # ── Pacote 2: Utilitários de controle ────────────────────────────
    # Entidades Utilitários de rastreamento de execução.
    "02utils": [
        "core/src/main/java/org/example/src/utils/VCard.java",
        "core/src/main/java/org/example/src/utils/Extractor.java",
        "core/src/main/java/org/example/src/utils/ErrorLogger.java",
    ],

    # ── Pacote 3: Runner, config & exceções ──────────────────────────────────
    # Ponto de entrada, configuração global, orquestração e exceções.
    "03runner_config": [
        "firms/src/main/java/org/example/Main.java",
        "firms/src/main/java/org/example/src/utils/myInterface/FirmsBuilder.java",
        "core/src/main/java/org/example/src/CONFIG.java",
        "core/src/main/java/org/example/src/utils/ContinentConfig.java",
        "core/src/main/java/org/example/exceptions/LawyerExceptions.java",
        "core/src/main/java/org/example/exceptions/ValidationExceptions.java",
        "core/src/main/resources/instructions/map.md",
        "core/src/main/resources/baseFiles/json/continentsConfig.json",
        "core/src/main/resources/baseFiles/json/countryAliases.json",
        "core/src/main/resources/instructions/countryNameConventions.md",
    ],
}

# Files that share the same basename across packages need a custom archive name.
# Key: relative path (from PROJECT_ROOT) → name to use inside the zip.
ARCNAME_OVERRIDES = {
    "firms/src/main/java/org/example/src/utils/myInterface/FirmsBuilder.java": "FirmsBuilder.java",
    "firms/src/main/java/org/example/src/utils/myInterface/CompletedFirms.java": "CompletedFirms.java",
    "core/src/main/java/org/example/src/utils/myInterface/MyInterfaceUtls.java": "MyInterfaceUtls.java",
    "core/src/main/java/org/example/src/utils/validation/EmailDuplicateChecker.java": "EmailDuplicateChecker.java",
}

DOWNLOADS = os.path.join(os.path.expanduser("~"), "Downloads")
OUTPUT = os.path.join(DOWNLOADS, "documentação.zip")


def build_inner_zip(files: list[str]) -> bytes:
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zf:
        for rel_path in files:
            abs_path = os.path.join(PROJECT_ROOT, rel_path)
            if not os.path.exists(abs_path):
                print(f"  WARNING: file not found — {rel_path}")
                continue
            arcname = ARCNAME_OVERRIDES.get(rel_path, os.path.basename(rel_path))
            zf.write(abs_path, arcname=arcname)
    return buf.getvalue()


def main():
    with zipfile.ZipFile(OUTPUT, "w", zipfile.ZIP_DEFLATED) as outer:
        for package_name, files in PACKAGES.items():
            print(f"Packing {package_name}.zip ({len(files)} files)...")
            inner_bytes = build_inner_zip(files)
            outer.writestr(f"{package_name}.zip", inner_bytes)

    print(f"\nCreated: {OUTPUT}")


if __name__ == "__main__":
    main()

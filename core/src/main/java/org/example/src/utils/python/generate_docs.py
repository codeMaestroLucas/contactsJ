import io
import os
import zipfile

# Script is 9 levels deep inside the project root (same logic as sort_builders.py).
_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

PACKAGES = {
    # ── Documentação principal ────────────────────────────────────────────────
    # Base da hierarquia, entidades centrais, README e diagrama de arquitetura.
    "01documentacao": [
        "core/src/main/java/org/example/src/entities/BaseSites/Site.java",
        "core/src/main/java/org/example/src/entities/BaseSites/ByPage.java",
        "core/src/main/java/org/example/src/entities/BaseSites/ByNewPage.java",
        "core/src/main/java/org/example/src/entities/BaseSites/SiteUtils.java",
        "core/src/main/java/org/example/src/entities/Lawyer.java",
        "core/src/main/java/org/example/src/entities/MyDriver.java",
        "core/src/main/java/org/example/src/utils/Extractor.java",
        "core/src/main/java/org/example/src/utils/Validations.java",
        "core/src/main/java/org/example/src/CONFIG.java",
        "README.md",
    ],
    # ── Orquestração e configuração de continentes ────────────────────────────
    # Builder unificado (arrays por continente), CompletedFirms (menu CLI +
    # peso de continentes), ContinentConfig, e JSONs de configuração.
    "02orquestracao": [
        "firms/src/main/java/org/example/src/utils/myInterface/FirmsBuilder.java",
        "firms/src/main/java/org/example/src/utils/myInterface/CompletedFirms.java",
        "core/src/main/java/org/example/src/utils/ContinentConfig.java",
        "firms/src/main/java/org/example/Main.java",
        "core/src/main/resources/baseFiles/json/continentsConfig.json",
        "core/src/main/resources/baseFiles/json/countriesToAvoidPermanent.json",
        "core/src/main/java/org/example/src/utils/EmailOfMonth.java",
        "core/src/main/resources/baseFiles/json/countriesToAvoidTemporary.json"
    ],
    # ── Utilitários e scripts Python ──────────────────────────────────────────
    # Utils Java (validação, extração, logging) + scripts de manutenção Python.
    "03utils": [
        "core/src/main/java/org/example/src/utils/ErrorLogger.java",
        "core/src/main/java/org/example/src/utils/FirmsOMonth.java",
        "core/src/main/java/org/example/src/utils/NoSleep.java",
        "core/src/main/java/org/example/src/utils/TreatLawyerParams.java",
        "core/src/main/resources/baseFiles/json/countryAliases.json",
        "core/src/main/resources/baseFiles/json/practiceAreas.json",
        "core/src/main/resources/instructions/countryNameConventions.md",
        "mermaidStructure.txt",
    ],
    # ── Exemplos de firmas (ByPage strategy) ─────────────────────────────────
    "04exemplosByPage": [
        "firms/src/main/java/org/example/src/sites/europe/Roschier.java",
        "firms/src/main/java/org/example/src/sites/mundial/TaylorWessing.java",
        "firms/src/main/java/org/example/src/sites/europe/Aera.java",
        "firms/src/main/java/org/example/src/sites/mundial/AlTamimi.java",
        "firms/src/main/java/org/example/src/sites/americas/Andersen.java",
        "firms/src/main/java/org/example/src/sites/mundial/Blakes.java",
        "firms/src/main/java/org/example/src/sites/americas/ClarkWilson.java",
        "firms/src/main/java/org/example/src/sites/europe/DechertLLP.java",
        "firms/src/main/java/org/example/src/sites/europe/HannesSnellman.java",
        "firms/src/main/java/org/example/src/sites/mundial/Kinstellar.java",
    ],
    # ── Exemplos de firmas (ByNewPage strategy) ───────────────────────────────
    "05exemplosByNewPage": [
        "firms/src/main/java/org/example/src/sites/europe/SchellenbergWittmer.java",
        "firms/src/main/java/org/example/src/sites/oceania/Gadens.java",
        "firms/src/main/java/org/example/src/sites/europe/ABGIP.java",
        "firms/src/main/java/org/example/src/sites/mundial/ALGoodbody.java",
        "firms/src/main/java/org/example/src/sites/americas/ArthurCox.java",
        "firms/src/main/java/org/example/src/sites/americas/AsafoAndCo.java",
        "firms/src/main/java/org/example/src/sites/europe/Borenius.java",
        "firms/src/main/java/org/example/src/sites/europe/BurgesSalmon.java",
        "firms/src/main/java/org/example/src/sites/africa/ENSAfrica.java",
        "firms/src/main/java/org/example/src/sites/mundial/Ellex.java",
    ],
}

# Files that share the same basename across packages need a custom archive name.
# Key: relative path (from PROJECT_ROOT) → name to use inside the zip.
ARCNAME_OVERRIDES = {}

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

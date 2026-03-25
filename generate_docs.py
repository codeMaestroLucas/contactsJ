import io
import os
import zipfile

BASE = os.path.dirname(os.path.abspath(__file__))

PACKAGES = {
    "01documentacao": [
        "src/main/java/org/example/src/entities/BaseSites/Site.java",
        "src/main/java/org/example/src/entities/BaseSites/ByPage.java",
        "src/main/java/org/example/src/entities/BaseSites/ByNewPage.java",
        "src/main/java/org/example/src/entities/BaseSites/SiteUtils.java",
        "src/main/java/org/example/src/entities/Lawyer.java",
        "src/main/java/org/example/src/entities/MyDriver.java",
        "src/main/java/org/example/src/utils/Extractor.java",
        "src/main/java/org/example/src/utils/Validations.java",
        "src/main/java/org/example/src/CONFIG.java",
        "README.md",
    ],
    "02maisDocumentacao": [
        "src/main/java/org/example/src/utils/myInterface/ByPageFirmsBuilder.java",
        "src/main/java/org/example/src/utils/myInterface/ByNewPageFirmsBuilder.java",
        "src/main/java/org/example/Main.java",
        "src/main/resources/baseFiles/json/continentsConfig.json",
        "src/main/resources/baseFiles/json/countriesToAvoidPermanent.json",
        "src/main/resources/baseFiles/json/countriesToAvoidTemporary.json",
        "src/main/resources/baseFiles/json/countryAliases.json",
        "src/main/resources/baseFiles/json/practiceAreas.json",
        "src/main/resources/instructions/countryNameConventions.md",
        "mermaidStructure.txt",
    ],
    "03maisDocumentação" : [
        "src/main/java/org/example/src/utils/ContinentConfig.java",
         "src/main/java/org/example/src/utils/EmailOfMonth.java",
         "src/main/java/org/example/src/utils/ErrorLogger.java",
         "src/main/java/org/example/src/utils/Extractor.java",
         "src/main/java/org/example/src/utils/FirmsOMonth.java",
         "src/main/java/org/example/src/utils/NoSleep.java",
         "src/main/java/org/example/src/utils/TimeCalculator.java",
         "src/main/java/org/example/src/utils/TreatLawyerParams.java",
         "src/main/java/org/example/src/utils/Validations.java"
    ],
    "04exemplosByPage": [
        "src/main/java/org/example/src/sites/byPage/Roschier.java",
        "src/main/java/org/example/src/sites/byPage/TaylorWessing.java",
        "src/main/java/org/example/src/sites/byPage/Aera.java",
        "src/main/java/org/example/src/sites/byPage/AlTamimi.java",
        "src/main/java/org/example/src/sites/byPage/Andersen.java",
        "src/main/java/org/example/src/sites/byPage/Blakes.java",
        "src/main/java/org/example/src/sites/byPage/ClarkWilson.java",
        "src/main/java/org/example/src/sites/byPage/DechertLLP.java",
        "src/main/java/org/example/src/sites/byPage/HannesSnellman.java",
        "src/main/java/org/example/src/sites/byPage/Kinstellar.java",
    ],
    "05exemplosByNewPage": [
        "src/main/java/org/example/src/sites/byNewPage/SchellenbergWittmer.java",
        "src/main/java/org/example/src/sites/byNewPage/Gadens.java",
        "src/main/java/org/example/src/sites/byNewPage/ABGIP.java",
        "src/main/java/org/example/src/sites/byNewPage/ALGoodbody.java",
        "src/main/java/org/example/src/sites/byNewPage/ArthurCox.java",
        "src/main/java/org/example/src/sites/byNewPage/AsafoAndCo.java",
        "src/main/java/org/example/src/sites/byNewPage/Borenius.java",
        "src/main/java/org/example/src/sites/byNewPage/BurgesSalmon.java",
        "src/main/java/org/example/src/sites/byNewPage/ENSAfrica.java",
        "src/main/java/org/example/src/sites/byNewPage/Ellex.java",
    ],
}

# Files that share the same basename across packages need a custom archive name.
# Key: relative path → name to use inside the zip.
ARCNAME_OVERRIDES = {
    "src/main/java/org/example/src/sites/byPage/_Template.java": "_Template_byPage.java",
    "src/main/java/org/example/src/sites/byNewPage/_Template.java": "_Template_byNewPage.java",
}

DOWNLOADS = os.path.join(os.path.expanduser("~"), "Downloads")
OUTPUT = os.path.join(DOWNLOADS, "documentação.zip")


def build_inner_zip(files: list[str]) -> bytes:
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zf:
        for rel_path in files:
            abs_path = os.path.join(BASE, rel_path)
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

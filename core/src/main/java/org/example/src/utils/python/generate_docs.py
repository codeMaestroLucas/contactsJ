import io
import os
import zipfile

# Script is 9 levels deep inside the project root.
_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

FILES = [
    "core/src/main/resources/instructions/promptFirma.txt",
    "core/src/main/java/org/example/src/entities/Lawyer.java",
    "core/src/main/java/org/example/src/entities/BaseSites/ByPage.java",
    "core/src/main/java/org/example/src/entities/BaseSites/ByNewPage.java",
    "firms/src/main/java/org/example/src/sites/americas/RodrigoEliasMedrano.java",
    "firms/src/main/java/org/example/src/sites/europe/ADVANTAltana.java"
]

DOWNLOADS = os.path.join(os.path.expanduser("~"), "Downloads")
OUTPUT    = os.path.join(DOWNLOADS, "firma_context.zip")


def main():
    with zipfile.ZipFile(OUTPUT, "w", zipfile.ZIP_DEFLATED) as zf:
        for rel_path in FILES:
            abs_path = os.path.join(PROJECT_ROOT, rel_path)
            if not os.path.exists(abs_path):
                print(f"  WARNING: file not found — {rel_path}")
                continue
            arcname = os.path.basename(rel_path)
            zf.write(abs_path, arcname=arcname)
            print(f"  + {arcname}")

    print(f"\nCreated: {OUTPUT}")


if __name__ == "__main__":
    main()

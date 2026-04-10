#!/usr/bin/env python3
"""
check_todos.py

Verifies which firms in the TODO JSON files have already been implemented as
.java files, and removes duplicates (by URL) within each JSON file.

Steps:
  1. Scan all .java files in the ByPage and ByNewPage dirs to collect known URLs.
  2. For each JSON file in todos/:
     a. Remove intra-file URL duplicates (keep first occurrence).
     b. Remove entries whose URL matches a known Java implementation URL.
  3. Print a summary.
"""

import json
import os
import re

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

_SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = _SCRIPT_DIR
for _ in range(9):
    PROJECT_ROOT = os.path.dirname(PROJECT_ROOT)

TODO_DIR          = os.path.join(PROJECT_ROOT, "core/src/main/resources/todos")
FIRMS_BYPAGE_DIR  = os.path.join(PROJECT_ROOT, "firms/src/main/java/org/example/src/sites/byPage")
FIRMS_BYNEWPAGE_DIR = os.path.join(PROJECT_ROOT, "core/src/main/java/org/example/src/sites/to_test")

JSON_FILES = ["byPage.json", "byNewPage.json", "uncategorized.json"]

# Matches the URL (2nd string arg) inside super("Name", "https://...", ...)
_URL_RE = re.compile(r'super\s*\(\s*"[^"]*"\s*,\s*"(https?://[^"]+)"')

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def collect_java_urls(*dirs):
    """Walk directories recursively and extract URLs from .java constructors."""
    urls = set()
    for directory in dirs:
        if not os.path.isdir(directory):
            print(f"  [WARN] Directory not found: {directory}")
            continue
        for root, _, files in os.walk(directory):
            for fname in files:
                if not fname.endswith(".java"):
                    continue
                path = os.path.join(root, fname)
                with open(path, encoding="utf-8") as f:
                    content = f.read()
                for match in _URL_RE.findall(content):
                    urls.add(match.strip())
    return urls


def is_valid(obj):
    """True if the object is a real firm entry (has non-empty name and link)."""
    return (
        isinstance(obj, dict)
        and isinstance(obj.get("name"), str) and obj["name"].strip()
        and isinstance(obj.get("link"), str) and obj["link"].strip()
    )


def url_matches(json_link, java_urls):
    """True if json_link equals, contains, or is contained by any Java URL."""
    for java_url in java_urls:
        if java_url == json_link or java_url in json_link or json_link in java_url:
            return True
    return False


def write_json_with_sep_spacing(f, data):
    """Write a JSON array where 'sep' objects are surrounded by 2 blank lines."""
    f.write('[\n')
    for i, obj in enumerate(data):
        is_sep = isinstance(obj, dict) and 'sep' in obj
        is_last = i == len(data) - 1

        if is_sep:
            f.write('\n\n')

        obj_str = json.dumps(obj, ensure_ascii=False, indent=2)
        indented = '\n'.join('  ' + line for line in obj_str.split('\n'))

        f.write(indented + (',' if not is_last else '') + '\n')

        if is_sep:
            f.write('\n\n')

    f.write(']')


def process_file(path, java_urls):
    """
    Load a JSON array, remove duplicate and implemented entries, save, and
    return (initial_valid, dup_removed, impl_removed).
    """
    with open(path, encoding="utf-8") as f:
        data = json.load(f)

    initial_valid = sum(1 for obj in data if is_valid(obj))

    seen_urls     = set()
    dup_removed   = 0
    impl_removed  = 0
    cleaned       = []

    for obj in data:
        if not is_valid(obj):
            # Separator / empty placeholder — keep as-is
            cleaned.append(obj)
            continue

        link = obj["link"].strip()

        # 1. Intra-file duplicate check
        if link in seen_urls:
            dup_removed += 1
            continue

        seen_urls.add(link)

        # 2. Already-implemented check
        if url_matches(link, java_urls):
            impl_removed += 1
            continue

        cleaned.append(obj)

    with open(path, "w", encoding="utf-8") as f:
        write_json_with_sep_spacing(f, cleaned)

    return initial_valid, dup_removed, impl_removed


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    print("Scanning Java implementations...")
    java_urls = collect_java_urls(FIRMS_BYPAGE_DIR, FIRMS_BYNEWPAGE_DIR)
    print(f"  Found {len(java_urls)} URLs across Java files.\n")

    total_removed = 0
    results = []

    for filename in JSON_FILES:
        path = os.path.join(TODO_DIR, filename)
        if not os.path.isfile(path):
            print(f"  [WARN] File not found: {path}")
            continue

        try:
            initial, dups, impl = process_file(path, java_urls)
        except json.JSONDecodeError as e:
            print(f"  [ERROR] {filename} has invalid JSON and was skipped: {e}")
            results.append((filename, -1, 0, 0))
            continue

        removed = dups + impl
        total_removed += removed
        results.append((filename, initial, dups, impl))

    # Summary
    print("=" * 40)
    print("SUMMARY")
    print("=" * 40)
    for filename, initial, dups, impl in results:
        print(f"\n{filename}")
        if initial == -1:
            print(f"  [SKIPPED — invalid JSON, fix manually]")
            continue
        removed  = dups + impl
        after    = initial - removed
        print(f"  Valid entries (before):          {initial}")
        print(f"  Valid entries (after):           {after}")
        print(f"  Removed (intra-file duplicates): {dups}")
        print(f"  Removed (already implemented):   {impl}")

    print(f"\nTOTAL removed: {total_removed}")


if __name__ == "__main__":
    main()

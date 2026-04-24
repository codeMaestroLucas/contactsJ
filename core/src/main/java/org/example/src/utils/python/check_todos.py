#!/usr/bin/env python3
"""
check_todos.py

Verifies which firms in the TODO JSON files have already been implemented as
.java files, and removes duplicates (by URL) within each JSON file.

Steps:
  1. Scan all .java files in the continent dirs (africa, asia, europe, americas,
     oceania, mundial) and in to_test to collect known URLs.
  2. For each JSON file in todos/:
     a. Remove intra-file URL duplicates (keep first occurrence).
     b. Remove entries whose URL exactly matches a known Java implementation URL.
     c. Move entries whose domain matches a known Java domain (but URL differs)
        to a dedicated "Similar" section at the bottom of the file.
  3. Print a summary.
"""

import json
import os
import re
from urllib.parse import urlparse

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

_SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = _SCRIPT_DIR
for _ in range(9):
    PROJECT_ROOT = os.path.dirname(PROJECT_ROOT)

TODO_DIR = os.path.join(PROJECT_ROOT, "core/src/main/resources/todos")

_FIRMS_SITES = os.path.join(PROJECT_ROOT, "firms/src/main/java/org/example/src/sites")
FIRMS_CONTINENT_DIRS = [
    os.path.join(_FIRMS_SITES, continent)
    for continent in ("africa", "asia", "europe", "americas", "oceania", "mundial")
]
# Also scan to_test (staging firms not yet in production)
TO_TEST_DIR = os.path.join(PROJECT_ROOT, "core/src/main/java/org/example/src/sites/to_test")

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


_SIMILAR_SEP      = "=== Similar (possibly already implemented) ==="
_SIMILAR_TODO_SEP = "=== Similar (domain already present in this file) ==="


def _domain(url):
    """Return the normalized netloc of a URL, stripping leading 'www.'."""
    try:
        host = urlparse(url).netloc.lower()
        return host[4:] if host.startswith("www.") else host
    except Exception:
        return ""


def url_exact_matches(json_link, java_urls):
    """True if json_link equals, contains, or is contained by any Java URL."""
    for java_url in java_urls:
        if java_url == json_link or java_url in json_link or json_link in java_url:
            return True
    return False


def url_domain_matches(json_link, java_domains):
    """True if json_link shares a domain with any known Java implementation."""
    d = _domain(json_link)
    return bool(d) and d in java_domains


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
    Load a JSON array, remove duplicate and implemented entries, move
    domain-similar entries to a dedicated section, save, and return
    (initial_valid, dup_removed, impl_removed, similar_moved).
    """
    with open(path, encoding="utf-8") as f:
        data = json.load(f)

    _SIMILAR_SEPS = {_SIMILAR_SEP, _SIMILAR_TODO_SEP}

    # Strip Similar sections written by previous runs so they are recomputed.
    in_similar = False
    stripped = []
    for obj in data:
        if isinstance(obj, dict) and obj.get("sep") in _SIMILAR_SEPS:
            in_similar = True
            continue
        if in_similar:
            if isinstance(obj, dict) and "sep" in obj and obj.get("sep") not in _SIMILAR_SEPS:
                in_similar = False
                stripped.append(obj)
            # Otherwise skip — entry gets re-evaluated below.
        else:
            stripped.append(obj)
    data = stripped

    java_domains  = {_domain(u) for u in java_urls if _domain(u)}
    initial_valid = sum(1 for obj in data if is_valid(obj))

    seen_urls    = set()
    seen_domains = set()
    dup_removed  = 0
    impl_removed = 0
    similar_java = []   # domain matches a Java implementation
    similar_todo = []   # domain matches another entry in the same file
    cleaned      = []

    for obj in data:
        if not is_valid(obj):
            cleaned.append(obj)
            continue

        link   = obj["link"].strip()
        domain = _domain(link)

        # 1. Intra-file exact duplicate
        if link in seen_urls:
            dup_removed += 1
            continue
        seen_urls.add(link)

        # 2. Exact / substring match against Java → already implemented, remove
        if url_exact_matches(link, java_urls):
            impl_removed += 1
            continue

        # 3. Domain matches a Java implementation → Similar (java)
        if url_domain_matches(link, java_domains):
            similar_java.append(obj)
            seen_domains.add(domain)
            continue

        # 4. Domain already seen within this file → Similar (intra-todo)
        if domain and domain in seen_domains:
            similar_todo.append(obj)
            continue

        if domain:
            seen_domains.add(domain)
        cleaned.append(obj)

    if similar_java:
        cleaned.append({"sep": _SIMILAR_SEP})
        cleaned.extend(similar_java)
    if similar_todo:
        cleaned.append({"sep": _SIMILAR_TODO_SEP})
        cleaned.extend(similar_todo)

    with open(path, "w", encoding="utf-8") as f:
        write_json_with_sep_spacing(f, cleaned)

    return initial_valid, dup_removed, impl_removed, len(similar_java), len(similar_todo)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    print("Scanning Java implementations...")
    java_urls = collect_java_urls(*FIRMS_CONTINENT_DIRS, TO_TEST_DIR)
    print(f"  Found {len(java_urls)} URLs across Java files.\n")

    total_removed = 0
    results = []

    for filename in JSON_FILES:
        path = os.path.join(TODO_DIR, filename)
        if not os.path.isfile(path):
            print(f"  [WARN] File not found: {path}")
            continue

        try:
            initial, dups, impl, sim_java, sim_todo = process_file(path, java_urls)
        except json.JSONDecodeError as e:
            print(f"  [ERROR] {filename} has invalid JSON and was skipped: {e}")
            results.append((filename, -1, 0, 0, 0, 0))
            continue

        removed = dups + impl
        total_removed += removed
        results.append((filename, initial, dups, impl, sim_java, sim_todo))

    # Summary
    print("=" * 40)
    print("SUMMARY")
    print("=" * 40)
    for filename, initial, dups, impl, sim_java, sim_todo in results:
        print(f"\n{filename}")
        if initial == -1:
            print(f"  [SKIPPED — invalid JSON, fix manually]")
            continue
        removed = dups + impl
        after   = initial - removed - sim_java - sim_todo
        print(f"  Valid entries (before):                    {initial}")
        print(f"  Valid entries kept:                        {after}")
        print(f"  Moved to Similar (matches Java impl):      {sim_java}")
        print(f"  Moved to Similar (domain dup in file):     {sim_todo}")
        print(f"  Removed (intra-file exact duplicates):     {dups}")
        print(f"  Removed (already implemented):             {impl}")

    print(f"\nTOTAL removed: {total_removed}")


if __name__ == "__main__":
    main()

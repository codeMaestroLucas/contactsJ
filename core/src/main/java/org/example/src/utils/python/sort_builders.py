#!/usr/bin/env python3
"""
Sorts firm entries alphabetically within each continent array in:
  - ByPageFirmsBuilder.java
  - ByNewPageFirmsBuilder.java

Rules:
  - Firms are sorted alphabetically (case-insensitive) by class name
  - Precisely 5 firms per line
  - Commented-out firms (// new ClassName()) are sorted together with active ones
    but each occupies its own line (cannot be safely mixed mid-line)
  - Section/structural comments (e.g. // ByPage - Africa) are discarded
  - Continental divisions and all other code outside the arrays are untouched
"""

import re
import os

# ── Configuration ─────────────────────────────────────────────────────────────

ITEMS_PER_LINE = 5
ENTRY_INDENT   = "            "   # 12 spaces — matches existing file style

# Arrays whose contents must NOT be sorted (preserved exactly as written)
SKIP_ARRAYS = {"TEST"}

# Script is at: core/src/main/java/org/example/src/utils/python/
# Project root is 9 levels up.
_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

BUILDER_FILES = [
    os.path.join(
        PROJECT_ROOT,
        "firms/src/main/java/org/example/src/utils/myInterface/ByPageFirmsBuilder.java",
    ),
    os.path.join(
        PROJECT_ROOT,
        "firms/src/main/java/org/example/src/utils/myInterface/ByNewPageFirmsBuilder.java",
    ),
]

# ── Helpers ───────────────────────────────────────────────────────────────────

_CLASS_RE    = re.compile(r'new\s+(\w+)\s*\(\)')
_ACTIVE_RE   = re.compile(r'new\s+\w+\s*\(\)')
_COMMENTED_RE = re.compile(r'//\s*new\s+\w+\s*\(\)')


def _class_name(text: str) -> str:
    """Return the class name from 'new ClassName()' (with or without leading //)."""
    m = _CLASS_RE.search(text)
    return m.group(1) if m else text


def _parse_entries(body: str) -> list[tuple[str, str, bool]]:
    """
    Scan *body* (the text between { and }) and return every firm entry as:
        (sort_key, canonical_entry, is_commented)

    - Active entries  → captured as 'new ClassName()'
    - Commented firms → captured as '// new ClassName()'
    - Structural/section comments (e.g. '// ByPage - Africa') are ignored.
    - Trailing inline comments after an active entry (e.g. '// new site coming soon')
      that do NOT start with 'new' are ignored.
    """
    entries: list[tuple[str, str, bool]] = []

    for line in body.splitlines():
        stripped = line.strip()
        if not stripped:
            continue

        if stripped.startswith("//"):
            # Could be a structural comment or a commented-out firm.
            # A commented firm matches: // new ClassName()
            m = _COMMENTED_RE.match(stripped)
            if m:
                raw = m.group(0)
                entries.append((_class_name(raw).lower(), raw, True))
            # else: structural comment → skip
        else:
            # Active line — may contain several 'new ClassName()' entries
            for m in _ACTIVE_RE.finditer(stripped):
                raw = m.group(0)
                entries.append((_class_name(raw).lower(), raw, False))

    return entries


def _format_entries(entries: list[tuple[str, str, bool]]) -> str:
    """
    Produce the sorted, formatted array body.

    Active entries are grouped ITEMS_PER_LINE per line.
    Each commented entry is placed on its own line (at its sorted position),
    flushing any buffered active entries first.
    """
    if not entries:
        return "\n"

    lines: list[str] = []
    active_buf: list[str] = []

    def flush():
        for i in range(0, len(active_buf), ITEMS_PER_LINE):
            chunk = active_buf[i : i + ITEMS_PER_LINE]
            lines.append(ENTRY_INDENT + " ".join(e + "," for e in chunk))
        active_buf.clear()

    for _, raw, commented in entries:
        if commented:
            flush()
            lines.append(ENTRY_INDENT + raw + ",")
        else:
            active_buf.append(raw)

    flush()

    return "\n" + "\n".join(lines) + "\n"


# ── Core processor ────────────────────────────────────────────────────────────

_ARRAY_RE = re.compile(
    r"([ \t]*private static final Site\[\] (\w+) = \{)"  # group 1: opening line, group 2: array name
    r"(.*?)"                                               # group 3: body
    r"([ \t]*\};)",                                        # group 4: closing
    re.DOTALL,
)


def _sort_block(match: re.Match) -> str:
    opening    = match.group(1)
    array_name = match.group(2)
    body       = match.group(3)
    closing    = match.group(4)

    # Leave skipped arrays untouched
    if array_name in SKIP_ARRAYS:
        return match.group(0)

    entries = _parse_entries(body)

    if not entries:
        return match.group(0)  # leave empty arrays as-is

    entries.sort(key=lambda t: t[0])  # sort by lower-case class name
    return opening + _format_entries(entries) + closing


def process_file(path: str) -> None:
    print(f"\nProcessing: {os.path.relpath(path, PROJECT_ROOT)}")

    with open(path, encoding="utf-8") as fh:
        original = fh.read()

    updated = _ARRAY_RE.sub(_sort_block, original)

    if updated == original:
        print("  — Already sorted, no changes written.")
        return

    with open(path, "w", encoding="utf-8") as fh:
        fh.write(updated)

    # Report how many arrays were touched (compare full match strings)
    original_arrays = _ARRAY_RE.findall(original)
    updated_arrays  = _ARRAY_RE.findall(updated)
    changed = sum(o != u for o, u in zip(original_arrays, updated_arrays))
    changed -= sum(1 for o, u in zip(original_arrays, updated_arrays) if o[1] in SKIP_ARRAYS)
    print(f"  ✓ {changed} array(s) re-sorted and saved.")


# ── Entry point ───────────────────────────────────────────────────────────────

def main() -> None:
    print("=" * 60)
    print("  Firm Builder Sorter")
    print("=" * 60)

    for path in BUILDER_FILES:
        if not os.path.exists(path):
            print(f"\n[ERROR] File not found: {path}")
            continue
        process_file(path)

    print("\nDone.")


if __name__ == "__main__":
    main()

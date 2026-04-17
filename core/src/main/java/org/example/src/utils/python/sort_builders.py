#!/usr/bin/env python3
"""
Sorts firm entries alphabetically within arrays in:
  - FirmsBuilder.java

Rules:
  - Firms are sorted alphabetically (case-insensitive) by class name
  - Precisely 5 firms per line
  - Commented-out firms (// new ClassName()) are sorted together with active ones
    but each occupies its own line (cannot be safely mixed mid-line)
  - Structural/separator comments that are not firm entries are discarded
  - Continental divisions and all other code outside the arrays are untouched
  - TEST array is NOT touched
  - AMERICAS array is sorted within each sub-section (// North America,
    // Central America, // South America) — sub-section order is preserved
"""

import re
import os

# ── Configuration ─────────────────────────────────────────────────────────────

ITEMS_PER_LINE = 5
ENTRY_INDENT   = "            "   # 12 spaces — matches existing file style

# Arrays whose contents must NOT be sorted by run_sort_builders()
SKIP_ARRAYS = {"TEST"}

# Arrays that contain named sub-sections (// North America, etc.) whose
# relative order must be preserved; entries are sorted WITHIN each sub-section.
SECTION_SORTED_ARRAYS = {"AMERICAS"}

# Script is at: core/src/main/java/org/example/src/utils/python/
# Project root is 9 levels up.
_SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(_SCRIPT_DIR, *[".."] * 9))

BUILDER_FILES = [
    os.path.join(
        PROJECT_ROOT,
        "firms/src/main/java/org/example/src/utils/myInterface/FirmsBuilder.java",
    ),
]

# ── Shared regexes ────────────────────────────────────────────────────────────

_CLASS_RE     = re.compile(r'new\s+(\w+)\s*\(\)')
_ACTIVE_RE    = re.compile(r'new\s+\w+\s*\(\)')
_COMMENTED_RE = re.compile(r'//\s*new\s+\w+\s*\(\)')

# Captures sub-section headers inside the AMERICAS array
# (// North America, // Central America, // South America).
_SECTION_HEADER_RE = re.compile(
    r'^([ \t]*//\s*(?:North America|Central America|South America))$',
    re.MULTILINE
)

# Trailing inline comment that is NOT a commented-out firm entry
_TRAILING_COMMENT_RE = re.compile(r'\s*(//(?!\s*new\s+\w+\s*\(\)).*)$')

# Full array block
_ARRAY_RE = re.compile(
    r"([ \t]*private static final Site\[\] (\w+) = \{)"  # group 1: opening, group 2: name
    r"(.*?)"                                               # group 3: body
    r"([ \t]*\};)",                                        # group 4: closing
    re.DOTALL,
)


# ── Helpers ───────────────────────────────────────────────────────────────────

def _class_name(text: str) -> str:
    """Return the class name from 'new ClassName()' (with or without leading //)."""
    m = _CLASS_RE.search(text)
    return m.group(1) if m else text


def _parse_entries(body: str) -> list[tuple[str, str, bool]]:
    """
    Scan *body* and return every firm entry as:
        (sort_key, canonical_entry, is_commented)

    Structural/section comments are ignored.
    Trailing inline comments after an active entry are ignored.
    """
    entries: list[tuple[str, str, bool]] = []

    for line in body.splitlines():
        stripped = line.strip()
        if not stripped:
            continue

        if stripped.startswith("//"):
            m = _COMMENTED_RE.match(stripped)
            if m:
                raw = m.group(0)
                entries.append((_class_name(raw).lower(), raw, False))
        else:
            for m in _ACTIVE_RE.finditer(stripped):
                raw = m.group(0)
                entries.append((_class_name(raw).lower(), raw, False))

    return entries


def _parse_entries_with_trailing(body: str) -> list[tuple[str, str, bool, str]]:
    """
    Like _parse_entries but also captures inline comments attached to a firm entry.
    Returns: (sort_key, canonical_entry, is_commented, trailing_comment)

    Example input line:
        new PoswaIncorporated(), // new site coming soon
    Result:
        ('poswaincorporated', 'new PoswaIncorporated()', False, '// new site coming soon')
    """
    entries: list[tuple[str, str, bool, str]] = []

    for line in body.splitlines():
        stripped = line.strip()
        if not stripped:
            continue

        if stripped.startswith("//"):
            m = _COMMENTED_RE.match(stripped)
            if m:
                raw = m.group(0)
                entries.append((_class_name(raw).lower(), raw, True, ""))
            # else: section header — caller handles it
        else:
            matches = list(_ACTIVE_RE.finditer(stripped))
            if not matches:
                continue

            # Check for a trailing inline comment after the last firm on this line
            last_end = matches[-1].end()
            remainder = stripped[last_end:]
            trailing_m = _TRAILING_COMMENT_RE.match(remainder)
            trailing = trailing_m.group(1) if trailing_m else ""

            for i, m in enumerate(matches):
                raw = m.group(0)
                # Attach trailing comment only to the last firm on the line
                t = trailing if i == len(matches) - 1 else ""
                entries.append((_class_name(raw).lower(), raw, False, t))

    return entries


def _format_entries(entries: list[tuple[str, str, bool]]) -> str:
    """
    Format (sort_key, canonical_entry, is_commented) entries.
    Active entries: grouped ITEMS_PER_LINE per line.
    Commented entries: own line (flush active buffer first).
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


def _format_entries_with_trailing(entries: list[tuple[str, str, bool, str]]) -> str:
    """
    Like _format_entries but handles trailing inline comments.
    An entry with a non-empty trailing_comment is placed on its own line.
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

    for _, raw, commented, trailing in entries:
        if commented:
            flush()
            lines.append(ENTRY_INDENT + raw + ",")
        elif trailing:
            flush()
            lines.append(ENTRY_INDENT + raw + ", " + trailing)
        else:
            active_buf.append(raw)

    flush()
    return "\n" + "\n".join(lines) + "\n"


# ── Opção 1: Ordenar Builders ─────────────────────────────────────────────────

def _sort_block(match: re.Match) -> str:
    opening    = match.group(1)
    array_name = match.group(2)
    body       = match.group(3)
    closing    = match.group(4)

    if array_name in SKIP_ARRAYS:
        return match.group(0)

    if array_name in SECTION_SORTED_ARRAYS:
        return opening + _sort_test_body(body) + closing

    entries = _parse_entries(body)
    if not entries:
        return match.group(0)

    entries.sort(key=lambda t: t[0])
    return opening + _format_entries(entries) + closing


def _process_file(path: str) -> None:
    print(f"\nProcessing: {os.path.relpath(path, PROJECT_ROOT)}")

    with open(path, encoding="utf-8") as fh:
        original = fh.read()

    updated = _ARRAY_RE.sub(_sort_block, original)

    if updated == original:
        print("  — Already sorted, no changes written.")
        return

    with open(path, "w", encoding="utf-8") as fh:
        fh.write(updated)

    original_arrays = _ARRAY_RE.findall(original)
    updated_arrays  = _ARRAY_RE.findall(updated)
    changed = sum(
        o != u and o[1] not in SKIP_ARRAYS
        for o, u in zip(original_arrays, updated_arrays)
    )
    print(f"  ✓ {changed} array(s) re-sorted and saved.")


def run_sort_builders() -> None:
    print("\n── Ordenando Builders ──")
    for path in BUILDER_FILES:
        if not os.path.exists(path):
            print(f"\n[ERROR] File not found: {path}")
            continue
        _process_file(path)


# ── Section-aware sort (used internally for AMERICAS) ─────────────────────────

def _sort_test_body(body: str) -> str:
    """
    Split the AMERICAS array body into its sub-sections
    (// North America, // Central America, // South America),
    merge duplicate headers, sort entries within each sub-section, and reassemble.
    """
    # re.split with a capturing group keeps the delimiters in the result list.
    # Result alternates: [pre_text, header1, block1, header2, block2, ...]
    parts = _SECTION_HEADER_RE.split(body)

    pre_text = parts[0]

    # Collect entries grouped by header, preserving first-seen order
    seen_order: list[str] = []
    sections: dict[str, list] = {}

    i = 1
    while i < len(parts):
        header = parts[i]
        block  = parts[i + 1] if i + 1 < len(parts) else ""

        entries = _parse_entries_with_trailing(block)

        if header not in sections:
            seen_order.append(header)
            sections[header] = []
        sections[header].extend(entries)

        i += 2

    output_parts: list[str] = [pre_text]

    for header in seen_order:
        entries = sections[header]
        entries.sort(key=lambda t: t[0])
        formatted = _format_entries_with_trailing(entries) if entries else "\n"
        output_parts.append("\n" + header)
        output_parts.append(formatted)

    return "".join(output_parts)


# ── Entry point ───────────────────────────────────────────────────────────────

if __name__ == "__main__":
    run_sort_builders()

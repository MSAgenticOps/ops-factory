#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Reduce unnecessary blank lines in Java code.

Rules:
1. Blank lines between attributes, constructors, methods, nested classes, static blocks
2. No consecutive blank lines inside methods, type definitions, initialization expressions
3. No 3 or more consecutive blank lines
4. No blank lines at start/end of code blocks (within braces)
"""

import re
import sys
from pathlib import Path


def reduce_blank_lines(content: str) -> str:
    """Reduce unnecessary blank lines according to the rules."""
    lines = content.split('\n')
    result = []
    i = 0
    n = len(lines)

    while i < n:
        line = lines[i]
        stripped = line.strip()

        # Count consecutive blank lines
        if stripped == '':
            blank_count = 0
            j = i
            while j < n and lines[j].strip() == '':
                blank_count += 1
                j += 1

            # Rule 3: No 3 or more consecutive blank lines -> reduce to 1
            if blank_count >= 3:
                blank_count = 1

            # Check context: is this inside a code block?
            prev_non_blank = find_prev_non_blank(result)
            next_non_blank = find_next_non_blank(lines, i + blank_count)

            # Rule 4: No blank lines at start of code block (after '{')
            if prev_non_blank is not None and prev_non_blank.rstrip().endswith('{'):
                # Skip blank lines right after opening brace
                i = j
                continue

            # Rule 4: No blank lines at end of code block (before '}')
            if next_non_blank is not None and next_non_blank.strip().startswith('}'):
                # Skip blank lines right before closing brace
                i = j
                continue

            # Rule 2: Inside method/type/initialization - no consecutive blank lines
            if is_inside_code_block(result, lines, i):
                blank_count = min(blank_count, 1)

            # Add appropriate blank lines
            for _ in range(blank_count):
                result.append('')
            i = j
        else:
            result.append(line)
            i += 1

    return '\n'.join(result)


def find_prev_non_blank(result: list) -> str | None:
    """Find the previous non-blank line from result."""
    for i in range(len(result) - 1, -1, -1):
        if result[i].strip() != '':
            return result[i]
    return None


def find_next_non_blank(lines: list, start: int) -> str | None:
    """Find the next non-blank line starting from index start."""
    for i in range(start, len(lines)):
        if lines[i].strip() != '':
            return lines[i]
    return None


def is_inside_code_block(result: list, lines: list, current_idx: int) -> bool:
    """
    Determine if current position is inside a code block.
    A simple heuristic: count open braces before this point.
    """
    brace_count = 0

    # Count from result (processed lines)
    for line in result:
        brace_count += line.count('{') - line.count('}')

    return brace_count > 0


def reduce_blank_lines_v2(content: str) -> str:
    """
    Alternative implementation using regex-based approach.
    More precise for Java code structure.
    """
    # Rule 3: Replace 3+ consecutive blank lines with 2 blank lines (then 1)
    content = re.sub(r'\n\n\n+', '\n\n', content)

    lines = content.split('\n')
    result = []

    i = 0
    while i < len(lines):
        line = lines[i]
        result.append(line)

        # If current line ends with '{', check for blank line after
        if line.rstrip().endswith('{'):
            # Skip blank lines immediately after '{'
            j = i + 1
            while j < len(lines) and lines[j].strip() == '':
                j += 1
            # Check if next non-blank is '}' (empty block) or content
            if j < len(lines):
                # Rule 4: Remove blank lines after '{'
                i = j - 1  # Will be incremented to j at end of loop
            else:
                i = j - 1
        # If next non-blank line starts with '}', skip blank lines before it
        elif i + 1 < len(lines):
            # Count blank lines ahead
            j = i + 1
            while j < len(lines) and lines[j].strip() == '':
                j += 1
            if j < len(lines) and lines[j].strip().startswith('}'):
                # Rule 4: Skip blank lines before '}'
                i = j - 1
            elif j > i + 2:
                # Rule 2 & 3: Multiple blank lines -> reduce to 1
                result.append('')
                i = j - 1

        i += 1

    return '\n'.join(result)


def reduce_blank_lines_final(content: str) -> str:
    """
    Final implementation - clear and follows all rules exactly.
    """
    lines = content.split('\n')
    result = []
    i = 0

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if stripped != '':
            # Non-blank line - just add it
            result.append(line)
            i += 1
            continue

        # We have a blank line - count consecutive blanks
        blank_start = i
        while i < len(lines) and lines[i].strip() == '':
            i += 1
        blank_count = i - blank_start

        # Get context
        prev_line = result[-1] if result else ''
        next_line = lines[i] if i < len(lines) else ''

        prev_stripped = prev_line.strip()
        next_stripped = next_line.strip()

        # Rule 4: No blank line after opening brace '{'
        if prev_stripped.endswith('{'):
            continue

        # Rule 4: No blank line before closing brace '}'
        if next_stripped.startswith('}'):
            continue

        # Rule 4: No blank line before closing brace at same line (like '};')
        if next_stripped.startswith('};') or next_stripped == '}':
            continue

        # Rule 3: No 3+ consecutive blank lines
        if blank_count >= 3:
            blank_count = 1

        # Rule 2: Inside code block - no consecutive blank lines
        # Check if we're inside a block by counting braces
        open_braces = sum(ln.count('{') - ln.count('}') for ln in result)
        if open_braces > 0:
            blank_count = min(blank_count, 1)

        # Add the blank line(s)
        for _ in range(blank_count):
            result.append('')

    return '\n'.join(result)


def process_file(file_path: str, dry_run: bool = False) -> tuple[bool, str]:
    """
    Process a single file.

    Args:
        file_path: Path to the file
        dry_run: If True, don't modify the file, just return what would change

    Returns:
        Tuple of (modified, message)
    """
    path = Path(file_path)
    if not path.exists():
        return False, f"File not found: {file_path}"

    if not file_path.endswith('.java'):
        return False, f"Not a Java file: {file_path}"

    try:
        original = path.read_text(encoding='utf-8')
    except UnicodeDecodeError:
        original = path.read_text(encoding='gbk')

    modified = reduce_blank_lines_final(original)

    if original == modified:
        return False, f"No changes needed: {file_path}"

    if dry_run:
        return True, f"Would modify: {file_path}"

    path.write_text(modified, encoding='utf-8')
    return True, f"Modified: {file_path}"


def main():
    """Main entry point.

    Directory scanning behavior (aligned with codecheck skill):
    - Directory argument is REQUIRED
    - Relative directory: Scan specified directory recursively
    - Absolute directory: Scan specified directory recursively
    """
    import argparse

    parser = argparse.ArgumentParser(
        description='Reduce unnecessary blank lines in Java code'
    )
    parser.add_argument(
        'files',
        nargs='+',
        help='Files or directories to process (REQUIRED)'
    )
    parser.add_argument(
        '-d', '--dry-run',
        action='store_true',
        help='Show what would be changed without modifying files'
    )
    parser.add_argument(
        '-r', '--recursive',
        action='store_true',
        default=True,
        help='Process directories recursively (default: True)'
    )
    parser.add_argument(
        '--no-recursive',
        action='store_true',
        help='Disable recursive directory scanning'
    )

    args = parser.parse_args()

    # Determine recursive mode
    recursive = args.recursive and not args.no_recursive

    files_to_process = []

    for f in args.files:
        path = Path(f)
        if path.is_file():
            files_to_process.append(str(path))
        elif path.is_dir():
            if recursive:
                files_to_process.extend(str(p) for p in path.rglob('*.java'))
            else:
                files_to_process.extend(str(p) for p in path.glob('*.java'))

    if not files_to_process:
        print("No Java files found to process.")
        return 1

    modified_count = 0
    for f in files_to_process:
        modified, msg = process_file(f, dry_run=args.dry_run)
        print(msg)
        if modified:
            modified_count += 1

    print(f"\nProcessed {len(files_to_process)} files, {modified_count} modified.")
    return 0


if __name__ == '__main__':
    sys.exit(main())

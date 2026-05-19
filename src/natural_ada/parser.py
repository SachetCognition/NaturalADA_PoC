"""Parser for Natural/Adabas mainframe programs."""

from __future__ import annotations

import re
from pathlib import Path

from natural_ada.models import (
    ProgramMode,
    SourceLine,
    Statement,
    StatementType,
)

# Structured mode: lines start with optional spaces then a 4-digit sequence number
STRUCTURED_LINE_RE = re.compile(r"^\s*(\d{4})\s(.*)$")

STATEMENT_KEYWORDS: dict[str, StatementType] = {
    "DEFINE DATA": StatementType.DEFINE_DATA,
    "END-DEFINE": StatementType.END_DEFINE,
    "END-READ": StatementType.END_READ,
    "END-IF": StatementType.END_IF,
    "END TRANSACTION": StatementType.END_TRANSACTION,
    "END-DECIDE": StatementType.END_DECIDE,
    "ESCAPE": StatementType.ESCAPE,
    "RESET": StatementType.RESET,
    "COMPRESS": StatementType.COMPRESS,
    "MOVE EDITED": StatementType.MOVE_EDITED,
    "MOVE": StatementType.MOVE,
    "WRITE": StatementType.WRITE,
    "STORE": StatementType.STORE,
    "UPDATE": StatementType.UPDATE,
    "DECIDE": StatementType.DECIDE,
    "VALUES": StatementType.VALUES,
    "NONE": StatementType.NONE_VALUE,
    "IGNORE": StatementType.IGNORE,
    "DOEND": StatementType.DOEND,
    "DO": StatementType.DO,
    "LOOP": StatementType.LOOP,
    "END": StatementType.END,
    "IF": StatementType.IF,
}


def detect_mode(lines: list[str]) -> ProgramMode:
    """Detect whether a program is Structured or Reporting mode."""
    for line in lines:
        stripped = line.strip()
        if STRUCTURED_LINE_RE.match(stripped):
            return ProgramMode.STRUCTURED
        if stripped and not stripped.startswith("*"):
            return ProgramMode.REPORTING
    return ProgramMode.REPORTING


def parse_source_lines(raw_text: str, mode: ProgramMode) -> list[SourceLine]:
    """Parse raw text into structured source lines."""
    result: list[SourceLine] = []
    line_num = 0
    for raw_line in raw_text.splitlines():
        line_num += 1
        if mode == ProgramMode.STRUCTURED:
            m = STRUCTURED_LINE_RE.match(raw_line)
            if m:
                seq = int(m.group(1))
                content = m.group(2).rstrip()
                result.append(SourceLine(line_num, seq, content, raw_line))
            else:
                result.append(SourceLine(line_num, None, raw_line.rstrip(), raw_line))
        else:
            result.append(SourceLine(line_num, None, raw_line.rstrip(), raw_line))
    return result


def classify_statement(content: str) -> StatementType:
    """Classify a statement line by its leading keyword."""
    stripped = content.strip()
    if not stripped or stripped.startswith("*"):
        return StatementType.COMMENT

    upper = stripped.upper()

    # Check for labeled statements (e.g., "R1. READ ...")
    label_match = re.match(r"^[A-Z][A-Z0-9]*\.\s+(.+)$", upper)
    if label_match:
        upper = label_match.group(1)

    # Check for VIEW definition
    if "VIEW OF" in upper:
        return StatementType.VIEW

    # Check for level-prefixed variable definitions (e.g., "1 #VAR(A16)")
    if re.match(r"^\d+\s+", upper):
        if "REDEFINE" in upper:
            return StatementType.REDEFINE
        if "VIEW OF" in upper:
            return StatementType.VIEW
        return StatementType.VARIABLE

    # Check for REDEFINE at start
    if upper.startswith("REDEFINE"):
        return StatementType.REDEFINE

    # Check for assignment with :=
    if ":=" in stripped:
        return StatementType.ASSIGNMENT

    # Check for READ with label pattern
    if re.match(r"^READ\b", upper):
        return StatementType.READ

    if re.match(r"^FIND\b", upper):
        return StatementType.FIND

    if re.match(r"^GET\b", upper):
        return StatementType.GET

    # Multi-word keywords first
    for keyword, stmt_type in STATEMENT_KEYWORDS.items():
        if upper.startswith(keyword):
            return stmt_type

    return StatementType.OTHER


def parse_statements(source_lines: list[SourceLine]) -> list[Statement]:
    """Parse source lines into classified statements."""
    statements: list[Statement] = []
    for sl in source_lines:
        content = sl.content.strip()
        if not content:
            continue
        stmt_type = classify_statement(content)
        statements.append(Statement(stmt_type, content, sl.line_number, sl.sequence_number))
    return statements


def parse_file(file_path: str | Path) -> tuple[ProgramMode, list[SourceLine], list[Statement]]:
    """Parse a Natural program file and return mode, source lines, and statements."""
    path = Path(file_path)
    raw_text = path.read_text(encoding="utf-8", errors="replace")
    lines = raw_text.splitlines()
    mode = detect_mode(lines)
    source_lines = parse_source_lines(raw_text, mode)
    statements = parse_statements(source_lines)
    return mode, source_lines, statements

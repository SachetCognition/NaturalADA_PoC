"""Tests for the Natural/Adabas parser."""

from __future__ import annotations

from pathlib import Path

import pytest

from natural_ada.models import ProgramMode, StatementType
from natural_ada.parser import (
    classify_statement,
    detect_mode,
    parse_file,
    parse_source_lines,
    parse_statements,
)

PROGRAMS_DIR = Path(__file__).resolve().parent.parent / "programs"


class TestDetectMode:
    def test_structured_mode(self) -> None:
        lines = [
            "   0010 *",
            "   0020 DEFINE DATA LOCAL",
        ]
        assert detect_mode(lines) == ProgramMode.STRUCTURED

    def test_reporting_mode(self) -> None:
        lines = [
            "***************",
            "RESET #VAR(N8)",
        ]
        assert detect_mode(lines) == ProgramMode.REPORTING

    def test_empty_returns_reporting(self) -> None:
        assert detect_mode([]) == ProgramMode.REPORTING


class TestClassifyStatement:
    @pytest.mark.parametrize(
        "content, expected",
        [
            ("*  comment line", StatementType.COMMENT),
            ("", StatementType.COMMENT),
            ("DEFINE DATA LOCAL", StatementType.DEFINE_DATA),
            ("END-DEFINE", StatementType.END_DEFINE),
            ("END-READ", StatementType.END_READ),
            ("END-IF", StatementType.END_IF),
            ("END TRANSACTION", StatementType.END_TRANSACTION),
            ("ESCAPE TOP END-IF", StatementType.ESCAPE),
            ("ESCAPE BOTTOM", StatementType.ESCAPE),
            ("RESET #S1 #S2", StatementType.RESET),
            ("WRITE WORK FILE 1 VAR", StatementType.WRITE),
            ("STORE CCF", StatementType.STORE),
            ("IF TABLE-TYPE NE 150", StatementType.IF),
            ("1 CENT-CODES-FILE2 VIEW OF CENT-CODES-FILE2", StatementType.VIEW),
            ("1 #COV-SUPER(A16)", StatementType.VARIABLE),
            ("1 REDEFINE #COV-SUPER", StatementType.REDEFINE),
            ("#DATE-2015-MAR := 20150331", StatementType.ASSIGNMENT),
            ("MOVE 150 TO #C-TAB", StatementType.MOVE),
            ("COMPRESS '10' #BCH INTO #KEY LEAVING NO SPACE", StatementType.COMPRESS),
            ("DECIDE ON FIRST VALUES ENTRY-TYPE", StatementType.DECIDE),
            ("END-DECIDE", StatementType.END_DECIDE),
            ("DOEND", StatementType.DOEND),
            ("LOOP(AC.)", StatementType.LOOP),
            ("END", StatementType.END),
        ],
    )
    def test_classification(self, content: str, expected: StatementType) -> None:
        assert classify_statement(content) == expected

    def test_labeled_read(self) -> None:
        stmt = "R1. READ CENT-CODES-FILE2 BY COVER-CODE-SUPER"
        assert classify_statement(stmt) == StatementType.READ

    def test_labeled_find(self) -> None:
        stmt = "AG. FIND AGENT-CONTROLS WITH MAIN-AGT-KEY = #KEY"
        assert classify_statement(stmt) == StatementType.FIND


class TestParseSourceLines:
    def test_structured_lines(self) -> None:
        text = "   0010 *  comment\n   0020 DEFINE DATA LOCAL\n"
        lines = parse_source_lines(text, ProgramMode.STRUCTURED)
        assert len(lines) == 2
        assert lines[0].sequence_number == 10
        assert lines[0].content == "*  comment"
        assert lines[1].sequence_number == 20

    def test_reporting_lines(self) -> None:
        text = "* comment\nRESET #VAR(N8)\n"
        lines = parse_source_lines(text, ProgramMode.REPORTING)
        assert len(lines) == 2
        assert lines[0].sequence_number is None
        assert lines[0].content == "* comment"


class TestParseStatements:
    def test_filters_empty_lines(self) -> None:
        lines = parse_source_lines("\n\n* comment\n", ProgramMode.REPORTING)
        stmts = parse_statements(lines)
        assert len(stmts) == 1
        assert stmts[0].stmt_type == StatementType.COMMENT


class TestParseFile:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_structured_mode_file(self) -> None:
        mode, source_lines, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        assert mode == ProgramMode.STRUCTURED
        assert len(source_lines) > 0
        assert len(statements) > 0
        stmt_types = {s.stmt_type for s in statements}
        assert StatementType.DEFINE_DATA in stmt_types
        assert StatementType.END_DEFINE in stmt_types

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "reporting_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_reporting_mode_file(self) -> None:
        mode, source_lines, statements = parse_file(PROGRAMS_DIR / "reporting_mode.nat")
        assert mode == ProgramMode.REPORTING
        assert len(source_lines) > 0
        assert len(statements) > 0

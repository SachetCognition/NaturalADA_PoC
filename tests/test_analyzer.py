"""Tests for the Natural/Adabas analyzer."""

from __future__ import annotations

from pathlib import Path

import pytest

from natural_ada.analyzer import (
    analyze_file,
    extract_operations,
    extract_variables_reporting,
    extract_variables_structured,
    extract_views,
)
from natural_ada.models import OperationType, ProgramMode
from natural_ada.parser import parse_file

PROGRAMS_DIR = Path(__file__).resolve().parent.parent / "programs"


class TestExtractVariablesStructured:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_extracts_variables(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        variables = extract_variables_structured(statements)
        assert len(variables) > 0
        var_names = [v.name for v in variables]
        assert "#COV-SUPER" in var_names
        assert "#DATE-2015-MAR" in var_names
        assert "#DATE-9999" in var_names

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_variable_types(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        variables = extract_variables_structured(statements)
        cov_super = next((v for v in variables if v.name == "#COV-SUPER"), None)
        assert cov_super is not None
        assert cov_super.data_type == "A16"


class TestExtractVariablesReporting:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "reporting_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_extracts_variables(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "reporting_mode.nat")
        variables = extract_variables_reporting(statements)
        assert len(variables) > 0
        var_names = [v.name for v in variables]
        assert "#EDATE" in var_names or "#DEB" in var_names

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "reporting_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_typed_variables(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "reporting_mode.nat")
        variables = extract_variables_reporting(statements)
        typed = [v for v in variables if v.data_type is not None]
        assert len(typed) > 0


class TestExtractViews:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_extracts_views(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        views = extract_views(statements)
        assert len(views) >= 1
        view_names = [v.name for v in views]
        assert "CENT-CODES-FILE2" in view_names

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_view_fields(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        views = extract_views(statements)
        ccf2 = next((v for v in views if v.name == "CENT-CODES-FILE2"), None)
        assert ccf2 is not None
        assert len(ccf2.fields) > 0
        assert "TABLE-TYPE" in ccf2.fields


class TestExtractOperations:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_structured_operations(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        ops = extract_operations(statements)
        assert len(ops) > 0
        op_types = [o.operation for o in ops]
        assert OperationType.READ in op_types

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_labeled_operations(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "structured_mode.nat")
        ops = extract_operations(statements)
        labeled = [o for o in ops if o.label is not None]
        assert len(labeled) > 0
        labels = [o.label for o in labeled]
        assert "R1" in labels

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "reporting_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_reporting_operations(self) -> None:
        _, _, statements = parse_file(PROGRAMS_DIR / "reporting_mode.nat")
        ops = extract_operations(statements)
        assert len(ops) > 0


class TestAnalyzeFile:
    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "structured_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_structured_analysis(self) -> None:
        analysis = analyze_file(PROGRAMS_DIR / "structured_mode.nat")
        assert analysis.mode == ProgramMode.STRUCTURED
        assert analysis.file_name == "structured_mode.nat"
        assert len(analysis.variables) > 0
        assert len(analysis.views) > 0
        assert len(analysis.operations) > 0
        assert len(analysis.statements) > 0
        assert len(analysis.source_lines) > 0

    @pytest.mark.skipif(
        not (PROGRAMS_DIR / "reporting_mode.nat").exists(),
        reason="Program file not found",
    )
    def test_reporting_analysis(self) -> None:
        analysis = analyze_file(PROGRAMS_DIR / "reporting_mode.nat")
        assert analysis.mode == ProgramMode.REPORTING
        assert analysis.file_name == "reporting_mode.nat"
        assert len(analysis.variables) > 0
        assert len(analysis.operations) > 0
        assert len(analysis.source_lines) > 0

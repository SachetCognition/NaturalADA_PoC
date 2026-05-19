"""Analyzer for extracting metadata from parsed Natural/Adabas programs."""

from __future__ import annotations

import re
from pathlib import Path

from natural_ada.models import (
    DatabaseOperation,
    DatabaseView,
    OperationType,
    ProgramAnalysis,
    StatementType,
    Variable,
)
from natural_ada.parser import parse_file

# Pattern for variable declarations: level name(type)
VAR_DECL_RE = re.compile(
    r"^(\d+)\s+"
    r"(#?[\w-]+)"
    r"(?:\(([^)]+)\))?"
)

# Pattern for VIEW OF declarations
VIEW_RE = re.compile(
    r"^(\d+)\s+"
    r"([\w-]+)\s+"
    r"VIEW\s+OF\s+"
    r"([\w-]+)",
    re.IGNORECASE,
)

# Pattern for labeled operations (e.g., "R1. READ CENT-CODES-FILE2 BY ...")
LABELED_OP_RE = re.compile(
    r"^([A-Z][A-Z0-9]*)\.\s+"
    r"(READ|FIND|GET|UPDATE|STORE)\s+"
    r"(?:MULTI-FETCH\s+\d+\s+)?"
    r"([\w-]+)"
    r"(?:\s+(?:BY|WITH|FROM)\s+([\w-]+))?",
    re.IGNORECASE,
)

# Pattern for unlabeled operations
UNLABELED_OP_RE = re.compile(
    r"^(READ|FIND|GET|UPDATE|STORE)\s+"
    r"(?:MULTI-FETCH\s+\d+\s+)?"
    r"([\w-]+)"
    r"(?:\s+(?:BY|WITH|FROM)\s+([\w-]+))?",
    re.IGNORECASE,
)

# Reporting mode variable declaration: RESET followed by variable list
RESET_VARS_RE = re.compile(r"^RESET\s+(.+)$", re.IGNORECASE)

# Reporting mode variable with type: #NAME(TYPE)
REPORTING_VAR_RE = re.compile(r"(#[\w-]+)\(([^)]+)\)")

OP_MAP: dict[str, OperationType] = {
    "READ": OperationType.READ,
    "FIND": OperationType.FIND,
    "GET": OperationType.GET,
    "UPDATE": OperationType.UPDATE,
    "STORE": OperationType.STORE,
}


def extract_variables_structured(statements: list) -> list[Variable]:
    """Extract variables from structured mode DEFINE DATA block."""
    variables: list[Variable] = []
    in_define = False
    current_parents: dict[int, str] = {}

    for stmt in statements:
        if stmt.stmt_type == StatementType.DEFINE_DATA:
            in_define = True
            continue
        if stmt.stmt_type == StatementType.END_DEFINE:
            in_define = False
            continue
        if not in_define:
            continue
        if stmt.stmt_type == StatementType.COMMENT:
            continue

        content = stmt.content.strip()

        # Check for VIEW
        view_match = VIEW_RE.match(content)
        if view_match:
            level = int(view_match.group(1))
            current_parents[level] = view_match.group(2)
            continue

        # Check for REDEFINE
        if stmt.stmt_type == StatementType.REDEFINE:
            var_match = VAR_DECL_RE.match(content)
            if var_match:
                level = int(var_match.group(1))
                name = var_match.group(2)
                dtype = var_match.group(3)
                parent = current_parents.get(level - 1)
                variables.append(
                    Variable(
                        name=name,
                        level=level,
                        data_type=dtype,
                        parent=parent,
                        redefined_from=name,
                    )
                )
                current_parents[level] = name
            continue

        # Regular variable
        var_match = VAR_DECL_RE.match(content)
        if var_match:
            level = int(var_match.group(1))
            name = var_match.group(2)
            dtype = var_match.group(3)
            is_array = bool(dtype and ":" in dtype) if dtype else False
            array_bounds = None
            if is_array and dtype:
                arr_match = re.search(r"\((\d+:\d+)\)", f"({dtype})")
                if arr_match:
                    array_bounds = arr_match.group(1)
            parent = current_parents.get(level - 1)
            variables.append(
                Variable(
                    name=name,
                    level=level,
                    data_type=dtype,
                    parent=parent,
                    is_array=is_array,
                    array_bounds=array_bounds,
                )
            )
            current_parents[level] = name

    return variables


def extract_variables_reporting(statements: list) -> list[Variable]:
    """Extract variables from reporting mode RESET declarations."""
    variables: list[Variable] = []
    seen: set[str] = set()
    collecting_reset = False
    reset_tokens: list[str] = []

    for stmt in statements:
        content = stmt.content.strip()
        if stmt.stmt_type == StatementType.COMMENT:
            continue

        if content.upper().startswith("RESET "):
            collecting_reset = True
            rest = content[6:].strip()
            reset_tokens.extend(rest.split())
            continue

        if collecting_reset:
            if content.startswith(" ") or content.startswith("#"):
                reset_tokens.extend(content.split())
                continue
            else:
                collecting_reset = False
                for token in reset_tokens:
                    token = token.strip().rstrip(",")
                    if not token:
                        continue
                    m = REPORTING_VAR_RE.match(token)
                    if m:
                        name, dtype = m.group(1), m.group(2)
                        if name not in seen:
                            seen.add(name)
                            variables.append(Variable(name=name, level=1, data_type=dtype))
                    elif token.startswith("#") and token not in seen:
                        seen.add(token)
                        variables.append(Variable(name=token, level=1))
                reset_tokens = []

    # Process remaining tokens
    if reset_tokens:
        for token in reset_tokens:
            token = token.strip().rstrip(",")
            if not token:
                continue
            m = REPORTING_VAR_RE.match(token)
            if m:
                name, dtype = m.group(1), m.group(2)
                if name not in seen:
                    seen.add(name)
                    variables.append(Variable(name=name, level=1, data_type=dtype))
            elif token.startswith("#") and token not in seen:
                seen.add(token)
                variables.append(Variable(name=token, level=1))

    return variables


def extract_views(statements: list) -> list[DatabaseView]:
    """Extract database views from statements."""
    views: list[DatabaseView] = []
    current_view: DatabaseView | None = None

    for stmt in statements:
        if stmt.stmt_type == StatementType.COMMENT:
            continue
        content = stmt.content.strip()
        view_match = VIEW_RE.match(content)
        if view_match:
            if current_view is not None:
                views.append(current_view)
            current_view = DatabaseView(
                name=view_match.group(2),
                file_name=view_match.group(3),
            )
            continue

        if current_view is not None:
            var_match = VAR_DECL_RE.match(content)
            if var_match and int(var_match.group(1)) > 1:
                field_name = var_match.group(2)
                current_view.fields.append(field_name)
            elif stmt.stmt_type not in (StatementType.COMMENT, StatementType.VARIABLE):
                views.append(current_view)
                current_view = None

    if current_view is not None:
        views.append(current_view)

    return views


def extract_operations(statements: list) -> list[DatabaseOperation]:
    """Extract database operations (READ, FIND, GET, UPDATE, STORE)."""
    operations: list[DatabaseOperation] = []

    for stmt in statements:
        if stmt.stmt_type == StatementType.COMMENT:
            continue
        content = stmt.content.strip()

        # Check for labeled operations
        labeled_match = LABELED_OP_RE.match(content)
        if labeled_match:
            op_type = OP_MAP.get(labeled_match.group(2).upper())
            if op_type:
                operations.append(
                    DatabaseOperation(
                        operation=op_type,
                        label=labeled_match.group(1),
                        target=labeled_match.group(3),
                        descriptor=labeled_match.group(4),
                        line_number=stmt.line_number,
                    )
                )
            continue

        # Check for unlabeled operations
        unlabeled_match = UNLABELED_OP_RE.match(content)
        if unlabeled_match:
            op_type = OP_MAP.get(unlabeled_match.group(1).upper())
            if op_type:
                operations.append(
                    DatabaseOperation(
                        operation=op_type,
                        target=unlabeled_match.group(2),
                        descriptor=unlabeled_match.group(3),
                        line_number=stmt.line_number,
                    )
                )
            continue

        # Check for END TRANSACTION
        if content.upper().startswith("END TRANSACTION"):
            operations.append(
                DatabaseOperation(
                    operation=OperationType.END_TRANSACTION,
                    line_number=stmt.line_number,
                )
            )

        # Check for UPDATE/STORE as inline references
        if stmt.stmt_type == StatementType.UPDATE:
            operations.append(
                DatabaseOperation(
                    operation=OperationType.UPDATE,
                    target=content.split("(")[0].replace("UPDATE", "").strip() or None,
                    line_number=stmt.line_number,
                )
            )
        elif stmt.stmt_type == StatementType.STORE:
            parts = content.upper().split()
            target = parts[1] if len(parts) > 1 else None
            operations.append(
                DatabaseOperation(
                    operation=OperationType.STORE,
                    target=target,
                    line_number=stmt.line_number,
                )
            )

    return operations


def analyze_file(file_path: str | Path) -> ProgramAnalysis:
    """Analyze a Natural program file and return full analysis."""
    path = Path(file_path)
    mode, source_lines, statements = parse_file(path)

    from natural_ada.models import ProgramMode

    if mode == ProgramMode.STRUCTURED:
        variables = extract_variables_structured(statements)
    else:
        variables = extract_variables_reporting(statements)

    views = extract_views(statements)
    operations = extract_operations(statements)

    return ProgramAnalysis(
        file_name=path.name,
        mode=mode,
        variables=variables,
        views=views,
        operations=operations,
        statements=statements,
        source_lines=source_lines,
    )

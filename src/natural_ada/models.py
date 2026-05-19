"""Data models for parsed Natural/Adabas programs."""

from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum


class StatementType(Enum):
    COMMENT = "COMMENT"
    DEFINE_DATA = "DEFINE_DATA"
    END_DEFINE = "END_DEFINE"
    VIEW = "VIEW"
    VARIABLE = "VARIABLE"
    REDEFINE = "REDEFINE"
    ASSIGNMENT = "ASSIGNMENT"
    MOVE = "MOVE"
    READ = "READ"
    FIND = "FIND"
    GET = "GET"
    UPDATE = "UPDATE"
    STORE = "STORE"
    END_TRANSACTION = "END_TRANSACTION"
    WRITE = "WRITE"
    IF = "IF"
    END_IF = "END_IF"
    ESCAPE = "ESCAPE"
    END_READ = "END_READ"
    RESET = "RESET"
    COMPRESS = "COMPRESS"
    DECIDE = "DECIDE"
    END_DECIDE = "END_DECIDE"
    VALUES = "VALUES"
    NONE_VALUE = "NONE_VALUE"
    IGNORE = "IGNORE"
    DO = "DO"
    DOEND = "DOEND"
    LOOP = "LOOP"
    END = "END"
    MOVE_EDITED = "MOVE_EDITED"
    OTHER = "OTHER"


class ProgramMode(Enum):
    STRUCTURED = "Structured"
    REPORTING = "Reporting"


class OperationType(Enum):
    READ = "READ"
    FIND = "FIND"
    GET = "GET"
    UPDATE = "UPDATE"
    STORE = "STORE"
    END_TRANSACTION = "END_TRANSACTION"


@dataclass
class SourceLine:
    line_number: int | None
    sequence_number: int | None
    content: str
    raw: str


@dataclass
class Variable:
    name: str
    level: int
    data_type: str | None = None
    parent: str | None = None
    is_array: bool = False
    array_bounds: str | None = None
    redefined_from: str | None = None


@dataclass
class DatabaseView:
    name: str
    file_name: str
    fields: list[str] = field(default_factory=list)


@dataclass
class DatabaseOperation:
    operation: OperationType
    label: str | None = None
    target: str | None = None
    descriptor: str | None = None
    line_number: int | None = None


@dataclass
class Statement:
    stmt_type: StatementType
    content: str
    line_number: int | None = None
    sequence_number: int | None = None


@dataclass
class ProgramAnalysis:
    file_name: str
    mode: ProgramMode
    variables: list[Variable] = field(default_factory=list)
    views: list[DatabaseView] = field(default_factory=list)
    operations: list[DatabaseOperation] = field(default_factory=list)
    statements: list[Statement] = field(default_factory=list)
    source_lines: list[SourceLine] = field(default_factory=list)

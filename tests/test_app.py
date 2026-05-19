"""Tests for the Flask web application."""

from __future__ import annotations

import pytest
from flask.testing import FlaskClient

from natural_ada.app import create_app


@pytest.fixture
def client() -> FlaskClient:
    app = create_app()
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


class TestHealthEndpoint:
    def test_health(self, client: FlaskClient) -> None:
        resp = client.get("/health")
        assert resp.status_code == 200
        data = resp.get_json()
        assert data is not None
        assert data["status"] == "ok"


class TestIndexPage:
    def test_index_loads(self, client: FlaskClient) -> None:
        resp = client.get("/")
        assert resp.status_code == 200
        assert b"Natural/Adabas Program Explorer" in resp.data

    def test_index_lists_programs(self, client: FlaskClient) -> None:
        resp = client.get("/")
        assert resp.status_code == 200
        assert b"structured_mode" in resp.data or b"reporting_mode" in resp.data


class TestDetailPage:
    def test_structured_detail(self, client: FlaskClient) -> None:
        resp = client.get("/program/structured_mode")
        assert resp.status_code == 200
        assert b"Variables" in resp.data
        assert b"Database Views" in resp.data
        assert b"Database Operations" in resp.data
        assert b"Source Code" in resp.data

    def test_reporting_detail(self, client: FlaskClient) -> None:
        resp = client.get("/program/reporting_mode")
        assert resp.status_code == 200
        assert b"Variables" in resp.data

    def test_404_for_missing(self, client: FlaskClient) -> None:
        resp = client.get("/program/nonexistent_program")
        assert resp.status_code == 404
        assert b"not found" in resp.data.lower()

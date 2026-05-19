"""Flask web application for exploring Natural/Adabas programs."""

from __future__ import annotations

from pathlib import Path

from flask import Flask, render_template

from natural_ada.analyzer import analyze_file
from natural_ada.models import ProgramAnalysis

PROGRAMS_DIR = Path(__file__).resolve().parent.parent.parent / "programs"


def create_app(programs_dir: Path | None = None) -> Flask:
    """Create and configure the Flask application."""
    app = Flask(
        __name__,
        template_folder=str(Path(__file__).resolve().parent.parent.parent / "templates"),
    )
    prog_dir = programs_dir or PROGRAMS_DIR

    @app.route("/")
    def index() -> str:
        programs: list[dict[str, str | ProgramAnalysis]] = []
        if prog_dir.exists():
            for p in sorted(prog_dir.iterdir()):
                if p.suffix in (".nat", ".txt"):
                    analysis = analyze_file(p)
                    programs.append(
                        {
                            "filename": p.name,
                            "stem": p.stem,
                            "analysis": analysis,
                        }
                    )
        return render_template("index.html", programs=programs)

    @app.route("/program/<name>")
    def program_detail(name: str) -> tuple[str, int] | str:
        candidates = list(prog_dir.glob(f"{name}.*"))
        if not candidates:
            return render_template("404.html", name=name), 404
        analysis = analyze_file(candidates[0])
        return render_template("detail.html", analysis=analysis, name=name)

    @app.route("/health")
    def health() -> dict[str, str]:
        return {"status": "ok"}

    return app


def main() -> None:
    """Run the development server."""
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)


if __name__ == "__main__":
    main()

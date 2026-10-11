#!/usr/bin/env python3
"""Apply every per-issue Reading Locale upsert to prod over a direct Postgres connection.

Usage:
  pip3 install "psycopg[binary]"
  DATABASE_URL='postgresql://...' python3 scripts/story-catalog/apply_i18n_prod.py

Files are idempotent upserts (ON CONFLICT DO UPDATE), so re-running is safe.
"""

import os
import sys
from pathlib import Path

import psycopg

CHUNKS = Path(__file__).resolve().parent / ".i18n_issue_chunks"


def main() -> int:
    url = os.environ.get("DATABASE_URL")
    if not url:
        print("Set DATABASE_URL (Supabase prod > Connect > Session pooler URI).", file=sys.stderr)
        return 2
    # Only the generated upserts: "{locale}__{slug}.sql".
    # Skip agent leftovers that start with "." or "mcp_" — those are JSON payloads.
    files = sorted(
        f for f in CHUNKS.glob("*__*.sql")
        if not f.name.startswith(".") and not f.name.startswith("mcp_")
    )
    failed = []
    with psycopg.connect(url, autocommit=True) as conn:
        for path in files:
            try:
                conn.execute(path.read_text(encoding="utf-8"))
                print(f"ok   {path.name}")
            except Exception as exc:
                failed.append(path.name)
                print(f"FAIL {path.name}: {exc}", file=sys.stderr)
        rows = conn.execute(
            "SELECT locale, COUNT(*) FROM story_chapter_i18n GROUP BY 1 ORDER BY 1"
        ).fetchall()
    print("\nchapter rows per locale (expect 150 each):")
    for locale, count in rows:
        print(f"  {locale:8} {count}")
    print(f"\n{len(files) - len(failed)}/{len(files)} files applied")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())

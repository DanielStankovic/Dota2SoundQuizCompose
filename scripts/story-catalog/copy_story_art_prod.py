#!/usr/bin/env python3
"""Copy the 22 story_art images from the public staging bucket to prod.

Run scripts/prod-rollout/story-art-bucket.sql in the prod SQL editor first.

  PROD_SERVICE_ROLE_KEY='<prod service_role secret>' \\
    python3 scripts/story-catalog/copy_story_art_prod.py

Key: Supabase prod → Project Settings → API → service_role. Do not commit it.
"""

import os
import sys
import urllib.error
import urllib.request

STAGING = "https://ljuylgejhmmtxulyfojd.supabase.co"
PROD = "https://tzmsnmqbajpgwcvnokgf.supabase.co"
BUCKET = "story_art"

SLUGS = [
    "invoker-three-wisps",
    "juggernaut-sixty-one-carvings",
    "lina-golden-rolling-pin",
    "marci-battle-speech",
    "pudge-butcher-of-blackwater",
    "queen-of-pain-coach-by-mistake",
    "riki-the-satyrs-strange-dota-match",
    "rubick-the-wooden-puppet",
    "sniper-eleven-bullets",
    "storm-spirit-handsome-devil",
    "tinker-the-cupboard-kids",
]
FILES = [(f"{slug}/cover.png", "image/png") for slug in SLUGS] + [
    (f"{slug}/backdrop.jpg", "image/jpeg") for slug in SLUGS
]


def main() -> int:
    key = os.environ.get("PROD_SERVICE_ROLE_KEY")
    if not key:
        print("Set PROD_SERVICE_ROLE_KEY (prod Project Settings → API → service_role).", file=sys.stderr)
        return 2
    failed = []
    for path, mime in FILES:
        src = f"{STAGING}/storage/v1/object/public/{BUCKET}/{path}"
        dest = f"{PROD}/storage/v1/object/{BUCKET}/{path}"
        try:
            with urllib.request.urlopen(src) as response:
                body = response.read()
            request = urllib.request.Request(
                dest,
                data=body,
                method="POST",
                headers={
                    "Authorization": f"Bearer {key}",
                    "apikey": key,
                    "Content-Type": mime,
                    "x-upsert": "true",
                },
            )
            with urllib.request.urlopen(request) as uploaded:
                uploaded.read()
            print(f"ok   {path} ({len(body)} bytes)")
        except urllib.error.HTTPError as exc:
            failed.append(path)
            detail = exc.read().decode("utf-8", errors="replace")
            print(f"FAIL {path}: HTTP {exc.code} {detail}", file=sys.stderr)
        except Exception as exc:
            failed.append(path)
            print(f"FAIL {path}: {exc}", file=sys.stderr)
    print(f"\n{len(FILES) - len(failed)}/{len(FILES)} files copied")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())

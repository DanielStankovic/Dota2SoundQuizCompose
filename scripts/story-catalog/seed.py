#!/usr/bin/env python3
"""Parse docs/story/*.md (no archive) into staging SQL for story_* English rows."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
STORY_DIR = ROOT / "docs" / "story"
OUT = Path(__file__).resolve().parent / "seed.sql"

PART_RE = re.compile(r"^## Part (\d+) — ", re.MULTILINE)

ISSUES = [
    {
        "slug": "lina-golden-rolling-pin",
        "file": "lina-golden-rolling-pin.md",
        "art": "lina",
        "caster": "Lina",
        "sort": 1,
        "title": "Lina and the Golden Rolling Pin",
        "synopsis": "A fire mage who cannot keep a job walks into Icewrack Valley and answers the last notice on the board: baker wanted, must like heat.",
    },
    {
        "slug": "juggernaut-sixty-one-carvings",
        "file": "juggernaut-sixty-one-carvings.md",
        "art": "juggernaut",
        "caster": "Juggernaut",
        "sort": 2,
        "title": "Juggernaut and the Sixty-One Carvings",
        "synopsis": "Yurnero washes up in a quiet bay and starts carving a mask, one cut a day, for a village that asked him to stay.",
    },
    {
        "slug": "pudge-butcher-of-blackwater",
        "file": "pudge-butcher-of-blackwater.md",
        "art": "pudge",
        "caster": "Pudge",
        "sort": 3,
        "title": "Pudge and the Butcher of Blackwater",
        "synopsis": "The gardener at Greywall boarding school is quiet, kind, and the last person you want to meet after dark.",
    },
    {
        "slug": "queen-of-pain-coach-by-mistake",
        "file": "queen-of-pain-coach-by-mistake.md",
        "art": "queen-of-pain",
        "caster": "Queen_of_Pain",
        "sort": 4,
        "title": "Coach by Mistake: The Demon of Amsterdam",
        "synopsis": "Akasha loses her last job, starts cleaning a brutal Amsterdam gym, and becomes its star trainer by accident.",
    },
    {
        "slug": "invoker-three-wisps",
        "file": "invoker-three-wisps.md",
        "art": "invoker",
        "caster": "Invoker",
        "sort": 5,
        "title": "Invoker and the Three Wisps",
        "synopsis": "A boy with no spark follows three wisps into the woods and learns that knowing everything is not the same as being ready.",
    },
    {
        "slug": "sniper-eleven-bullets",
        "file": "sniper-eleven-bullets.md",
        "art": "sniper",
        "caster": "Sniper",
        "sort": 6,
        "title": "Eleven Bullets",
        "synopsis": "A lone marksman, a village in a mountain pass, an army coming up the valley, and eleven bullets.",
    },
    {
        "slug": "tinker-the-cupboard-kids",
        "file": "tinker-the-cupboard-kids.md",
        "art": "tinker",
        "caster": "Tinker",
        "sort": 7,
        "title": "Tinker and the Cupboard Kids",
        "synopsis": "Three boys nobody wants, a club room that used to be a cupboard, and one night the whole school needs them.",
    },
    {
        "slug": "rubick-the-wooden-puppet",
        "file": "rubick-the-wooden-puppet.md",
        "art": "rubick",
        "caster": "Rubick",
        "sort": 8,
        "title": "Rubick the Wooden Puppet",
        "synopsis": "A wooden puppet is struck by green lightning, names himself by accident, and learns the world by copying it.",
    },
    {
        "slug": "riki-the-satyrs-strange-dota-match",
        "file": "riki-the-satyrs-strange-dota-match.md",
        "art": "riki",
        "caster": "Riki",
        "sort": 9,
        "title": "The Satyr's Strange Dota Match",
        "synopsis": "A satyr takes one gulp of cheap beer, the pub vanishes, and he wakes up in a match he does not understand.",
    },
    {
        "slug": "storm-spirit-handsome-devil",
        "file": "storm-spirit-handsome-devil.md",
        "art": "storm-spirit",
        "caster": "Storm_Spirit",
        "sort": 10,
        "title": "Who's That Handsome Devil?",
        "synopsis": "Raijin Thunderkeg sits down to dinner, certain the evening is going well, and discovers someone else is using his face.",
    },
    {
        "slug": "marci-battle-speech",
        "file": "marci-battle-speech.md",
        "art": "marci",
        "caster": "Marci",
        "sort": 11,
        "title": "Marci's Battle Speech",
        "synopsis": "Marci has a few words before the battle.",
    },
]


def parse_parts(markdown: str) -> list[tuple[int, str]]:
    matches = list(PART_RE.finditer(markdown))
    if not matches:
        raise SystemExit("no ## Part N headings")
    parts: list[tuple[int, str]] = []
    for i, match in enumerate(matches):
        index = int(match.group(1))
        start = match.end()
        eol = markdown.find("\n", start)
        start = eol + 1 if eol != -1 else start
        end = matches[i + 1].start() if i + 1 < len(matches) else len(markdown)
        body = markdown[start:end]
        index_at = body.find("\n## Part index")
        if index_at != -1:
            body = body[:index_at]
        body = body.strip()
        if body.endswith("---"):
            body = body[:-3].strip()
        parts.append((index, body))
    return parts


def dollar_quote(text: str) -> str:
    tag = "body"
    while f"${tag}$" in text:
        tag += "x"
    return f"${tag}${text}${tag}$"


def main() -> None:
    lines = [
        "-- Generated by scripts/story-catalog/seed.py. English only.",
        "BEGIN;",
        "",
    ]
    total_chapters = 0
    for issue in ISSUES:
        path = STORY_DIR / issue["file"]
        parts = parse_parts(path.read_text(encoding="utf-8"))
        indexes = [p[0] for p in parts]
        if indexes != list(range(1, len(parts) + 1)):
            raise SystemExit(f"{issue['slug']}: bad part indexes {indexes}")
        total_chapters += len(parts)
        cover = f"{issue['slug']}/cover.png"
        backdrop = f"{issue['slug']}/backdrop.jpg"
        title = dollar_quote(issue["title"])
        synopsis = dollar_quote(issue["synopsis"])
        lines += [
            f"-- {issue['title']} ({len(parts)} chapters)",
            "INSERT INTO public.story_issue",
            "  (slug, protagonist_caster_id, sort, cover_path, backdrop_path, active)",
            "SELECT",
            f"  {dollar_quote(issue['slug'])},",
            "  c.id,",
            f"  {issue['sort']},",
            f"  {dollar_quote(cover)},",
            f"  {dollar_quote(backdrop)},",
            "  true",
            "FROM public.casters c",
            f"WHERE c.name = {dollar_quote(issue['caster'])};",
            "",
            "INSERT INTO public.story_issue_i18n (issue_id, locale, title, synopsis)",
            "SELECT i.id, 'en',",
            f"  {title},",
            f"  {synopsis}",
            "FROM public.story_issue i",
            f"WHERE i.slug = {dollar_quote(issue['slug'])};",
            "",
        ]
        for index, body in parts:
            lines += [
                "INSERT INTO public.story_chapter (issue_id, chapter_index)",
                "SELECT i.id,",
                f"  {index}",
                "FROM public.story_issue i",
                f"WHERE i.slug = {dollar_quote(issue['slug'])};",
                "",
                "INSERT INTO public.story_chapter_i18n (chapter_id, locale, body)",
                "SELECT ch.id, 'en',",
                f"  {dollar_quote(body)}",
                "FROM public.story_chapter ch",
                "JOIN public.story_issue i ON i.id = ch.issue_id",
                f"WHERE i.slug = {dollar_quote(issue['slug'])} AND ch.chapter_index = {index};",
                "",
            ]
    if total_chapters != 150:
        raise SystemExit(f"expected 150 chapters, parsed {total_chapters}")
    lines += ["COMMIT;", ""]
    OUT.write_text("\n".join(lines), encoding="utf-8")
    print(f"wrote {OUT} ({total_chapters} chapters)")


if __name__ == "__main__":
    main()

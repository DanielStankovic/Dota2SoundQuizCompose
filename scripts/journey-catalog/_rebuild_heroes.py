#!/usr/bin/env python3
"""Rebuild LEVELS hero assignments: max 3 pair/roster repeats, all heroes used."""
from __future__ import annotations

import re
import subprocess
import sys
from collections import Counter, defaultdict
from itertools import combinations
from pathlib import Path

ROOT = Path(__file__).resolve().parent
catalog_path = ROOT / "catalog.py"
src = catalog_path.read_text()
ns: dict = {}
exec(
    src.split("HIGH_VOLUME")[0]
    + "\nHIGH_VOLUME = frozenset({'Invoker', 'Kez', 'Ringmaster'})\n",
    ns,
)
SC = ns["SOUND_COUNTS"]
HV = frozenset({"Invoker", "Kez", "Ringmaster"})
NON_HV = sorted([n for n in SC if n not in HV], key=lambda n: (SC[n], n))
LEGAL = {8, 12, 16, 20, 24, 28}

pair_lv: dict[tuple[str, str], list[int]] = defaultdict(list)
rost_lv: dict[tuple[str, ...], list[int]] = defaultdict(list)
seen: Counter[str] = Counter()
intro: set[str] = set()


def legal_total(heroes: list[str], level: int) -> int | None:
    c = sum(SC[h] for h in heroes)
    allowed = LEGAL if level >= 121 else LEGAL - {28}
    opts = [t for t in sorted(allowed) if t - c >= 2]
    return opts[-1] if opts else None


def pairs(hs: list[str]) -> list[tuple[str, str]]:
    hs = sorted(hs)
    return [(hs[i], hs[j]) for i in range(len(hs)) for j in range(i + 1, len(hs))]


def ok(heroes: list[str], level: int, *, all_fam: bool = False) -> bool:
    heroes = list(heroes)
    if len(heroes) != len(set(heroes)):
        return False
    if any(h not in SC for h in heroes):
        return False
    if any(h in HV and level < 106 for h in heroes):
        return False
    if all_fam and not set(heroes) <= intro:
        return False
    if len(rost_lv[tuple(sorted(heroes))]) >= 3:
        return False
    if any(len(pair_lv[p]) >= 3 for p in pairs(heroes)):
        return False
    return legal_total(heroes, level) is not None


def commit(heroes: list[str], level: int) -> None:
    rost_lv[tuple(sorted(heroes))].append(level)
    for p in pairs(heroes):
        pair_lv[p].append(level)
    for h in heroes:
        seen[h] += 1
        intro.add(h)


def pick(
    level: int,
    n: int,
    *,
    all_fam: bool = False,
    take_new: int = 0,
    require: list[str] | None = None,
) -> list[str]:
    require = list(require or [])
    need = n - len(require)
    fam = sorted(
        [h for h in NON_HV if h in intro and h not in require],
        key=lambda h: (seen[h], SC[h], h),
    )
    fresh = sorted(
        [h for h in NON_HV if h not in intro and h not in require],
        key=lambda h: (SC[h], h),
    )
    take_new = min(max(take_new, 0), need, len(fresh))
    if all_fam:
        for window in (30, 60, 120, len(fam)):
            for combo in combinations(fam[:window], need):
                hs = require + list(combo)
                if ok(hs, level, all_fam=True):
                    return hs
        raise RuntimeError(f"L{level} all_fam fail need={need} fam={len(fam)}")
    for nc in range(take_new, -1, -1):
        opts = list(combinations(fresh[: nc + 15], nc)) if nc else [()]
        for fc in opts[:1200]:
            rem = need - nc
            if rem == 0:
                hs = require + list(fc)
                if ok(hs, level):
                    return hs
                continue
            fill = [h for h in fam + fresh if h not in fc]
            for rc in combinations(fill[: rem + 40], rem):
                hs = require + list(fc) + list(rc)
                if ok(hs, level):
                    return hs
    raise RuntimeError(
        f"L{level} pick fail take_new={take_new} fresh={len(fresh)} fam={len(fam)}"
    )


EASY = {"Hidden Marks", "Blurred Vision", "Fragile Spirit"}
MH = {
    "Unknown Count",
    "Echo Limit",
    "Race Against Time",
    "Partial Veil",
    "Soundquake",
    "The Hidden Hero",
    "Blurred Vision 2",
    "Mirror Mode",
    "Sudden Death",
    "Among Heroes",
    "Soundquake Aftershock",
    "Soundquake Fracture",
    "Soundquake Cataclysm",
}
TW = {
    "Hidden Marks": (21, 22, 23),
    "Blurred Vision": (29, 30, 31),
    "Fragile Spirit": (36, 37, 38),
    "Mirror Mode": (40, 41, 42),
    "Sudden Death": (46,),
    "Unknown Count": (50, 51, 52),
    "Echo Limit": (56, 57, 58),
    "Race Against Time": (62, 63, 64),
    "Partial Veil": (70, 71, 72),
    "Soundquake": (78, 79, 80),
    "Soundquake Aftershock": (85, 86, 87),
    "The Hidden Hero": (92, 93, 94),
    "Among Heroes": (98, 99, 100),
    "Blurred Vision 2": (104, 105, 106),
    "Soundquake Fracture": (121, 122),
    "Soundquake Cataclysm": (125, 126),
}
teach = {lv for w in TW.values() for lv in w}

rows: list[dict] = []


def R(
    level: int,
    n: int,
    *aff: str,
    density: str = "medium",
    max_sounds: int | None = None,
    timer: int | None = None,
    echo: int = 5,
    role: str = "",
    mem: str | None = None,
    intro_n: int = 0,
    require: tuple[str, ...] = (),
) -> None:
    rows.append(
        dict(
            level=level,
            n=n,
            aff=aff,
            density=density,
            max_sounds=max_sounds,
            timer=timer,
            echo=echo,
            role=role,
            mem=mem,
            intro_n=intro_n,
            require=list(require),
        )
    )


for i, (n, d, ms, role) in enumerate(
    [
        (1, "light", 8, "primer"),
        (1, "light", 8, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "primer"),
        (1, "medium", 12, "guest decoys"),
        (1, "medium", 12, "guest decoys"),
        (2, "light", 12, "teach 2 Heroes"),
        (2, "light", 12, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "plateau"),
        (2, "medium", 16, "fat decoys"),
        (2, "medium", 20, "fat decoys"),
    ],
    1,
):
    R(i, n, density=d, max_sounds=ms, role=role, intro_n=n)

# level, n, affixes, density, role, intro_n, optional kwargs via trailing dict-like
SPECS: list[tuple] = [
    (21, 2, ("Hidden Marks",), "light", "teach Hidden Marks", 0, {}),
    (22, 2, ("Hidden Marks",), "light", "Hidden Marks mini-set", 0, {}),
    (23, 2, ("Hidden Marks",), "medium", "Hidden Marks mini-set", 0, {}),
    (24, 3, (), "light", "guest 3 Heroes", 3, {}),
    (25, 3, (), "light", "introduce 3", 3, {}),
    (26, 3, ("Hidden Marks",), "light", "Hidden Marks new+familiar", 2, {}),
    (27, 3, (), "light", "introduce 3", 3, {}),
    (28, 2, ("Hidden Marks",), "medium", "Hidden Marks return", 0, {}),
    (29, 2, ("Blurred Vision",), "medium", "teach Blurred Vision", 0, {"mem": "blur_one"}),
    (30, 2, ("Blurred Vision",), "medium", "Blurred Vision mini-set", 0, {"mem": "blur_one"}),
    (31, 2, ("Blurred Vision",), "medium", "Blurred Vision mini-set", 0, {"mem": "blur_one"}),
    (32, 2, ("Hidden Marks", "Blurred Vision"), "medium", "first stack HM+Blur", 0, {"mem": "blur_one"}),
    (33, 3, (), "light", "introduce 3", 3, {}),
    (34, 2, ("Blurred Vision",), "medium", "Blurred Vision return", 0, {"mem": "blur_one"}),
    (35, 3, ("Hidden Marks",), "light", "Hidden Marks new+familiar", 2, {}),
    (36, 1, ("Fragile Spirit",), "medium", "teach Fragile Spirit", 0, {"max_sounds": 12}),
    (37, 2, ("Fragile Spirit",), "medium", "Fragile Spirit mini-set", 0, {}),
    (38, 2, ("Fragile Spirit",), "medium", "Fragile Spirit mini-set", 0, {}),
    (39, 2, ("Fragile Spirit", "Blurred Vision"), "medium", "Fragile+Blur", 0, {"mem": "blur_one"}),
    (40, 2, ("Mirror Mode",), "heavy", "teach Mirror Mode", 0, {"max_sounds": 16}),
    (41, 2, ("Mirror Mode",), "medium", "Mirror Mode mini-set", 0, {}),
    (42, 2, ("Mirror Mode",), "medium", "Mirror Mode mini-set", 0, {}),
    (43, 2, ("Mirror Mode", "Blurred Vision"), "medium", "Mirror+Blur", 0, {"mem": "blur_one"}),
    (44, 3, (), "light", "introduce 3", 3, {}),
    (45, 3, ("Mirror Mode", "Hidden Marks"), "light", "Mirror+HM", 1, {}),
    (46, 1, ("Sudden Death",), "medium", "guest Sudden Death", 0, {"max_sounds": 12}),
    (47, 3, (), "light", "recover introduce 3", 3, {}),
    (48, 3, (), "light", "recover introduce 3", 3, {}),
    (49, 2, ("Fragile Spirit",), "medium", "Fragile return", 0, {}),
    (50, 2, ("Unknown Count",), "medium", "teach Unknown Count", 0, {}),
    (51, 2, ("Unknown Count",), "medium", "Unknown Count mini-set", 0, {}),
    (52, 2, ("Unknown Count",), "medium", "Unknown Count mini-set", 0, {}),
    (53, 3, ("Unknown Count",), "light", "Unknown Count new+familiar", 1, {}),
    (54, 3, ("Unknown Count", "Blurred Vision"), "light", "UC+Blur", 1, {"mem": "blur_one"}),
    (55, 3, (), "light", "introduce 3", 3, {}),
    (56, 2, ("Echo Limit",), "medium", "teach Echo Limit Easy", 0, {"echo": 10}),
    (57, 2, ("Echo Limit",), "medium", "Echo Limit Easy", 0, {"echo": 10}),
    (58, 2, ("Echo Limit",), "medium", "Echo Limit Easy", 0, {"echo": 10}),
    (59, 2, ("Echo Limit", "Blurred Vision"), "medium", "Echo+Blur", 0, {"echo": 10, "mem": "blur_one"}),
    (60, 2, ("Echo Limit", "Mirror Mode"), "medium", "Echo+Mirror", 0, {"echo": 10}),
    (61, 3, (), "light", "introduce 3", 3, {}),
    (62, 2, ("Race Against Time",), "medium", "teach Race", 0, {"timer": 120}),
    (63, 2, ("Race Against Time",), "medium", "Race", 0, {"timer": 110}),
    (64, 2, ("Race Against Time",), "medium", "Race", 0, {"timer": 100}),
    (65, 3, (), "light", "no-timer introduce 3", 3, {}),
    (66, 3, ("Race Against Time", "Blurred Vision"), "light", "Race+Blur", 1, {"timer": 100, "mem": "blur_one"}),
    (67, 2, ("Race Against Time", "Mirror Mode"), "medium", "Race+Mirror", 0, {"timer": 95}),
    (68, 1, ("Fragile Spirit",), "medium", "guest Fragile Spirit", 0, {"max_sounds": 12}),
    (69, 1, ("Sudden Death",), "medium", "guest Sudden Death", 0, {"max_sounds": 12}),
    (70, 2, ("Partial Veil",), "medium", "teach Partial Veil", 0, {"mem": "mask_one"}),
    (71, 2, ("Partial Veil",), "medium", "Partial Veil mini-set", 0, {"mem": "mask_one"}),
    (72, 2, ("Partial Veil",), "medium", "Partial Veil mini-set", 0, {"mem": "mask_one"}),
    (73, 3, ("Partial Veil",), "medium", "Partial Veil 3 Heroes", 0, {"mem": "mask_one"}),
    (74, 2, ("Partial Veil", "Blurred Vision"), "medium", "PV+Blur", 0, {"mem": "mask_blur"}),
    (75, 3, ("Partial Veil",), "light", "Partial Veil", 1, {"mem": "mask_one"}),
    (76, 2, ("Partial Veil", "Echo Limit"), "medium", "PV+Echo", 0, {"echo": 10, "mem": "mask_one"}),
    (77, 3, (), "light", "introduce 3", 3, {}),
    (78, 2, ("Soundquake",), "medium", "teach Soundquake", 0, {"timer": 25}),
    (79, 2, ("Soundquake",), "medium", "Soundquake mini-set", 0, {"timer": 22}),
    (80, 2, ("Soundquake",), "medium", "Soundquake mini-set", 0, {"timer": 20}),
    (81, 3, ("Soundquake",), "light", "Soundquake", 1, {"timer": 22}),
    (82, 3, ("Soundquake", "Blurred Vision"), "light", "SQ+Blur", 1, {"timer": 22, "mem": "blur_one"}),
    (83, 2, ("Soundquake", "Mirror Mode"), "medium", "SQ+Mirror", 0, {"timer": 25}),
    (84, 3, (), "light", "introduce 3", 3, {}),
    (85, 2, ("Soundquake Aftershock",), "medium", "teach Aftershock", 0, {"timer": 36}),
    (86, 2, ("Soundquake Aftershock",), "medium", "Aftershock mini-set", 0, {"timer": 34}),
    (87, 2, ("Soundquake Aftershock",), "medium", "Aftershock mini-set", 0, {"timer": 32}),
    (88, 3, ("Soundquake Aftershock", "Blurred Vision"), "light", "Aftershock+Blur", 1, {"timer": 32, "mem": "blur_one"}),
    (89, 2, ("Soundquake Aftershock", "Echo Limit"), "medium", "Aftershock+Echo", 0, {"timer": 30, "echo": 10}),
    (90, 3, (), "light", "introduce 3", 3, {}),
    (91, 2, ("Mirror Mode",), "heavy", "Mirror return", 0, {"max_sounds": 16}),
    (92, 2, ("The Hidden Hero",), "medium", "teach The Hidden Hero", 0, {}),
    (93, 2, ("The Hidden Hero",), "medium", "Hidden Hero mini-set", 0, {}),
    (94, 2, ("The Hidden Hero",), "medium", "Hidden Hero mini-set", 0, {}),
    (95, 3, (), "light", "drop introduce 3", 3, {}),
    (96, 2, ("The Hidden Hero", "Fragile Spirit"), "medium", "HH+Fragile", 0, {}),
    (97, 2, ("The Hidden Hero", "Echo Limit"), "medium", "HH+Echo", 0, {"echo": 10}),
    (98, 2, ("Among Heroes",), "medium", "teach Among Heroes", 0, {"mem": "hide_one"}),
    (99, 2, ("Among Heroes",), "medium", "Among Heroes mini-set", 0, {"mem": "hide_one"}),
    (100, 2, ("Among Heroes",), "medium", "Among Heroes mini-set", 0, {"mem": "hide_one"}),
    (101, 3, ("Among Heroes",), "medium", "Among Heroes 3 Heroes", 0, {"mem": "hide_one"}),
    (102, 2, ("Among Heroes", "Blurred Vision"), "medium", "Among+Blur", 0, {"mem": "hide_blur"}),
    (103, 2, ("Among Heroes", "Echo Limit"), "medium", "Among+Echo", 0, {"echo": 10, "mem": "hide_one"}),
    (104, 2, ("Blurred Vision 2",), "medium", "teach Blurred Vision 2", 0, {"mem": "blur_all"}),
    (105, 2, ("Blurred Vision 2",), "medium", "Blurred Vision 2 mini-set", 0, {"mem": "blur_one"}),
    (106, 2, ("Blurred Vision 2",), "medium", "Blurred Vision 2 mini-set", 0, {"mem": "blur_all"}),
    (107, 3, ("Blurred Vision 2", "Echo Limit"), "light", "BV2+Echo", 1, {"echo": 10, "mem": "blur_one"}),
    (108, 2, ("Echo Limit", "Mirror Mode", "Blurred Vision"), "medium", "3-stack Echo+Mirror+Blur", 0, {"echo": 10, "mem": "blur_one"}),
    (109, 2, ("The Hidden Hero", "Fragile Spirit"), "medium", "HH+Fragile", 0, {}),
    (110, 3, (), "light", "rollercoaster introduce 3", 3, {}),
    (111, 2, ("Race Against Time", "Blurred Vision", "Echo Limit"), "medium", "3-stack Race+Blur+Echo", 0, {"timer": 80, "echo": 5, "mem": "blur_one"}),
    (112, 3, ("Echo Limit",), "light", "Echo Limit Medium", 1, {"echo": 5}),
    (113, 2, ("Soundquake Aftershock", "Mirror Mode"), "medium", "Aftershock+Mirror", 0, {"timer": 32}),
    (114, 3, ("Race Against Time",), "light", "tight Race", 1, {"timer": 75}),
    (115, 2, ("Echo Limit", "Blurred Vision", "Fragile Spirit"), "medium", "3-stack Echo+Blur+Fragile", 0, {"echo": 5, "mem": "blur_one"}),
    (116, 3, ("Hidden Marks",), "light", "easy introduce 3", 3, {}),
    (117, 3, ("Partial Veil", "Echo Limit"), "light", "PV+Echo Med", 1, {"echo": 5, "mem": "mask_one"}),
    (118, 3, (), "light", "guest 3 Heroes familiar", 0, {}),
    (119, 2, ("Unknown Count", "Blurred Vision", "Echo Limit"), "medium", "3-stack UC+Blur+Echo", 0, {"echo": 10, "mem": "blur_one"}),
    (120, 3, ("Mirror Mode", "Echo Limit"), "light", "Mirror+Echo", 1, {"echo": 10}),
    (121, 2, ("Soundquake Fracture",), "medium", "teach Fracture", 0, {"timer": 28}),
    (122, 2, ("Soundquake Fracture", "Blurred Vision"), "medium", "Fracture+Blur", 0, {"timer": 26, "mem": "blur_one"}),
    (123, 3, (), "light", "remix introduce 3", 3, {}),
    (124, 3, ("Soundquake Fracture", "Echo Limit"), "light", "Fracture+Echo", 1, {"timer": 26, "echo": 5}),
    (125, 2, ("Soundquake Cataclysm",), "medium", "teach Cataclysm", 0, {"timer": 38}),
    (126, 2, ("Soundquake Cataclysm",), "medium", "Cataclysm mini-set", 0, {"timer": 35}),
    (127, 3, (), "light", "high-volume Ringmaster", 2, {"require": ("Ringmaster",)}),
    (128, 3, (), "medium", "high-volume Kez", 2, {"require": ("Kez",)}),
    (129, 2, (), "medium", "high-volume Invoker", 0, {"require": ("Invoker",)}),
    (130, 3, ("Echo Limit",), "light", "guest Echo Hard", 1, {"echo": 0}),
    (131, 2, ("Mirror Mode", "The Hidden Hero"), "medium", "expert Mirror+Hidden Hero", 0, {}),
    (132, 2, ("Mirror Mode", "Partial Veil"), "medium", "expert Mirror+Partial Veil", 0, {"mem": "mask_one"}),
    (133, 3, (), "light", "remix introduce 3", 3, {}),
    (134, 3, ("Unknown Count", "Blurred Vision", "Fragile Spirit"), "light", "3-stack UC+Blur+Fragile", 1, {"mem": "blur_one"}),
    (135, 2, ("Among Heroes", "Blurred Vision", "Echo Limit"), "medium", "3-stack Among+Blur+Echo", 0, {"echo": 5, "mem": "hide_blur"}),
    (136, 3, (), "light", "remix introduce 3", 3, {}),
    (137, 2, ("Soundquake", "Mirror Mode", "Blurred Vision"), "medium", "3-stack SQ+Mirror+Blur", 0, {"timer": 22, "mem": "blur_one"}),
    (138, 3, ("Race Against Time", "Blurred Vision", "Echo Limit"), "light", "3-stack tight Race+Blur+Echo", 1, {"timer": 70, "echo": 5, "mem": "blur_one"}),
    (139, 3, (), "light", "remix introduce 3", 3, {}),
    (140, 3, (), "light", "remix introduce 3", 3, {}),
    (141, 3, ("Soundquake Aftershock", "Echo Limit", "Blurred Vision"), "light", "3-stack Aftershock+Echo+Blur", 1, {"timer": 32, "echo": 5, "mem": "blur_one"}),
    (142, 4, ("Fragile Spirit",), "light", "guest 4 Heroes Fragile", 0, {}),
    (143, 3, (), "light", "remix introduce 3", 3, {}),
    (144, 2, ("Mirror Mode", "The Hidden Hero"), "medium", "expert Mirror+Hidden Hero", 0, {}),
    (145, 3, ("Among Heroes", "Blurred Vision", "Fragile Spirit", "Echo Limit"), "medium", "4-stack Among+Blur+Fragile+Echo", 0, {"echo": 5, "mem": "hide_blur"}),
    (146, 2, ("Race Against Time", "Mirror Mode", "Blurred Vision", "Echo Limit"), "medium", "4-stack Race+Mirror+Blur+Echo", 0, {"timer": 75, "echo": 5, "mem": "blur_one"}),
    (147, 3, ("Soundquake Cataclysm", "Blurred Vision", "Echo Limit"), "light", "3-stack Cataclysm+Blur+Echo", 1, {"timer": 34, "echo": 5, "mem": "blur_one"}),
    (148, 3, (), "medium", "remix introduce 3", 3, {}),
    (149, 3, ("Echo Limit",), "light", "guest Echo Hard", 1, {"echo": 0}),
    (150, 2, ("Soundquake Cataclysm", "Mirror Mode", "Blurred Vision", "Echo Limit"), "medium", "finale 4-stack Cataclysm+Mirror+Blur+Echo", 0, {"timer": 34, "echo": 5, "mem": "blur_one"}),
]

for level, n, aff, density, role, intro_n, kw in SPECS:
    R(
        level,
        n,
        *aff,
        density=density,
        role=role,
        intro_n=intro_n,
        max_sounds=kw.get("max_sounds"),
        timer=kw.get("timer"),
        echo=kw.get("echo", 5),
        mem=kw.get("mem"),
        require=kw.get("require", ()),
    )

assert [r["level"] for r in rows] == list(range(1, 151)), len(rows)

# Pre-assign introductions onto intro_n slots
queue = list(NON_HV)
forced: dict[int, list[str]] = {r["level"]: [] for r in rows}
qi = 0
for r in rows:
    k = r["intro_n"]
    if k <= 0:
        continue
    take = min(k, r["n"], len(queue) - qi)
    forced[r["level"]] = queue[qi : qi + take]
    qi += take
print(f"intro slots filled {qi}/{len(NON_HV)}")
if qi < len(NON_HV):
    raise SystemExit(f"not enough intro slots: {qi} < {len(NON_HV)}")

out: list[dict] = []
for r in rows:
    level, n, aff = r["level"], r["n"], r["aff"]
    # Mid/hard teach windows + identity: all familiar.
    # Levels with intro_n>0 are deliberate new+familiar (or pure introduce).
    all_fam = False
    if r["intro_n"] == 0:
        for fam, wins in TW.items():
            if level in wins and fam in aff and fam not in EASY:
                all_fam = True
        if any(a in (MH - EASY) for a in aff) and level not in teach:
            all_fam = True
        if "The Hidden Hero" in aff or "Among Heroes" in aff:
            all_fam = True
        if level in (118, 142):
            all_fam = True

    new_force = [h for h in forced[level] if h not in intro]
    if all_fam and new_force:
        raise SystemExit(f"L{level}: forced new on all_fam board {new_force}")

    require = list(r["require"]) + new_force
    if len(require) >= n:
        heroes = require[:n]
        if not ok(heroes, level, all_fam=all_fam):
            raise SystemExit(f"L{level}: forced board illegal {heroes}")
    elif n == 1:
        # Solo fill: prefer familiar once pool exists; never all_fam before first intro.
        heroes = pick(level, 1, all_fam=bool(intro))
    else:
        heroes = pick(
            level,
            n,
            all_fam=all_fam,
            take_new=0,
            require=require,
        )

    masked: tuple[str, ...] = ()
    blurred: tuple[str, ...] = ()
    hidden = None
    mem = r["mem"]
    if mem == "blur_one":
        blurred = (heroes[0],)
    elif mem == "blur_all":
        blurred = tuple(heroes)
    elif mem == "mask_one":
        masked = (heroes[-1],)
    elif mem == "mask_blur":
        masked = (heroes[0],)
        blurred = (heroes[1],)
    elif mem == "hide_one":
        hidden = heroes[-1]
    elif mem == "hide_blur":
        hidden = heroes[-1]
        blurred = (heroes[0],)

    commit(heroes, level)
    out.append(
        {
            **r,
            "heroes": heroes,
            "masked": masked,
            "blurred": blurred,
            "hidden": hidden,
        }
    )

unused = [h for h in SC if seen[h] == 0]
print("unused", unused)
print(
    "max pair/roster",
    max(len(v) for v in pair_lv.values()),
    max(len(v) for v in rost_lv.values()),
)
print("top pairs:")
for p, lv in sorted(pair_lv.items(), key=lambda x: -len(x[1]))[:12]:
    print(f"  {len(lv)}x {p[0]}+{p[1]} @ {lv}")
if unused or any(len(v) > 3 for v in pair_lv.values()) or any(len(v) > 3 for v in rost_lv.values()):
    raise SystemExit("constraints failed")


def ft(t: tuple[str, ...]) -> str:
    if not t:
        return "()"
    inner = ", ".join(repr(x) for x in t)
    if len(t) == 1:
        inner += ","
    return f"({inner})"


lines = ["LEVELS: list[Spec] = ["]
for r in out:
    args = [str(r["level"]), repr(list(r["heroes"]))] + [repr(a) for a in r["aff"]]
    kw = [f"density={r['density']!r}"]
    if r["max_sounds"] is not None:
        kw.append(f"max_sounds={r['max_sounds']}")
    if r["masked"]:
        kw.append(f"masked={ft(r['masked'])}")
    if r["blurred"]:
        kw.append(f"blurred={ft(r['blurred'])}")
    if r["hidden"]:
        kw.append(f"hidden={r['hidden']!r}")
    if r["timer"] is not None:
        kw.append(f"timer={r['timer']}")
    if r["echo"] != 5:
        kw.append(f"echo={r['echo']}")
    kw.append(f"role={r['role']!r}")
    lines.append(f"    L({', '.join(args)}, {', '.join(kw)}),")
lines.append("]")
levels_src = "\n".join(lines) + "\n"

text = catalog_path.read_text()
teach = """# Assist-retuned teach windows. Hero pairs/rosters capped at 3 repeats.
TEACH_WINDOWS: dict[str, tuple[int, ...]] = {
    "Hidden Marks": (21, 22, 23),
    "Blurred Vision": (29, 30, 31),
    "Fragile Spirit": (36, 37, 38),
    "Mirror Mode": (40, 41, 42),
    "Sudden Death": (46,),
    "Unknown Count": (50, 51, 52),
    "Echo Limit": (56, 57, 58),
    "Race Against Time": (62, 63, 64),
    "Partial Veil": (70, 71, 72),
    "Soundquake": (78, 79, 80),
    "Soundquake Aftershock": (85, 86, 87),
    "The Hidden Hero": (92, 93, 94),
    "Among Heroes": (98, 99, 100),
    "Blurred Vision 2": (104, 105, 106),
    "Soundquake Fracture": (121, 122),
    "Soundquake Cataclysm": (125, 126),
}
"""
text, n = re.subn(
    r"# Assist-retuned teach windows.*?\nTEACH_WINDOWS: dict\[str, tuple\[int, \.\.\.\]\] = \{.*?\n\}\n",
    teach,
    text,
    count=1,
    flags=re.S,
)
assert n == 1, n
text, n = re.subn(
    r"LEVELS: list\[Spec\] = \[.*?\n\]\n\n\ndef correct_count",
    levels_src + "\n\ndef correct_count",
    text,
    count=1,
    flags=re.S,
)
assert n == 1, n
if "pair >3:" not in text:
    text = text.replace(
        '        errors.append(f"Hero overuse (>16 appearances): {\', \'.join(overused[:12])}")\n    return errors',
        '        errors.append(f"Hero overuse (>16 appearances): {\', \'.join(overused[:12])}")\n'
        "    from collections import defaultdict as _dd\n"
        "    _rosters = _dd(list)\n"
        "    _pairs = _dd(list)\n"
        "    for spec in levels:\n"
        "        hs = tuple(sorted(spec.names()))\n"
        "        _rosters[hs].append(spec.level)\n"
        "        for i in range(len(hs)):\n"
        "            for j in range(i + 1, len(hs)):\n"
        "                _pairs[(hs[i], hs[j])].append(spec.level)\n"
        "    for hs, lvls in _rosters.items():\n"
        '        if len(lvls) > 3:\n'
        '            errors.append(f"roster >3: {\', \'.join(hs)} @ {lvls}")\n'
        "    for pair, lvls in _pairs.items():\n"
        "        if len(lvls) > 3:\n"
        '            errors.append(f"pair >3: {pair[0]}+{pair[1]} @ {lvls}")\n'
        "    return errors",
    )
catalog_path.write_text(text)
r = subprocess.run([sys.executable, str(catalog_path)], capture_output=True, text=True)
print(r.stdout)
print(r.stderr)
sys.exit(r.returncode)

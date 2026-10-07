#!/usr/bin/env python3
"""Journey catalog seed (150 levels) — source of truth.

Heroes and Affixes are natural keys (casters.name, affixes.affix). apply.sql
looks those up at run time, so the same script can load staging or prod.
Never paste environment-specific numeric FKs.

Regenerate SQL:  python3 scripts/journey-catalog/catalog.py
Then run apply.sql on the target DB. Staging tester reset is a separate file.
"""

from __future__ import annotations

import sys
from dataclasses import dataclass, field
from pathlib import Path

FILL = "FILL"

# Active catalog spell counts (staging 2026-10-05). Re-query casters×sounds
# if the spell catalog changes, then re-run this file.
SOUND_COUNTS: dict[str, int] = {
    "Omniknight": 2,
    "Shadow_Fiend": 2,
    "Bloodseeker": 3,
    "Bristleback": 3,
    "Chen": 3,
    "Crystal_Maiden": 3,
    "Dragon_Knight": 3,
    "Earthshaker": 3,
    "Elder_Titan": 3,
    "Lifestealer": 3,
    "Luna": 3,
    "Lycan": 3,
    "Marci": 3,
    "Necrophos": 3,
    "Night_Stalker": 3,
    "Outworld_Destroyer": 3,
    "Pudge": 3,
    "Queen_of_Pain": 3,
    "Rubick": 3,
    "Slark": 3,
    "Troll_Warlord": 3,
    "Tusk": 3,
    "Underlord": 3,
    "Ursa": 3,
    "Vengeful_Spirit": 3,
    "Venomancer": 3,
    "Visage": 3,
    "Abaddon": 4,
    "Alchemist": 4,
    "Ancient_Apparition": 4,
    "Anti-Mage": 4,
    "Arc_Warden": 4,
    "Axe": 4,
    "Bane": 4,
    "Batrider": 4,
    "Bounty_Hunter": 4,
    "Brewmaster": 4,
    "Broodmother": 4,
    "Centaur_Warrunner": 4,
    "Chaos_Knight": 4,
    "Dawnbreaker": 4,
    "Dazzle": 4,
    "Death_Prophet": 4,
    "Doom": 4,
    "Enigma": 4,
    "Faceless_Void": 4,
    "Gyrocopter": 4,
    "Hoodwink": 4,
    "Huskar": 4,
    "Io": 4,
    "Jakiro": 4,
    "Juggernaut": 4,
    "Legion_Commander": 4,
    "Lina": 4,
    "Lion": 4,
    "Lone_Druid": 4,
    "Magnus": 4,
    "Mars": 4,
    "Medusa": 4,
    "Meepo": 4,
    "Mirana": 4,
    "Morphling": 4,
    "Muerta": 4,
    "Naga_Siren": 4,
    "Nature's_Prophet": 4,
    "Ogre_Magi": 4,
    "Phantom_Lancer": 4,
    "Phoenix": 4,
    "Primal_Beast": 4,
    "Puck": 4,
    "Pugna": 4,
    "Razor": 4,
    "Riki": 4,
    "Shadow_Demon": 4,
    "Shadow_Shaman": 4,
    "Silencer": 4,
    "Skywrath_Mage": 4,
    "Slardar": 4,
    "Snapfire": 4,
    "Sniper": 4,
    "Spectre": 4,
    "Spirit_Breaker": 4,
    "Storm_Spirit": 4,
    "Sven": 4,
    "Tidehunter": 4,
    "Timbersaw": 4,
    "Tinker": 4,
    "Tiny": 4,
    "Undying": 4,
    "Viper": 4,
    "Void_Spirit": 4,
    "Warlock": 4,
    "Weaver": 4,
    "Winter_Wyvern": 4,
    "Witch_Doctor": 4,
    "Wraith_King": 4,
    "Zeus": 4,
    "Beastmaster": 5,
    "Dark_Seer": 5,
    "Dark_Willow": 5,
    "Drow_Ranger": 5,
    "Ember_Spirit": 5,
    "Enchantress": 5,
    "Grimstroke": 5,
    "Keeper_of_the_Light": 5,
    "Kunkka": 5,
    "Largo": 5,
    "Leshrac": 5,
    "Lich": 5,
    "Nyx_Assassin": 5,
    "Oracle": 5,
    "Pangolier": 5,
    "Phantom_Assassin": 5,
    "Sand_King": 5,
    "Techies": 5,
    "Templar_Assassin": 5,
    "Terrorblade": 5,
    "Windranger": 5,
    "Clinkz": 6,
    "Clockwerk": 6,
    "Disruptor": 6,
    "Earth_Spirit": 6,
    "Treant_Protector": 6,
    "Monkey_King": 7,
    "Ringmaster": 8,
    "Kez": 9,
    "Invoker": 11,
}

HIGH_VOLUME = frozenset({"Invoker", "Kez", "Ringmaster"})
LEGAL_BOARD = frozenset({8, 12, 16, 20, 24, 28})
DENSITY_EXTRA = {
    "light": (2, 6),
    "medium": (7, 10),
    "heavy": (11, 20),
}

EASY = frozenset({"Hidden Marks", "Blurred Vision", "Fragile Spirit"})
MID = frozenset(
    {
        "Unknown Count",
        "Echo Limit",
        "Race Against Time",
        "Partial Veil",
        "Soundquake",
        "The Hidden Hero",
        "Blurred Vision 2",
    }
)
HARD = frozenset(
    {
        "Sudden Death",
        "Among Heroes",
        "Soundquake Aftershock",
        "Mirror Mode",
        "Soundquake Fracture",
        "Soundquake Cataclysm",
    }
)

IDENTITY = ("The Hidden Hero", "Partial Veil", "Among Heroes")
QUAKE = (
    "Soundquake",
    "Soundquake Aftershock",
    "Soundquake Fracture",
    "Soundquake Cataclysm",
)

# First teach + next 1–2 of that family must be all-familiar (mid/hard).
# Easy teach: ≥1 familiar. Sudden Death teach: 1 familiar, solo.
TEACH_WINDOWS: dict[str, tuple[int, ...]] = {
    "Hidden Marks": (27, 28, 29),
    "Blurred Vision": (39, 40, 41),
    "Fragile Spirit": (46, 47, 48),
    "Sudden Death": (52,),
    "Unknown Count": (62, 63, 64),
    "Echo Limit": (66, 67, 68),
    "Race Against Time": (70, 71, 72),
    "Partial Veil": (82, 83, 84),
    "Soundquake": (91, 92, 93),
    "The Hidden Hero": (99, 100, 101),
    "Among Heroes": (106, 107, 108),
    "Blurred Vision 2": (111, 112, 113),
    "Soundquake Aftershock": (115, 116, 117),
    "Mirror Mode": (118, 119, 120),
    "Soundquake Fracture": (124, 125),
    "Soundquake Cataclysm": (126, 127),
}


@dataclass
class Spec:
    level: int
    heroes: list[str]
    affixes: tuple[str, ...] = ()
    density: str = "medium"
    max_sounds: int | None = None
    masked: tuple[str, ...] = ()
    blurred: tuple[str, ...] = ()
    hidden: str | None = None
    timer: int | None = None
    echo: int = 5
    role: str = ""
    resolved: list[str] = field(default_factory=list)

    def names(self) -> list[str]:
        return self.resolved or self.heroes


def L(
    level: int,
    heroes: list[str],
    *affixes: str,
    density: str = "medium",
    max_sounds: int | None = None,
    masked: tuple[str, ...] = (),
    blurred: tuple[str, ...] = (),
    hidden: str | None = None,
    timer: int | None = None,
    echo: int = 5,
    role: str = "",
) -> Spec:
    return Spec(
        level=level,
        heroes=heroes,
        affixes=affixes,
        density=density,
        max_sounds=max_sounds,
        masked=masked,
        blurred=blurred,
        hidden=hidden,
        timer=timer,
        echo=echo,
        role=role,
    )


# Band map slices. FILL = unused Hero not yet named on an earlier level.
LEVELS: list[Spec] = [
    # --- Slice 1: 1–20 primer, no Affix, no 3-Hero ---
    L(1, ["Shadow_Fiend"], density="light", max_sounds=8, role="primer"),
    L(2, ["Omniknight"], density="light", max_sounds=8, role="primer"),
    L(3, ["Crystal_Maiden"], density="medium", max_sounds=12, role="primer"),
    L(4, ["Pudge"], density="medium", max_sounds=12, role="primer"),
    L(5, ["Luna"], density="medium", max_sounds=12, role="primer"),
    L(6, ["Dragon_Knight"], density="medium", max_sounds=12, role="primer"),
    L(7, ["Axe"], density="medium", max_sounds=12, role="primer"),
    L(8, ["Lina"], density="medium", max_sounds=12, role="primer"),
    L(9, ["Marci"], density="medium", max_sounds=12, role="guest decoys"),
    L(10, ["Sniper"], density="medium", max_sounds=12, role="guest decoys"),
    L(11, ["Juggernaut", "Sven"], density="light", max_sounds=12, role="teach 2 Heroes"),
    L(12, ["Wraith_King", "Tidehunter"], density="light", max_sounds=12, role="plateau"),
    L(13, ["Lion", "Witch_Doctor"], density="medium", max_sounds=16, role="plateau"),
    L(14, ["Riki", "Bounty_Hunter"], density="medium", max_sounds=16, role="plateau"),
    L(15, ["Viper", "Shadow_Shaman"], density="medium", max_sounds=16, role="plateau"),
    L(16, ["Ogre_Magi", "Puck"], density="medium", max_sounds=16, role="more decoys"),
    L(17, ["Abaddon", "Huskar"], density="medium", max_sounds=16, role="plateau"),
    L(18, ["Necrophos", "Drow_Ranger"], density="medium", max_sounds=16, role="plateau"),
    L(19, ["Zeus", "Phantom_Assassin"], density="medium", max_sounds=16, role="fat decoys"),
    L(20, ["Sand_King", "Kunkka"], density="medium", max_sounds=20, role="fat decoys"),
    # --- Slice 2: 21–45 ---
    L(21, [FILL, FILL], density="medium", role="introduce"),
    L(22, [FILL, FILL], density="medium", role="introduce"),
    L(23, [FILL, FILL], density="medium", role="introduce"),
    L(24, [FILL, FILL, "Juggernaut"], density="light", role="guest 3 Heroes"),
    L(25, [FILL, FILL], density="heavy", max_sounds=20, role="fatter decoys"),
    L(26, [FILL, FILL], density="heavy", max_sounds=20, role="fatter decoys"),
    L(27, ["Crystal_Maiden", "Pudge"], "Hidden Marks", density="light", role="teach Hidden Marks"),
    L(28, ["Luna", "Dragon_Knight"], "Hidden Marks", density="light", role="Hidden Marks mini-set"),
    L(29, ["Axe", "Lina"], "Hidden Marks", density="medium", role="Hidden Marks mini-set"),
    L(30, [FILL, "Sniper"], "Hidden Marks", density="light", role="Hidden Marks new+familiar"),
    L(31, [FILL, FILL], density="medium", role="rollercoaster introduce"),
    L(32, [FILL, "Juggernaut"], "Hidden Marks", density="medium", role="Hidden Marks return"),
    L(33, [FILL, FILL, "Axe"], density="light", role="guest 3 Heroes"),
    L(34, [FILL, FILL], density="medium", role="introduce"),
    L(35, [FILL, FILL], density="medium", role="introduce"),
    L(36, [FILL, "Riki"], "Hidden Marks", density="medium", role="Hidden Marks new+familiar"),
    L(37, [FILL, FILL], density="medium", role="introduce"),
    L(38, [FILL, "Viper"], "Hidden Marks", density="medium", role="Hidden Marks new+familiar"),
    L(39, ["Lion", "Witch_Doctor"], "Blurred Vision", density="medium", blurred=("Lion",), role="teach Blurred Vision"),
    L(40, ["Ogre_Magi", "Puck"], "Blurred Vision", density="medium", blurred=("Puck",), role="Blurred Vision mini-set"),
    L(41, ["Abaddon", "Huskar"], "Blurred Vision", density="medium", blurred=("Huskar",), role="Blurred Vision mini-set"),
    L(42, ["Zeus", "Phantom_Assassin"], "Blurred Vision", density="medium", blurred=("Zeus",), role="Blurred Vision"),
    L(43, [FILL, "Necrophos"], "Hidden Marks", density="medium", role="drop blur"),
    L(44, [FILL, FILL], density="medium", role="introduce"),
    L(45, ["Shadow_Fiend", "Omniknight"], "Hidden Marks", density="heavy", max_sounds=16, role="fat decoys Hidden Marks"),
    # --- Slice 3: 46–75 ---
    L(46, ["Sniper"], "Fragile Spirit", density="medium", max_sounds=12, role="teach Fragile Spirit"),
    L(47, ["Crystal_Maiden", "Lina"], "Fragile Spirit", density="medium", role="Fragile Spirit mini-set"),
    L(48, ["Pudge", "Luna"], "Fragile Spirit", density="medium", role="Fragile Spirit mini-set"),
    L(49, ["Axe", "Juggernaut"], "Fragile Spirit", density="medium", role="Fragile Spirit"),
    L(50, [FILL, FILL, FILL], density="light", role="recover introduce 3"),
    L(51, [FILL, "Sven"], "Hidden Marks", density="medium", role="recover Hidden Marks"),
    L(52, ["Sniper"], "Sudden Death", density="medium", max_sounds=12, role="guest Sudden Death"),
    L(53, [FILL, FILL, FILL], density="light", role="recover introduce 3"),
    L(54, [FILL, FILL, FILL], density="light", role="recover introduce 3"),
    L(55, [FILL, FILL, FILL], density="light", role="introduce 3 Heroes"),
    L(56, [FILL, FILL, "Marci"], "Hidden Marks", density="light", role="Hidden Marks new+familiar"),
    L(57, [FILL, FILL, FILL], density="light", role="introduce 3 Heroes"),
    L(58, ["Dragon_Knight", "Luna", "Crystal_Maiden"], density="medium", role="guest 3 Heroes familiar"),
    L(59, ["Sniper", "Axe"], "Fragile Spirit", density="medium", role="Fragile Spirit return"),
    L(60, [FILL, FILL, FILL], density="light", role="introduce 3 Heroes"),
    L(61, [FILL, FILL, "Pudge"], "Hidden Marks", density="light", role="Hidden Marks new+familiar"),
    L(62, ["Juggernaut", "Sven"], "Unknown Count", density="medium", role="teach Unknown Count"),
    L(63, ["Wraith_King", "Tidehunter"], "Unknown Count", density="medium", role="Unknown Count mini-set"),
    L(64, ["Lion", "Witch_Doctor"], "Unknown Count", density="medium", role="Unknown Count mini-set"),
    L(65, [FILL, "Lina"], "Unknown Count", density="medium", role="Unknown Count new+familiar"),
    L(66, ["Pudge", "Crystal_Maiden"], "Echo Limit", density="medium", echo=10, role="teach Echo Limit Easy"),
    L(67, ["Luna", "Dragon_Knight"], "Echo Limit", density="medium", echo=10, role="Echo Limit Easy"),
    L(68, ["Axe", "Sniper"], "Echo Limit", density="medium", echo=10, role="Echo Limit Easy"),
    L(69, ["Riki", "Bounty_Hunter"], "Echo Limit", density="medium", echo=10, role="Echo Limit Easy"),
    L(70, ["Juggernaut", "Phantom_Assassin"], "Race Against Time", density="medium", timer=120, role="teach Race"),
    L(71, ["Sven", "Sand_King"], "Race Against Time", density="medium", timer=110, role="Race"),
    L(72, ["Zeus", "Kunkka"], "Race Against Time", density="heavy", timer=100, role="Race"),
    L(73, [FILL, FILL, FILL], density="light", role="no-timer 3 Heroes"),
    L(74, ["Ogre_Magi", "Puck"], "Race Against Time", density="medium", timer=90, role="Race"),
    L(75, ["Viper", "Shadow_Shaman"], "Race Against Time", density="medium", timer=90, role="Race"),
    # --- Slice 4: 76–105 ---
    L(76, [FILL, FILL, FILL], density="light", role="introduce 3 Heroes"),
    L(77, [FILL, FILL, "Tidehunter"], "Hidden Marks", density="light", role="Hidden Marks new+familiar"),
    L(78, ["Lina"], "Fragile Spirit", density="medium", max_sounds=12, role="guest Fragile Spirit"),
    L(79, [FILL, FILL, "Sven"], density="medium", role="guest 3 Heroes"),
    L(80, ["Axe", "Sniper"], "Echo Limit", density="medium", echo=10, role="Echo Limit Easy"),
    L(81, ["Marci"], "Sudden Death", density="medium", max_sounds=12, role="guest Sudden Death"),
    L(82, ["Juggernaut", "Sven"], "Partial Veil", density="medium", masked=("Sven",), role="teach Partial Veil"),
    L(83, ["Wraith_King", "Tidehunter"], "Partial Veil", density="medium", masked=("Wraith_King",), role="Partial Veil mini-set"),
    L(84, ["Lion", "Witch_Doctor"], "Partial Veil", density="medium", masked=("Witch_Doctor",), role="Partial Veil mini-set"),
    L(85, ["Axe", "Lina", "Sniper"], "Partial Veil", density="medium", masked=("Lina",), role="Partial Veil 3 Heroes"),
    L(86, [FILL, FILL, FILL], density="light", role="introduce 3 Heroes"),
    L(87, ["Pudge", "Crystal_Maiden"], "Partial Veil", density="medium", masked=("Pudge",), role="Partial Veil"),
    L(88, ["Riki", "Bounty_Hunter"], "Partial Veil", density="medium", masked=("Riki",), role="Partial Veil"),
    L(89, ["Drow_Ranger", "Phantom_Assassin"], "Partial Veil", density="light", masked=("Drow_Ranger",), role="Partial Veil"),
    L(90, ["Ogre_Magi", "Zeus"], "Partial Veil", density="medium", masked=("Zeus",), role="Partial Veil"),
    L(91, ["Juggernaut", "Sven"], "Soundquake", density="medium", timer=25, role="teach Soundquake"),
    L(92, ["Wraith_King", "Tidehunter"], "Soundquake", density="medium", timer=22, role="Soundquake mini-set"),
    L(93, ["Lion", "Witch_Doctor"], "Soundquake", density="medium", timer=20, role="Soundquake mini-set"),
    L(94, ["Axe", "Lina"], "Soundquake", density="medium", timer=20, role="Soundquake"),
    L(95, ["Sniper", "Crystal_Maiden"], "Soundquake", density="medium", timer=20, role="Soundquake"),
    L(96, ["Pudge", "Luna"], "Soundquake", "Hidden Marks", density="medium", timer=20, role="Soundquake + Hidden Marks"),
    L(97, ["Riki", "Bounty_Hunter"], "Soundquake", density="medium", timer=22, role="Soundquake"),
    L(98, ["Abaddon", "Huskar"], "Soundquake", density="medium", timer=20, role="Soundquake"),
    L(99, ["Juggernaut", "Sven"], "The Hidden Hero", density="medium", role="teach The Hidden Hero"),
    L(100, ["Wraith_King", "Tidehunter"], "The Hidden Hero", density="medium", role="Hidden Hero mini-set"),
    L(101, ["Lion", "Witch_Doctor"], "The Hidden Hero", density="medium", role="Hidden Hero mini-set"),
    L(102, [FILL, FILL, FILL], "Hidden Marks", density="light", role="drop to easy introduce"),
    L(103, ["Axe", "Lina"], "The Hidden Hero", density="medium", role="Hidden Hero"),
    L(104, ["Sniper", "Phantom_Assassin"], "The Hidden Hero", density="medium", role="Hidden Hero"),
    L(105, ["Pudge", "Crystal_Maiden"], "The Hidden Hero", density="medium", role="Hidden Hero"),
    # --- Slice 5: 106–120 peak teach ---
    L(106, ["Juggernaut", "Sven"], "Among Heroes", density="medium", hidden="Sven", role="teach Among Heroes"),
    L(107, ["Wraith_King", "Tidehunter"], "Among Heroes", density="medium", hidden="Tidehunter", role="Among Heroes mini-set"),
    L(108, ["Lion", "Witch_Doctor"], "Among Heroes", density="medium", hidden="Witch_Doctor", role="Among Heroes mini-set"),
    L(109, ["Axe", "Lina", "Sniper"], "Among Heroes", density="medium", hidden="Sniper", role="Among Heroes 3 Heroes"),
    L(110, [FILL, FILL, "Crystal_Maiden"], "Hidden Marks", density="medium", role="rollercoaster 3 + easy"),
    L(111, ["Juggernaut", "Sven"], "Blurred Vision 2", density="medium", blurred=("Juggernaut", "Sven"), role="teach Blurred Vision 2"),
    L(112, ["Wraith_King", "Tidehunter"], "Blurred Vision 2", density="medium", blurred=("Tidehunter",), role="Blurred Vision 2 mini-set"),
    L(113, ["Lion", "Witch_Doctor"], "Blurred Vision 2", density="medium", blurred=("Lion", "Witch_Doctor"), role="Blurred Vision 2 mini-set"),
    L(114, ["Axe", "Lina"], "Blurred Vision 2", density="medium", blurred=("Lina",), role="Blurred Vision 2"),
    L(115, ["Juggernaut", "Phantom_Assassin"], "Soundquake Aftershock", density="medium", timer=22, role="teach Aftershock"),
    L(116, ["Sven", "Sand_King"], "Soundquake Aftershock", density="medium", timer=20, role="Aftershock mini-set"),
    L(117, ["Zeus", "Kunkka"], "Soundquake Aftershock", density="heavy", timer=20, role="Aftershock"),
    L(118, ["Crystal_Maiden", "Pudge"], "Mirror Mode", density="medium", role="teach Mirror Mode"),
    L(119, ["Luna", "Dragon_Knight"], "Mirror Mode", density="medium", role="Mirror Mode mini-set"),
    L(120, ["Shadow_Fiend", "Omniknight"], "Mirror Mode", density="heavy", max_sounds=16, role="Mirror Mode"),
    # --- Slice 6: 121–150 remix ---
    L(121, [FILL, FILL, FILL], density="light", role="remix introduce 3"),
    L(122, [FILL, FILL, FILL], density="light", role="remix introduce 3"),
    L(123, [FILL, FILL, FILL], density="medium", role="remix introduce 3"),
    L(124, ["Juggernaut", "Sven"], "Soundquake Fracture", density="medium", timer=22, role="teach Fracture"),
    L(125, ["Wraith_King", "Tidehunter"], "Soundquake Fracture", density="medium", timer=20, role="Fracture"),
    L(126, ["Lion", "Witch_Doctor"], "Soundquake Cataclysm", density="medium", timer=22, role="teach Cataclysm"),
    L(127, ["Axe", "Lina"], "Soundquake Cataclysm", density="medium", timer=20, role="Cataclysm"),
    L(128, ["Ringmaster", FILL, "Abaddon"], density="light", role="high-volume Ringmaster"),
    L(129, ["Kez", FILL, "Huskar"], density="medium", role="high-volume Kez"),
    L(130, ["Invoker", "Omniknight"], density="medium", role="high-volume Invoker"),
    L(131, ["Sniper", "Phantom_Assassin"], "Echo Limit", density="medium", echo=5, role="Echo Limit Medium"),
    L(132, ["Crystal_Maiden", "Pudge"], "Echo Limit", density="medium", echo=5, role="Echo Limit Medium"),
    L(133, ["Juggernaut", "Sven"], "Race Against Time", density="medium", timer=75, role="tight Race"),
    L(134, [FILL, FILL, FILL], density="light", role="remix recover introduce"),
    L(135, [FILL, FILL, FILL], density="light", role="remix introduce 3"),
    L(136, ["Shadow_Fiend", "Omniknight", "Marci", "Sniper"], density="light", role="guest 4 Heroes"),
    L(137, ["Luna", "Dragon_Knight"], "Hidden Marks", "Unknown Count", density="medium", role="Hidden Marks + Unknown Count"),
    L(138, ["Zeus", "Kunkka"], "Echo Limit", "Hidden Marks", density="medium", echo=10, role="Echo Easy + Hidden Marks"),
    L(139, ["Juggernaut", "Sven"], "Mirror Mode", "The Hidden Hero", density="medium", role="expert Mirror + Hidden Hero"),
    L(140, ["Wraith_King", "Tidehunter"], "Mirror Mode", "Partial Veil", density="medium", masked=("Tidehunter",), role="expert Mirror + Partial Veil"),
    L(141, [FILL, FILL, FILL], density="light", role="remix introduce 3"),
    L(142, ["Lion", "Witch_Doctor"], "Among Heroes", "Hidden Marks", density="medium", hidden="Witch_Doctor", role="Among Heroes + Hidden Marks"),
    L(143, ["Juggernaut", "Sven"], "Among Heroes", "Blurred Vision", density="medium", hidden="Sven", blurred=("Juggernaut",), role="Among Heroes + Blurred Vision"),
    L(144, ["Axe", "Lina"], "Race Against Time", "Blurred Vision", density="medium", timer=70, blurred=("Lina",), role="tight Race + Blurred Vision"),
    L(145, ["Sniper", "Crystal_Maiden"], "Soundquake", "Hidden Marks", density="medium", timer=20, role="Soundquake + Hidden Marks"),
    L(146, ["Juggernaut", "Phantom_Assassin"], "Echo Limit", density="medium", echo=0, role="guest Echo Limit Hard"),
    L(147, ["Crystal_Maiden", "Pudge", "Luna", "Dragon_Knight"], "Hidden Marks", density="light", role="guest 4 Heroes easy"),
    L(148, [FILL, FILL, FILL], density="light", role="remix introduce 3"),
    L(149, ["Crystal_Maiden", "Pudge"], "Mirror Mode", "The Hidden Hero", density="medium", role="expert Mirror + Hidden Hero"),
    L(150, ["Wraith_King", "Tidehunter"], "Soundquake Cataclysm", "Hidden Marks", density="medium", timer=20, role="finale Cataclysm + Hidden Marks"),
]


def correct_count(heroes: list[str]) -> int:
    return sum(SOUND_COUNTS[h] for h in heroes)


def pack_max_sounds(
    heroes: list[str], density: str, override: int | None, level: int = 1
) -> int:
    if override is not None:
        return override
    correct = correct_count(heroes)
    lo, hi = DENSITY_EXTRA[density]
    allowed = LEGAL_BOARD if level >= 121 else LEGAL_BOARD - {28}
    legal = []
    for extra in range(lo, hi + 1):
        total = correct + extra
        if total in allowed:
            legal.append(total)
    if not legal:
        raise ValueError(
            f"L{level}: no legal max_sounds for {heroes} density={density} correct={correct}"
        )
    return legal[-1]


def resolve_fills(levels: list[Spec]) -> None:
    named: set[str] = set()
    for spec in levels:
        for h in spec.heroes:
            if h != FILL:
                named.add(h)
    unused = [
        n
        for n in SOUND_COUNTS
        if n not in named and n not in HIGH_VOLUME
    ]
    unused.sort(key=lambda n: (SOUND_COUNTS[n], n))
    fill_slots = sum(h == FILL for spec in levels for h in spec.heroes)
    if fill_slots != len(unused):
        raise SystemExit(
            f"FILL slots {fill_slots} != unused heroes {len(unused)} "
            f"(named unique {len(named)}, leftover {unused})"
        )
    i = 0
    for spec in levels:
        out: list[str] = []
        for h in spec.heroes:
            if h == FILL:
                out.append(unused[i])
                i += 1
            else:
                out.append(h)
        spec.resolved = out


def density_extra(correct: int, max_sounds: int) -> str:
    extra = max_sounds - correct
    if extra <= 6:
        return "light"
    if extra <= 10:
        return "medium"
    return "heavy"


def validate(levels: list[Spec]) -> list[str]:
    errors: list[str] = []
    if [s.level for s in levels] != list(range(1, 151)):
        errors.append("levels must be 1..150 consecutive")

    seen: dict[str, int] = {}
    seen_before: dict[int, set[str]] = {}
    familiar_at: dict[int, set[str]] = {}

    for spec in levels:
        heroes = spec.names()
        familiar_at[spec.level] = {h for h in heroes if seen.get(h, 0) > 0}
        seen_before[spec.level] = {h for h, c in seen.items() if c > 0}
        for h in heroes:
            seen[h] = seen.get(h, 0) + 1

        if len(heroes) != len(set(heroes)):
            errors.append(f"L{spec.level}: duplicate Heroes {heroes}")
        if len(heroes) > 4:
            errors.append(f"L{spec.level}: 5+ Heroes")
        if len(heroes) == 4 and spec.level < 121:
            errors.append(f"L{spec.level}: 4 Heroes only in remix")
        if len(heroes) == 4 and spec.affixes and not set(spec.affixes) <= EASY:
            errors.append(f"L{spec.level}: 4 Heroes only with no/easy Affix")
        if any(h not in SOUND_COUNTS for h in heroes):
            errors.append(f"L{spec.level}: unknown Hero in {heroes}")
            continue

        for hv in HIGH_VOLUME:
            if hv in heroes and spec.level < 106:
                errors.append(f"L{spec.level}: high-volume {hv} too early")

        correct = correct_count(heroes)
        ms = pack_max_sounds(heroes, spec.density, spec.max_sounds, spec.level)
        extra = ms - correct
        if extra < 2:
            errors.append(f"L{spec.level}: decoys {extra} < 2 (correct={correct} max={ms})")
        if ms not in LEGAL_BOARD:
            errors.append(f"L{spec.level}: max_sounds {ms} not in {sorted(LEGAL_BOARD)}")
        if spec.max_sounds is None:
            lo, hi = DENSITY_EXTRA[spec.density]
            if not (lo <= extra <= hi):
                errors.append(
                    f"L{spec.level}: extra {extra} outside {spec.density} {lo}-{hi}"
                )
        if ms == 28 and spec.level < 121:
            errors.append(f"L{spec.level}: 28-card board only as late guest")

        aff = spec.affixes
        if len(aff) != len(set(aff)):
            errors.append(f"L{spec.level}: duplicate Affix")
        if "Fragile Spirit" in aff and "Sudden Death" in aff:
            errors.append(f"L{spec.level}: Fragile Spirit + Sudden Death")
        if sum(a in IDENTITY for a in aff) > 1:
            errors.append(f"L{spec.level}: two identity Affixes")
        if "Blurred Vision" in aff and "Blurred Vision 2" in aff:
            errors.append(f"L{spec.level}: both blur intensities")
        if any(a in ("Blurred Vision", "Blurred Vision 2") for a in aff) and "The Hidden Hero" in aff:
            errors.append(f"L{spec.level}: blur + Hidden Hero")
        if "Unknown Count" in aff and ("The Hidden Hero" in aff or "Among Heroes" in aff):
            errors.append(f"L{spec.level}: Unknown Count + identity")
        if "Mirror Mode" in aff and "Among Heroes" in aff:
            errors.append(f"L{spec.level}: Mirror + Among Heroes")
        if "Race Against Time" in aff and any(a in QUAKE for a in aff):
            errors.append(f"L{spec.level}: Race + Soundquake family")
        if sum(a in QUAKE for a in aff) > 1:
            errors.append(f"L{spec.level}: two Soundquake variants")
        if "Among Heroes" in aff:
            if spec.hidden is None or spec.hidden not in heroes:
                errors.append(f"L{spec.level}: Among Heroes needs hidden_hero_id in roster")
            if spec.hidden in spec.masked or spec.hidden in spec.blurred:
                errors.append(f"L{spec.level}: mystery id also masked/blurred")
            visible = [h for h in heroes if h != spec.hidden]
            if len(visible) < 1:
                errors.append(f"L{spec.level}: Among Heroes needs a visible Radiant")
            if len(heroes) < 2:
                errors.append(f"L{spec.level}: Among Heroes needs ≥2 Heroes")
        elif spec.hidden is not None:
            errors.append(f"L{spec.level}: hidden_hero_id without Among Heroes")
        if "Partial Veil" in aff:
            if not spec.masked or not set(spec.masked) <= set(heroes):
                errors.append(f"L{spec.level}: Partial Veil mask membership")
            if set(spec.masked) == set(heroes):
                errors.append(f"L{spec.level}: Partial Veil masking all (use Hidden Hero)")
            if len(heroes) < 2:
                errors.append(f"L{spec.level}: Partial Veil needs ≥2 Heroes")
        elif spec.masked:
            errors.append(f"L{spec.level}: masked_hero_ids without Partial Veil")
        if any(a in ("Blurred Vision", "Blurred Vision 2") for a in aff):
            if not spec.blurred or not set(spec.blurred) <= set(heroes):
                errors.append(f"L{spec.level}: blur membership")
            if spec.hidden and spec.hidden in spec.blurred:
                errors.append(f"L{spec.level}: blur on Among mystery")
        elif spec.blurred:
            errors.append(f"L{spec.level}: blurred_hero_ids without blur Affix")
        if "Race Against Time" in aff or any(a in QUAKE for a in aff):
            if spec.timer is None or spec.timer <= 0:
                errors.append(f"L{spec.level}: timer_seconds required")
        if "Echo Limit" in aff and spec.echo < 0:
            errors.append(f"L{spec.level}: negative echo offset")
        if "Mirror Mode" in aff and extra < 2:
            errors.append(f"L{spec.level}: Mirror Mode needs Decoys")
        if spec.level <= 20 and aff:
            errors.append(f"L{spec.level}: primer must have no Affix")
        if spec.level <= 20 and len(heroes) != (1 if spec.level <= 10 else 2):
            errors.append(f"L{spec.level}: primer Hero count")
        if spec.level < 121 and ("Soundquake Fracture" in aff or "Soundquake Cataclysm" in aff):
            errors.append(f"L{spec.level}: Fracture/Cataclysm only in remix")
        expert = (
            "Mirror Mode" in aff
            and ("The Hidden Hero" in aff or "Partial Veil" in aff)
        )
        if expert and spec.level < 135:
            errors.append(f"L{spec.level}: expert Mirror stack too early")
        if expert and len(heroes) == 4:
            errors.append(f"L{spec.level}: expert stack on 4 Heroes")

        prior = seen_before[spec.level]
        for family, window in TEACH_WINDOWS.items():
            if spec.level not in window or family not in aff:
                continue
            if family in EASY:
                if spec.level == window[0] and not any(h in prior for h in heroes):
                    errors.append(f"L{spec.level}: easy teach needs ≥1 familiar")
            else:
                if not set(heroes) <= prior:
                    errors.append(
                        f"L{spec.level}: {family} teach window needs all familiar {heroes}"
                    )
            if family == "Sudden Death" and (len(heroes) != 1 or len(aff) != 1):
                errors.append(f"L{spec.level}: Sudden Death teach is 1 Hero solo")

        mid_hard = [a for a in aff if a in MID or a in HARD]
        if mid_hard and spec.level not in {lv for wins in TEACH_WINDOWS.values() for lv in wins}:
            # After the teach window, still prefer familiar on mid/hard.
            if not set(heroes) <= prior and spec.level < 121:
                # Slice 3 UC 65 is the designed new+familiar after the window.
                if not (spec.level == 65 and "Unknown Count" in aff):
                    if not any(h in prior for h in heroes):
                        errors.append(f"L{spec.level}: mid/hard with zero familiar")

        solo_first = {
            27: "Hidden Marks",
            39: "Blurred Vision",
            46: "Fragile Spirit",
            52: "Sudden Death",
            62: "Unknown Count",
            66: "Echo Limit",
            70: "Race Against Time",
            82: "Partial Veil",
            91: "Soundquake",
            99: "The Hidden Hero",
            106: "Among Heroes",
            111: "Blurred Vision 2",
            115: "Soundquake Aftershock",
            118: "Mirror Mode",
            124: "Soundquake Fracture",
            126: "Soundquake Cataclysm",
        }
        if spec.level in solo_first:
            want = solo_first[spec.level]
            if aff != (want,):
                errors.append(f"L{spec.level}: teach {want} must be solo, got {aff}")

        if len(aff) >= 2:
            for a in aff:
                taught_by = min(
                    (lv for fam, wins in TEACH_WINDOWS.items() if fam == a for lv in wins),
                    default=None,
                )
                if taught_by is None:
                    # Echo Medium/Hard reuse Echo Limit family (taught 66).
                    if a != "Echo Limit":
                        errors.append(f"L{spec.level}: stack member {a} has no teach window")
                elif spec.level <= taught_by:
                    errors.append(f"L{spec.level}: stacked {a} before/on teach")

    missing = [n for n in SOUND_COUNTS if n not in seen]
    if missing:
        errors.append(f"unused Heroes: {missing}")
    return errors


def sql_str(name: str) -> str:
    return "'" + name.replace("'", "''") + "'"


def text_array(names: list[str] | tuple[str, ...]) -> str:
    if not names:
        return "ARRAY[]::text[]"
    return "ARRAY[" + ", ".join(sql_str(n) for n in names) + "]"


def emit_sql(levels: list[Spec]) -> str:
    rows = []
    for spec in levels:
        heroes = spec.names()
        ms = pack_max_sounds(heroes, spec.density, spec.max_sounds, spec.level)
        hidden = "NULL" if spec.hidden is None else sql_str(spec.hidden)
        timer = "NULL" if spec.timer is None else str(spec.timer)
        cols = [
            str(spec.level),
            text_array(heroes),
            text_array(spec.affixes),
            str(ms),
            text_array(spec.masked),
            text_array(spec.blurred),
            hidden,
            timer,
            str(spec.echo),
        ]
        if spec.level == 1:
            cols = [
                f"{spec.level}::smallint",
                text_array(heroes),
                text_array(spec.affixes),
                f"{ms}::smallint",
                text_array(spec.masked),
                text_array(spec.blurred),
                "NULL::text",
                "NULL::integer",
                f"{spec.echo}::smallint",
            ]
        rows.append("(" + ", ".join(cols) + ")")
    values = ",\n".join(rows)
    return f"""-- Journey catalog 150. Generated by scripts/journey-catalog/catalog.py.
-- Looks up casters.name / affixes.affix — do not paste staging numeric FKs.
-- Staging tester reset is reset-staging-testers.sql (do NOT run that on prod).
-- Single statement so one MCP/SQL session can apply the whole catalog.

WITH seed (
  level,
  hero_names,
  affix_names,
  max_sounds,
  masked_names,
  blurred_names,
  hidden_name,
  timer_seconds,
  echo_limit_offset
) AS (
  VALUES
{values}
),
missing_heroes AS (
  SELECT string_agg(DISTINCT n, ', ' ORDER BY n) AS names
  FROM seed s, unnest(s.hero_names || s.masked_names || s.blurred_names
       || CASE WHEN s.hidden_name IS NULL THEN ARRAY[]::text[] ELSE ARRAY[s.hidden_name] END) AS n
  WHERE NOT EXISTS (SELECT 1 FROM casters c WHERE c.name = n)
),
missing_affixes AS (
  SELECT string_agg(DISTINCT n, ', ' ORDER BY n) AS names
  FROM seed s, unnest(s.affix_names) AS n
  WHERE NOT EXISTS (SELECT 1 FROM affixes a WHERE a.affix = n)
),
_deleted AS (
  DELETE FROM journey RETURNING 1
)
INSERT INTO journey (
  id,
  level,
  radiant_heroes,
  dire_heroes,
  max_sounds,
  affixes,
  masked_hero_ids,
  blurred_hero_ids,
  hidden_hero_id,
  timer_seconds,
  timer_extension_seconds,
  echo_limit_offset
)
SELECT
  s.level,
  s.level,
  ARRAY(
    SELECT c.id
    FROM unnest(s.hero_names) WITH ORDINALITY AS u(n, ord)
    JOIN casters c ON c.name = u.n
    ORDER BY u.ord
  ),
  '{{}}'::bigint[],
  s.max_sounds,
  ARRAY(
    SELECT a.id
    FROM unnest(s.affix_names) WITH ORDINALITY AS u(n, ord)
    JOIN affixes a ON a.affix = u.n
    ORDER BY u.ord
  ),
  ARRAY(
    SELECT c.id
    FROM unnest(s.masked_names) WITH ORDINALITY AS u(n, ord)
    JOIN casters c ON c.name = u.n
    ORDER BY u.ord
  ),
  ARRAY(
    SELECT c.id
    FROM unnest(s.blurred_names) WITH ORDINALITY AS u(n, ord)
    JOIN casters c ON c.name = u.n
    ORDER BY u.ord
  )::smallint[],
  (SELECT c.id FROM casters c WHERE c.name = s.hidden_name),
  s.timer_seconds,
  20,
  s.echo_limit_offset
FROM seed s
WHERE (SELECT COUNT(*) FROM _deleted) >= 0
  AND 1 / CASE WHEN (SELECT names FROM missing_heroes) IS NULL THEN 1 ELSE 0 END = 1
  AND 1 / CASE WHEN (SELECT names FROM missing_affixes) IS NULL THEN 1 ELSE 0 END = 1
RETURNING level;
"""


def write_human_table(levels: list[Spec], path: Path) -> None:
    lines = [
        "# Journey catalog 150 (generated)",
        "",
        "Source: `catalog.py`. Do not edit this file by hand.",
        "",
        "| Level | Heroes | Affixes | max_sounds | decoys | density | extras | role |",
        "|---|---|---|---|---|---|---|---|",
    ]
    for spec in levels:
        heroes = spec.names()
        ms = pack_max_sounds(heroes, spec.density, spec.max_sounds, spec.level)
        correct = correct_count(heroes)
        extra_bits = []
        if spec.masked:
            extra_bits.append("mask " + ", ".join(spec.masked))
        if spec.blurred:
            extra_bits.append("blur " + ", ".join(spec.blurred))
        if spec.hidden:
            extra_bits.append("hide " + spec.hidden)
        if spec.timer is not None:
            extra_bits.append(f"t={spec.timer}")
        if "Echo Limit" in spec.affixes:
            extra_bits.append(f"echo {spec.echo:+d}")
        lines.append(
            "| {lvl} | {heroes} | {aff} | {ms} | {dec} | {den} | {ex} | {role} |".format(
                lvl=spec.level,
                heroes=", ".join(heroes),
                aff=", ".join(spec.affixes) or "—",
                ms=ms,
                dec=ms - correct,
                den=spec.density,
                ex="; ".join(extra_bits) or "—",
                role=spec.role,
            )
        )
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    here = Path(__file__).resolve().parent
    resolve_fills(LEVELS)
    errors = validate(LEVELS)
    if errors:
        print("VALIDATION FAILED", file=sys.stderr)
        for e in errors:
            print(f"  - {e}", file=sys.stderr)
        return 1
    sql = emit_sql(LEVELS)
    (here / "apply.sql").write_text(sql, encoding="utf-8")
    write_human_table(LEVELS, here / "CATALOG.md")
    print(f"ok: 150 levels, wrote {here / 'apply.sql'} and {here / 'CATALOG.md'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

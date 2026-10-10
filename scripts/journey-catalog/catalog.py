#!/usr/bin/env python3
"""Journey catalog seed (150 levels) — source of truth.

Assist-retuned: earlier Affix teaches, earlier legal stacks (incl. 3–4),
Echo/Race/Soundquake params used for teach ease vs later pressure.

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

# Assist-retuned teach windows. Hero pairs/rosters capped at 3 repeats.
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
    "Among Heroes": (98, 99),
    "Blurred Vision 2": (104, 105, 106),
    "Soundquake Fracture": (121, 122),
    "Soundquake Cataclysm": (125, 126),
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


# Ground-up Assist retune: Mirror ~40, varied stacks (not always HM),
# longer Aftershock/Cataclysm intervals, wide Hero rotation.
# FILL = unused Hero not yet named on an earlier level.
LEVELS: list[Spec] = [
    L(1, ['Omniknight'], density='light', max_sounds=8, role='primer'),
    L(2, ['Shadow_Fiend'], density='light', max_sounds=8, role='primer'),
    L(3, ['Bloodseeker'], density='medium', max_sounds=12, role='primer'),
    L(4, ['Bristleback'], density='medium', max_sounds=12, role='primer'),
    L(5, ['Chen'], density='medium', max_sounds=12, role='primer'),
    L(6, ['Crystal_Maiden'], density='medium', max_sounds=12, role='primer'),
    L(7, ['Dragon_Knight'], density='medium', max_sounds=12, role='primer'),
    L(8, ['Earthshaker'], density='medium', max_sounds=12, role='primer'),
    L(9, ['Elder_Titan'], density='medium', max_sounds=12, role='guest decoys'),
    L(10, ['Lifestealer'], density='medium', max_sounds=12, role='guest decoys'),
    L(11, ['Luna', 'Lycan'], density='light', max_sounds=12, role='teach 2 Heroes'),
    L(12, ['Marci', 'Necrophos'], density='light', max_sounds=12, role='plateau'),
    L(13, ['Night_Stalker', 'Outworld_Destroyer'], density='medium', max_sounds=16, role='plateau'),
    L(14, ['Pudge', 'Queen_of_Pain'], density='medium', max_sounds=16, role='plateau'),
    L(15, ['Rubick', 'Slark'], density='medium', max_sounds=16, role='plateau'),
    L(16, ['Troll_Warlord', 'Tusk'], density='medium', max_sounds=16, role='plateau'),
    L(17, ['Underlord', 'Ursa'], density='medium', max_sounds=16, role='plateau'),
    L(18, ['Vengeful_Spirit', 'Venomancer'], density='medium', max_sounds=16, role='plateau'),
    L(19, ['Visage', 'Abaddon'], density='medium', max_sounds=16, role='fat decoys'),
    L(20, ['Alchemist', 'Ancient_Apparition'], density='medium', max_sounds=20, role='fat decoys'),
    L(21, ['Omniknight', 'Shadow_Fiend'], 'Hidden Marks', density='light', role='teach Hidden Marks'),
    L(22, ['Bloodseeker', 'Bristleback'], 'Hidden Marks', density='light', role='Hidden Marks mini-set'),
    L(23, ['Chen', 'Crystal_Maiden'], 'Hidden Marks', density='medium', role='Hidden Marks mini-set'),
    L(24, ['Anti-Mage', 'Arc_Warden', 'Axe'], density='light', role='guest 3 Heroes'),
    L(25, ['Bane', 'Batrider', 'Bounty_Hunter'], density='light', role='introduce 3'),
    L(26, ['Brewmaster', 'Broodmother', 'Dragon_Knight'], 'Hidden Marks', density='light', role='Hidden Marks new+familiar'),
    L(27, ['Centaur_Warrunner', 'Chaos_Knight', 'Dawnbreaker'], density='light', role='introduce 3'),
    L(28, ['Earthshaker', 'Elder_Titan'], 'Hidden Marks', density='medium', role='Hidden Marks return'),
    L(29, ['Lifestealer', 'Luna'], 'Blurred Vision', density='medium', blurred=('Lifestealer',), role='teach Blurred Vision'),
    L(30, ['Lycan', 'Marci'], 'Blurred Vision', density='medium', blurred=('Lycan',), role='Blurred Vision mini-set'),
    L(31, ['Necrophos', 'Night_Stalker'], 'Blurred Vision', density='medium', blurred=('Necrophos',), role='Blurred Vision mini-set'),
    L(32, ['Outworld_Destroyer', 'Pudge'], 'Hidden Marks', 'Blurred Vision', density='medium', blurred=('Outworld_Destroyer',), role='first stack HM+Blur'),
    L(33, ['Dazzle', 'Death_Prophet', 'Doom'], density='light', role='introduce 3'),
    L(34, ['Queen_of_Pain', 'Rubick'], 'Blurred Vision', density='medium', blurred=('Queen_of_Pain',), role='Blurred Vision return'),
    L(35, ['Enigma', 'Faceless_Void', 'Slark'], 'Hidden Marks', density='light', role='Hidden Marks new+familiar'),
    L(36, ['Troll_Warlord'], 'Fragile Spirit', density='medium', max_sounds=12, role='teach Fragile Spirit'),
    L(37, ['Tusk', 'Underlord'], 'Fragile Spirit', density='medium', role='Fragile Spirit mini-set'),
    L(38, ['Ursa', 'Vengeful_Spirit'], 'Fragile Spirit', density='medium', role='Fragile Spirit mini-set'),
    L(39, ['Venomancer', 'Visage'], 'Fragile Spirit', 'Blurred Vision', density='medium', blurred=('Venomancer',), role='Fragile+Blur'),
    L(40, ['Abaddon', 'Alchemist'], 'Mirror Mode', density='heavy', max_sounds=16, role='teach Mirror Mode'),
    L(41, ['Ancient_Apparition', 'Anti-Mage'], 'Mirror Mode', density='medium', role='Mirror Mode mini-set'),
    L(42, ['Arc_Warden', 'Axe'], 'Mirror Mode', density='medium', role='Mirror Mode mini-set'),
    L(43, ['Bane', 'Batrider'], 'Mirror Mode', 'Blurred Vision', density='medium', blurred=('Bane',), role='Mirror+Blur'),
    L(44, ['Gyrocopter', 'Hoodwink', 'Huskar'], density='light', role='introduce 3'),
    L(45, ['Io', 'Bounty_Hunter', 'Brewmaster'], 'Mirror Mode', 'Hidden Marks', density='light', role='Mirror+HM'),
    L(46, ['Broodmother'], 'Sudden Death', density='medium', max_sounds=12, role='guest Sudden Death'),
    L(47, ['Jakiro', 'Juggernaut', 'Legion_Commander'], density='light', role='recover introduce 3'),
    L(48, ['Lina', 'Lion', 'Lone_Druid'], density='light', role='recover introduce 3'),
    L(49, ['Centaur_Warrunner', 'Chaos_Knight'], 'Fragile Spirit', density='medium', role='Fragile return'),
    L(50, ['Dawnbreaker', 'Dazzle'], 'Unknown Count', density='medium', role='teach Unknown Count'),
    L(51, ['Death_Prophet', 'Doom'], 'Unknown Count', density='medium', role='Unknown Count mini-set'),
    L(52, ['Enigma', 'Faceless_Void'], 'Unknown Count', density='medium', role='Unknown Count mini-set'),
    L(53, ['Magnus', 'Gyrocopter', 'Hoodwink'], 'Unknown Count', density='light', role='Unknown Count new+familiar'),
    L(54, ['Mars', 'Huskar', 'Io'], 'Unknown Count', 'Blurred Vision', density='light', blurred=('Mars',), role='UC+Blur'),
    L(55, ['Medusa', 'Meepo', 'Mirana'], density='light', role='introduce 3'),
    L(56, ['Jakiro', 'Juggernaut'], 'Echo Limit', density='medium', echo=10, role='teach Echo Limit Easy'),
    L(57, ['Legion_Commander', 'Lina'], 'Echo Limit', density='medium', echo=10, role='Echo Limit Easy'),
    L(58, ['Lion', 'Lone_Druid'], 'Echo Limit', density='medium', echo=10, role='Echo Limit Easy'),
    L(59, ['Magnus', 'Mars'], 'Echo Limit', 'Blurred Vision', density='medium', blurred=('Magnus',), echo=10, role='Echo+Blur'),
    L(60, ['Medusa', 'Meepo'], 'Echo Limit', 'Mirror Mode', density='medium', echo=10, role='Echo+Mirror'),
    L(61, ['Morphling', 'Muerta', 'Naga_Siren'], density='light', role='introduce 3'),
    L(62, ['Mirana', 'Morphling'], 'Race Against Time', density='medium', timer=120, role='teach Race'),
    L(63, ['Muerta', 'Naga_Siren'], 'Race Against Time', density='medium', timer=110, role='Race'),
    L(64, ['Omniknight', 'Shadow_Fiend'], 'Race Against Time', density='medium', timer=100, role='Race'),
    L(65, ["Nature's_Prophet", 'Ogre_Magi', 'Phantom_Lancer'], density='light', role='no-timer introduce 3'),
    L(66, ['Phoenix', "Nature's_Prophet", 'Ogre_Magi'], 'Race Against Time', 'Blurred Vision', density='light', blurred=('Phoenix',), timer=100, role='Race+Blur'),
    L(67, ['Phantom_Lancer', 'Phoenix'], 'Race Against Time', 'Mirror Mode', density='medium', timer=95, role='Race+Mirror'),
    L(68, ['Bloodseeker'], 'Fragile Spirit', density='medium', max_sounds=12, role='guest Fragile Spirit'),
    L(69, ['Bristleback'], 'Sudden Death', density='medium', max_sounds=12, role='guest Sudden Death'),
    L(70, ['Chen', 'Crystal_Maiden'], 'Partial Veil', density='medium', masked=('Crystal_Maiden',), role='teach Partial Veil'),
    L(71, ['Dragon_Knight', 'Earthshaker'], 'Partial Veil', density='medium', masked=('Earthshaker',), role='Partial Veil mini-set'),
    L(72, ['Elder_Titan', 'Lifestealer'], 'Partial Veil', density='medium', masked=('Lifestealer',), role='Partial Veil mini-set'),
    L(73, ['Luna', 'Lycan', 'Marci'], 'Partial Veil', density='medium', masked=('Marci',), role='Partial Veil 3 Heroes'),
    L(74, ['Necrophos', 'Night_Stalker'], 'Partial Veil', 'Blurred Vision', density='medium', masked=('Necrophos',), blurred=('Night_Stalker',), role='PV+Blur'),
    L(75, ['Primal_Beast', 'Outworld_Destroyer', 'Pudge'], 'Partial Veil', density='light', masked=('Pudge',), role='Partial Veil'),
    L(76, ['Primal_Beast', 'Queen_of_Pain'], 'Partial Veil', 'Echo Limit', density='medium', masked=('Queen_of_Pain',), echo=10, role='PV+Echo'),
    L(77, ['Puck', 'Pugna', 'Razor'], density='light', role='introduce 3'),
    L(78, ['Puck', 'Pugna'], 'Soundquake', density='medium', timer=25, role='teach Soundquake'),
    L(79, ['Razor', 'Rubick'], 'Soundquake', density='medium', timer=22, role='Soundquake mini-set'),
    L(80, ['Slark', 'Troll_Warlord'], 'Soundquake', density='medium', timer=20, role='Soundquake mini-set'),
    L(81, ['Riki', 'Tusk', 'Underlord'], 'Soundquake', density='light', timer=22, role='Soundquake'),
    L(82, ['Shadow_Demon', 'Riki', 'Ursa'], 'Soundquake', 'Blurred Vision', density='light', blurred=('Shadow_Demon',), timer=22, role='SQ+Blur'),
    L(83, ['Shadow_Demon', 'Vengeful_Spirit'], 'Soundquake', 'Mirror Mode', density='medium', timer=25, role='SQ+Mirror'),
    L(84, ['Shadow_Shaman', 'Silencer', 'Skywrath_Mage'], density='light', role='introduce 3'),
    L(85, ['Shadow_Shaman', 'Silencer'], 'Soundquake Aftershock', density='medium', timer=70, role='teach Aftershock'),
    L(86, ['Skywrath_Mage', 'Venomancer'], 'Soundquake Aftershock', density='medium', timer=65, role='Aftershock mini-set'),
    L(87, ['Visage', 'Abaddon'], 'Soundquake Aftershock', density='medium', timer=65, role='Aftershock mini-set'),
    L(88, ['Slardar', 'Alchemist', 'Ancient_Apparition'], 'Soundquake Aftershock', 'Blurred Vision', density='light', blurred=('Slardar',), timer=70, role='Aftershock+Blur'),
    L(89, ['Slardar', 'Anti-Mage'], 'Soundquake Aftershock', 'Echo Limit', density='medium', timer=60, echo=10, role='Aftershock+Echo'),
    L(90, ['Snapfire', 'Sniper', 'Spectre'], density='light', role='introduce 3'),
    L(91, ['Snapfire', 'Sniper'], 'Mirror Mode', density='heavy', max_sounds=16, role='Mirror return'),
    L(92, ['Spectre', 'Arc_Warden'], 'The Hidden Hero', density='medium', role='teach The Hidden Hero'),
    L(93, ['Axe', 'Bane'], 'The Hidden Hero', density='medium', role='Hidden Hero mini-set'),
    L(94, ['Batrider', 'Bounty_Hunter'], 'The Hidden Hero', density='medium', role='Hidden Hero mini-set'),
    L(95, ['Spirit_Breaker', 'Storm_Spirit', 'Sven'], density='light', role='drop introduce 3'),
    L(96, ['Spirit_Breaker', 'Storm_Spirit'], 'The Hidden Hero', 'Fragile Spirit', density='medium', role='HH+Fragile'),
    L(97, ['Sven', 'Brewmaster'], 'The Hidden Hero', 'Echo Limit', density='medium', echo=10, role='HH+Echo'),
    L(98, ['Broodmother', 'Centaur_Warrunner'], 'Among Heroes', density='medium', hidden='Centaur_Warrunner', role='teach Among Heroes'),
    L(99, ['Chaos_Knight', 'Dawnbreaker'], 'Among Heroes', density='medium', hidden='Dawnbreaker', role='Among Heroes mini-set'),
    L(100, ['Dazzle', 'Death_Prophet'], 'Partial Veil', density='medium', masked=('Death_Prophet',), role='Partial Veil return'),
    L(101, ['Doom', 'Enigma', 'Faceless_Void'], 'Echo Limit', density='light', echo=10, role='Echo Limit Medium'),
    L(102, ['Gyrocopter', 'Hoodwink'], 'Partial Veil', 'Blurred Vision', density='medium', masked=('Hoodwink',), blurred=('Gyrocopter',), role='PV+Blur'),
    L(103, ['Huskar', 'Io'], 'The Hidden Hero', 'Echo Limit', density='medium', echo=10, role='HH+Echo'),
    L(104, ['Jakiro', 'Juggernaut'], 'Blurred Vision 2', density='medium', blurred=('Jakiro', 'Juggernaut'), role='teach Blurred Vision 2'),
    L(105, ['Legion_Commander', 'Lina'], 'Blurred Vision 2', density='medium', blurred=('Legion_Commander',), role='Blurred Vision 2 mini-set'),
    L(106, ['Lion', 'Lone_Druid'], 'Blurred Vision 2', density='medium', blurred=('Lion', 'Lone_Druid'), role='Blurred Vision 2 mini-set'),
    L(107, ['Tidehunter', 'Magnus', 'Mars'], 'Blurred Vision 2', 'Echo Limit', density='light', blurred=('Tidehunter',), echo=10, role='BV2+Echo'),
    L(108, ['Tidehunter', 'Medusa'], 'Echo Limit', 'Mirror Mode', 'Blurred Vision', density='medium', blurred=('Tidehunter',), echo=10, role='3-stack Echo+Mirror+Blur'),
    L(109, ['Meepo', 'Mirana'], 'Among Heroes', density='medium', hidden='Mirana', role='Among Heroes return'),
    L(110, ['Timbersaw', 'Tinker', 'Tiny'], density='light', role='rollercoaster introduce 3'),
    L(111, ['Timbersaw', 'Tinker'], 'Race Against Time', 'Blurred Vision', 'Echo Limit', density='medium', blurred=('Timbersaw',), timer=80, role='3-stack Race+Blur+Echo'),
    L(112, ['Undying', 'Tiny', 'Morphling'], 'Echo Limit', density='light', role='Echo Limit Medium'),
    L(113, ['Undying', 'Muerta'], 'Soundquake', 'Mirror Mode', density='medium', timer=28, role='SQ+Mirror'),
    L(114, ['Viper', 'Naga_Siren', "Nature's_Prophet"], 'Race Against Time', density='light', timer=75, role='tight Race'),
    L(115, ['Viper', 'Ogre_Magi'], 'Echo Limit', 'Blurred Vision', 'Fragile Spirit', density='medium', blurred=('Viper',), role='3-stack Echo+Blur+Fragile'),
    L(116, ['Void_Spirit', 'Warlock', 'Weaver'], 'Hidden Marks', density='light', role='easy introduce 3'),
    L(117, ['Winter_Wyvern', 'Void_Spirit', 'Warlock'], 'Partial Veil', 'Echo Limit', density='light', masked=('Warlock',), role='PV+Echo Med'),
    L(118, ['Weaver', 'Winter_Wyvern', 'Phantom_Lancer'], density='light', role='guest 3 Heroes familiar'),
    L(119, ['Phoenix', 'Primal_Beast'], 'Unknown Count', 'Blurred Vision', 'Echo Limit', density='medium', blurred=('Phoenix',), echo=10, role='3-stack UC+Blur+Echo'),
    L(120, ['Witch_Doctor', 'Puck', 'Pugna'], 'Mirror Mode', 'Echo Limit', density='light', echo=10, role='Mirror+Echo'),
    L(121, ['Witch_Doctor', 'Razor'], 'Soundquake Fracture', density='medium', timer=28, role='teach Fracture'),
    L(122, ['Riki', 'Shadow_Demon'], 'Soundquake Fracture', 'Blurred Vision', density='medium', blurred=('Riki',), timer=26, role='Fracture+Blur'),
    L(123, ['Wraith_King', 'Zeus', 'Beastmaster'], density='light', role='remix introduce 3'),
    L(124, ['Dark_Seer', 'Wraith_King', 'Zeus'], 'Soundquake Fracture', 'Echo Limit', density='light', timer=26, role='Fracture+Echo'),
    L(125, ['Beastmaster', 'Dark_Seer'], 'Soundquake Cataclysm', density='medium', timer=75, role='teach Cataclysm'),
    L(126, ['Shadow_Shaman', 'Silencer'], 'Soundquake Cataclysm', density='medium', timer=70, role='Cataclysm mini-set'),
    L(127, ['Ringmaster', 'Dark_Willow'], density='light', role='high-volume Ringmaster'),
    L(128, ['Kez', 'Enchantress'], density='medium', role='high-volume Kez'),
    L(129, ['Invoker', 'Drow_Ranger'], density='medium', role='high-volume Invoker'),
    L(130, ['Grimstroke', 'Drow_Ranger', 'Ember_Spirit'], 'Echo Limit', density='light', echo=0, role='guest Echo Hard'),
    L(131, ['Enchantress', 'Grimstroke'], 'Mirror Mode', 'The Hidden Hero', density='medium', role='expert Mirror+Hidden Hero'),
    L(132, ['Skywrath_Mage', 'Slardar'], 'Mirror Mode', 'Partial Veil', density='medium', masked=('Slardar',), role='expert Mirror+Partial Veil'),
    L(133, ['Keeper_of_the_Light', 'Kunkka', 'Largo'], density='light', role='remix introduce 3'),
    L(134, ['Leshrac', 'Keeper_of_the_Light', 'Kunkka'], 'Unknown Count', 'Blurred Vision', 'Fragile Spirit', density='light', blurred=('Leshrac',), role='3-stack UC+Blur+Fragile'),
    L(135, ['Largo', 'Leshrac'], 'Among Heroes', 'Blurred Vision', 'Echo Limit', density='medium', blurred=('Largo',), hidden='Leshrac', echo=5, role='Among+Blur+Echo'),
    L(136, ['Lich', 'Nyx_Assassin', 'Oracle'], density='light', role='remix introduce 3'),
    L(137, ['Lich', 'Nyx_Assassin'], 'Soundquake', 'Mirror Mode', 'Blurred Vision', density='medium', blurred=('Lich',), timer=22, role='3-stack SQ+Mirror+Blur'),
    L(138, ['Pangolier', 'Oracle', 'Snapfire'], 'Race Against Time', 'Blurred Vision', 'Echo Limit', density='light', blurred=('Pangolier',), timer=70, role='3-stack tight Race+Blur+Echo'),
    L(139, ['Phantom_Assassin', 'Sand_King', 'Techies'], density='light', role='remix introduce 3'),
    L(140, ['Templar_Assassin', 'Terrorblade', 'Windranger'], density='light', role='remix introduce 3'),
    L(141, ['Clinkz', 'Pangolier', 'Phantom_Assassin'], 'Soundquake', 'Echo Limit', 'Blurred Vision', density='light', blurred=('Clinkz',), timer=28, echo=5, role='3-stack SQ+Echo+Blur'),
    L(142, ['Sand_King', 'Techies', 'Templar_Assassin', 'Terrorblade'], 'Fragile Spirit', density='light', role='guest 4 Heroes Fragile'),
    L(143, ['Clockwerk', 'Disruptor', 'Earth_Spirit'], density='light', role='remix introduce 3'),
    L(144, ['Windranger', 'Clinkz'], 'Mirror Mode', 'The Hidden Hero', density='medium', role='expert Mirror+Hidden Hero'),
    L(145, ['Clockwerk', 'Disruptor', 'Earth_Spirit'], 'Partial Veil', 'Blurred Vision', 'Fragile Spirit', 'Echo Limit', density='medium', masked=('Earth_Spirit',), blurred=('Clockwerk',), echo=5, role='4-stack PV+Blur+Fragile+Echo'),
    L(146, ['Sniper', 'Spectre'], 'Race Against Time', 'Mirror Mode', 'Blurred Vision', 'Echo Limit', density='medium', blurred=('Sniper',), timer=75, role='4-stack Race+Mirror+Blur+Echo'),
    L(147, ['Treant_Protector', 'Spirit_Breaker', 'Storm_Spirit'], 'Soundquake Fracture', 'Blurred Vision', 'Echo Limit', density='light', blurred=('Treant_Protector',), timer=32, echo=5, role='3-stack Fracture+Blur+Echo'),
    L(148, ['Monkey_King', 'Treant_Protector', 'Sven'], density='medium', role='remix introduce 3'),
    L(149, ['Monkey_King', 'Tidehunter', 'Timbersaw'], 'Echo Limit', density='light', echo=0, role='guest Echo Hard'),
    L(150, ['Tinker', 'Tiny'], 'Soundquake Cataclysm', 'Mirror Mode', 'Blurred Vision', 'Echo Limit', density='medium', blurred=('Tinker',), timer=75, role='finale 4-stack Cataclysm+Mirror+Blur+Echo'),
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
        # Never debut a Hero behind full-mystery identity (all ? or omitted).
        if "The Hidden Hero" in aff or "Among Heroes" in aff:
            known = seen_before[spec.level]
            new_on_board = [h for h in heroes if h not in known]
            if new_on_board:
                errors.append(
                    f"L{spec.level}: new Hero on identity Affix (need prior normal portrait): {new_on_board}"
                )
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
        if expert and spec.level < 120:
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
        teach_levels = {lv for wins in TEACH_WINDOWS.values() for lv in wins}
        if mid_hard and spec.level not in teach_levels:
            # After teach window: all-familiar preferred; allow exactly one new
            # on the first level after a family's window (new+familiar beat).
            if not set(heroes) <= prior and spec.level < 121:
                new_heroes = [h for h in heroes if h not in prior]
                if len(new_heroes) > 1 or not any(h in prior for h in heroes):
                    # Exception: easy+mid stacks may carry one new on easy-tier boards
                    if not (len(new_heroes) == 1 and any(h in prior for h in heroes)):
                        errors.append(f"L{spec.level}: mid/hard with too many new Heroes {new_heroes}")

        solo_first = {wins[0]: family for family, wins in TEACH_WINDOWS.items()}
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
    overused = [f"{h}x{c}" for h, c in sorted(seen.items(), key=lambda x: -x[1]) if c > 16]
    if overused:
        errors.append(f"Hero overuse (>16 appearances): {', '.join(overused[:12])}")
    from collections import defaultdict as _dd
    _rosters = _dd(list)
    _pairs = _dd(list)
    for spec in levels:
        hs = tuple(sorted(spec.names()))
        _rosters[hs].append(spec.level)
        for i in range(len(hs)):
            for j in range(i + 1, len(hs)):
                _pairs[(hs[i], hs[j])].append(spec.level)
    for hs, lvls in _rosters.items():
        if len(lvls) > 3:
            errors.append(f"roster >3: {', '.join(hs)} @ {lvls}")
    for pair, lvls in _pairs.items():
        if len(lvls) > 3:
            errors.append(f"pair >3: {pair[0]}+{pair[1]} @ {lvls}")
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
        "| Level | #H | Heroes | Affixes | correct | decoys | total | density | extras | role | playtest |",
        "|---|---|---|---|---|---|---|---|---|---|---|",
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
        aff = spec.affixes
        playtest = ""
        hard_hits = sum(1 for a in aff if a in HARD)
        if len(aff) >= 4 or (len(aff) >= 3 and hard_hits >= 1) or hard_hits >= 2:
            playtest = "hard"
        elif "Sudden Death" in aff or "Mirror Mode" in aff or any(a in ("Soundquake Fracture", "Soundquake Cataclysm") for a in aff):
            playtest = "tricky"
        elif len(aff) >= 3 or ("Echo Limit" in aff and spec.echo <= 0) or (
            "Race Against Time" in aff and spec.timer is not None and spec.timer <= 75
        ):
            playtest = "tricky"
        elif "Among Heroes" in aff or "The Hidden Hero" in aff:
            playtest = "check"
        lines.append(
            "| {lvl} | {nh} | {heroes} | {aff} | {cor} | {dec} | {ms} | {den} | {ex} | {role} | {pt} |".format(
                lvl=spec.level,
                nh=len(heroes),
                heroes=", ".join(heroes),
                aff=", ".join(aff) or "—",
                cor=correct,
                dec=ms - correct,
                ms=ms,
                den=spec.density,
                ex="; ".join(extra_bits) or "—",
                role=spec.role,
                pt=playtest or "—",
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

# Journey catalog band map (150)

Peak toolkit around level **120**. Levels **121–150** are **remix** at that peak. `dire_heroes` stays empty. Active Issue Chapter sum is **150**; board Heroes do not pick the Issue.

**Toolkit** = loads already taught. A **guest** uses a higher load once or twice, then the curve returns to mix.

A **familiar** Hero has already appeared on an earlier Journey level (catalog sounds, not the Dota kit).

**Decoy density** is its own axis (skill `journey-level-authoring`): extra cards = `max_sounds` − roster spell count. Light 2–6, medium 7–10, heavy 11+. The seed does not list Decoy spell ids.

## Affix tiers

| Tier | Affixes | **Familiar** bar |
|---|---|---|
| Easy | Hidden Marks, Blurred Vision, Fragile Spirit | **Teach**: ≥1 familiar Hero on the board |
| Mid | Unknown Count, Echo Limit (Easy), Race Against Time (generous), Partial Veil, Soundquake, The Hidden Hero, Blurred Vision 2 | **Teach** + next 1–2 levels: **every** Hero familiar (prefer two prior appearances) |
| Hard | Sudden Death, Echo Limit Medium/Hard, Race (tight), Among Heroes, Soundquake Aftershock, Mirror Mode, Soundquake Fracture, Soundquake Cataclysm, any stack | Same as mid; Sudden Death **teach** is 1 familiar Hero and no other Affix |

New Heroes belong on **no-Affix** or easy plateau boards. Never stack until each Affix in the stack was taught solo.

## Unlock schedule

| Taught by | Load now in the toolkit |
|---|---|
| 1 | 1 Hero, decoy density (`max_sounds`) |
| 12 | 2 Heroes |
| 24 | 3 Heroes as **guest** (not a floor) |
| 30 | Hidden Marks (first Affix — easy) |
| 42 | Blurred Vision |
| 50 | Fragile Spirit |
| 52 | Sudden Death as **guest** (1 familiar Hero) |
| 62 | Unknown Count |
| 70 | Echo Limit Easy (`echo_limit_offset = 10`) |
| 78 | Race Against Time (generous 90–120s) |
| 90 | Partial Veil |
| 98 | Soundquake |
| 106 | The Hidden Hero |
| 114 | Among Heroes |
| 120 | Blurred Vision 2, Soundquake Aftershock, Mirror Mode (each taught in this band, solo, **familiar** boards) |
| 121–150 | **Remix**: Fracture, Cataclysm, 4-Hero **guest**, two-Affix legal stacks, expert stacks (Mirror + Hidden Hero / Partial Veil). Echo Medium, tighter Race as mix. |

## Slice 1 — levels 1–20 (primer only)

No Affixes. No 3-Hero boards. Learn the catalog spell set.

| Level | Radiant count | Affixes | `max_sounds` | Role |
|---|---|---|---|---|
| 1 | 1 (prefer 2-spell Hero) | none | 8 | Primer |
| 2 | 1 | none | 8 | Primer |
| 3 | 1 | none | 12 | Primer |
| 4 | 1 | none | 12 | Primer |
| 5 | 1 | none | 12 | Primer |
| 6 | 1 | none | 12 | Primer |
| 7 | 1 | none | 12 | Primer |
| 8 | 1 | none | 12 | Primer |
| 9 | 1 | none | 12 | **Guest** decoys |
| 10 | 1 | none | 12 | **Guest** decoys; 2 Heroes unlock after this |
| 11 | 2 | none | 12 | Teach 2 Heroes |
| 12 | 2 | none | 12 | Plateau |
| 13 | 2 | none | 16 | Plateau |
| 14 | 2 | none | 16 | Plateau |
| 15 | 2 | none | 16 | Plateau |
| 16 | 2 | none | 16 | More decoys |
| 17 | 2 | none | 16 | Plateau |
| 18 | 2 | none | 16 | Plateau |
| 19 | 2 | none | 16 | Fat decoys |
| 20 | 2 | none | 20 | Fat decoys |

Timers / echo / veil membership: unset.

## Slice 2 — levels 21–45

| Levels | Mix | Notes |
|---|---|---|
| 21–23 | 2 Heroes, no Affix | Keep introducing unused Heroes |
| 24 | 3 Heroes, no Affix, one repeat | **Guest** 3-Hero; then drop back |
| 25–26 | 2 Heroes, no Affix, fatter `max_sounds` | 3 is not the floor |
| 27–30 | Hidden Marks, 2 Heroes | **Teach** 27 with **two familiar** Heroes. Mini-set 28–29. 30 may mix one new + one familiar (easy tier) |
| 31–34 | Rollercoaster: 2–3 Heroes, no Affix or Hidden Marks returning | At most one 3-Hero level |
| 35–38 | 2 Heroes, Hidden Marks or none | New Heroes on the no-Affix rows |
| 39–42 | Blurred Vision, 2 Heroes | **Teach** 39: ≥1 familiar, blur some not all portraits |
| 43–45 | Drop blur; 2 Heroes, decoys or Hidden Marks | Other axis |

`max_sounds` 12, 16, or 20 (multiple of 4; ≥2 Decoys).

## Slice 3 — levels 46–75

| Levels | Mix | Notes |
|---|---|---|
| 46–49 | Fragile Spirit, 2 Heroes (1 Hero on **teach** 46, familiar) | Gate still allowed |
| 50–51 | Recover: 2 Heroes, no Affix or Hidden Marks | |
| 52 | Sudden Death, **1 familiar Hero**, no other Affix, moderate board | Hard **guest** |
| 53–54 | Recover | |
| 55–61 | Mix of easy toolkit; new Heroes on no-Affix rows | Include one 3-Hero no-Affix **guest** |
| 62–65 | Unknown Count, 2 **familiar** Heroes | Mid **teach**; then 1–2 more all-familiar; then one new+familiar after that |
| 66–69 | Echo Limit Easy (`+10`), 2 **familiar** on **teach** | |
| 70–75 | Race Against Time, 2 Heroes, `timer_seconds` **90–120** | **Teach** familiar; Gate +time 20. Interleave one no-timer 3-Hero |

## Slice 4 — levels 76–105

| Levels | Mix | Notes |
|---|---|---|
| 76–81 | Rollercoaster of toolkit so far | One Fragile Spirit or Sudden Death **guest**; one 3-Hero no-Affix |
| 82–90 | Partial Veil, 2–3 Heroes | Mid: **teach** 82–84 all **familiar**; ≥1 visible, ≥1 masked |
| 91–98 | Soundquake, 2 **familiar** on **teach** | `timer_seconds` **20–25**. Keep marks. Race off |
| 99–105 | The Hidden Hero, 2 **familiar** on **teach** | All `?`. No blur, no Unknown Count. Interleave one drop-to-easy level |

## Slice 5 — levels 106–120 (peak teach)

| Levels | Mix | Notes |
|---|---|---|
| 106–110 | Among Heroes, ≥2 **familiar** | `hidden_hero_id` Radiant; ≥1 Radiant portrait; no Unknown Count / Mirror Mode |
| 111–114 | Blurred Vision 2, **familiar** **teach** | Stronger blur; real art only |
| 115–117 | Soundquake Aftershock, **familiar** **teach** | Marks clear on quake |
| 118–120 | Mirror Mode, 2 **familiar** Heroes, Easy-feeling board | `max_sounds` > correct count. No Among Heroes |

One level in 106–120 may drop the new Affix and use 3 Heroes + an easy Affix (rollercoaster).

## Slice 6 — levels 121–150 (**remix**)

No new Affix *family* except finishing the hard guests: Soundquake Fracture, Cataclysm, Echo Medium, tighter Race (`60–75`), 4-Hero **guest**, two-Affix legal stacks, expert stacks.

All stacks and hard guests sit on **familiar** boards. New Heroes (if any roster left) go on no-Affix or easy rows.

Expert-only (legal, late): Mirror Mode + The Hidden Hero; Mirror Mode + Partial Veil. A handful in 135–150, **familiar** Heroes, not on a 4-Hero board.

High-volume Heroes (Invoker, Kez, Ringmaster) live here or in 106–120, not earlier.

`max_sounds` 16–24 (multiple of 4). Echo Hard (`0`) only as a rare **guest** after Medium has been seen.

## Hero variety

- Track ids used and appearance counts; fill unused roster on no-Affix / easy rows.
- Repeats are for **teach**, **familiar** mid/hard boards, and **remix**.
- Early 1-Hero boards: 2–3 spell Heroes, distinctive lines.
- Partial Veil / Among Heroes / blur membership: `CONTEXT.md`.

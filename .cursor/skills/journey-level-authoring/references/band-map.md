# Journey catalog band map (120)

Peak toolkit around level **85**. Levels **86–120** are **remix** at that peak (variety, not a third climb). `dire_heroes` stays empty. Six Issues × 20 Chapters sit *behind* first-clears; they do not pick board Heroes.

**Toolkit** = loads already taught. Most levels still mix *lower* loads. A **guest** uses a higher load once or twice, then the curve returns to mix.

## Unlock schedule

| Taught by | Load now in the toolkit |
|---|---|
| 1 | 1 Hero, decoy density (`max_sounds`) |
| 8 | 2 Heroes |
| 15 | Hidden Marks |
| 16 | 3 Heroes as **guest** (not a floor) |
| 27 | Blurred Vision |
| 31 | Fragile Spirit |
| 32 | Sudden Death as **guest** |
| 36 | Unknown Count |
| 38 | Echo Limit (first teach Easy `echo_limit_offset = 10`) |
| 40 | Race Against Time |
| 48 | Partial Veil |
| 52 | Soundquake |
| 56 | The Hidden Hero |
| 60 | Among Heroes |
| 64 | Blurred Vision 2 |
| 68 | Soundquake Aftershock |
| 72 | Mirror Mode |
| 76 | Soundquake Fracture |
| 85 | 4 Heroes as **guest**, Soundquake Cataclysm as **guest**, two-Affix legal stacks |
| 86–120 | **Remix** full toolkit; expert stacks (Mirror Mode + The Hidden Hero / Partial Veil) only here |

## Slice 1 — levels 1–20 (Issue-sized prefix)

| Level | Radiant count | Affixes | `max_sounds` | Role |
|---|---|---|---|---|
| 1 | 1 (prefer 2-spell Hero) | none | 8 | Primer |
| 2 | 1 | none | 8 | Primer |
| 3 | 1 | none | 9 | Primer |
| 4 | 1 | none | 9 | Primer |
| 5 | 1 | none | 10 | Primer |
| 6 | 1 | none | 10 | Primer |
| 7 | 1 | none | 12 | **Guest** decoys |
| 8 | 1 | none | 12 | **Guest** decoys; 2 Heroes unlock after this |
| 9 | 2 | none | 12 | Teach 2 Heroes |
| 10 | 2 | none | 12 | Plateau |
| 11 | 2 | none | 14 | Plateau, more decoys |
| 12 | 2 | none | 14 | Plateau |
| 13 | 2 (one **teach** repeat) | Hidden Marks | 14 | Teach Affix on a known Hero |
| 14 | 2 new | Hidden Marks | 14 | Mini-set |
| 15 | 2 new | Hidden Marks | 16 | Mini-set end |
| 16 | 3 (one repeat) | none | 16 | **Guest**: drop Affix, raise Heroes |
| 17 | 2 | Hidden Marks | 16 | Affix returns; 3 is not the floor |
| 18 | 2 | none | 16 | Rollercoaster: decoys, no Affix |
| 19 | 3 | none | 18 | 3-Hero mix (still a guest density) |
| 20 | 2 | none | 18 | Fat decoys, 2 Heroes |

Timers / echo / veil membership: unset. `echo_limit_offset` may stay default 5 (unused without Echo Limit).

## Slice 2 — levels 21–40

| Levels | Mix | Notes |
|---|---|---|
| 21–24 | 2 Heroes, no Affix or Hidden Marks returning | Keep 3-Hero as occasional **guest** only (one of these four at most) |
| 25–27 | 2 Heroes, Blurred Vision (`blurred_hero_ids` = some but not all; empty blur is a noop) | **Teach** on a familiar Hero at 25 |
| 28 | 2–3 Heroes, no Affix, fatter `max_sounds` | Drop blur, other axis |
| 29–31 | Fragile Spirit, 2 Heroes (1 Hero on **teach** 29) | Gate still allowed |
| 32 | Sudden Death, **1 familiar Hero**, no other Affix, moderate board | **Guest**; first fail ends the round |
| 33 | 2 Heroes, Hidden Marks or none | Recover after Sudden Death |
| 34–36 | Unknown Count, 2 Heroes | **Teach** 34 with a repeat Hero |
| 37–38 | Echo Limit Easy (`echo_limit_offset = 10`), 2 Heroes | Confirm with owner only if you leave Easy |
| 39–40 | Race Against Time, 2 Heroes, `timer_seconds` **90–120** | Generous; Gate +time 20 |

`max_sounds` for this slice: 14–18 typical; 20 only as a decoy **guest**.

## Slice 3 — levels 41–60

| Levels | Mix | Notes |
|---|---|---|
| 41–44 | Rollercoaster of toolkit so far | Include one 3-Hero no-Affix and one Fragile Spirit or Sudden Death **guest** (not both stacked) |
| 45–48 | Partial Veil, 2–3 Heroes | ≥1 visible, ≥1 masked; **teach** 45 with 2 Heroes, one repeat. No The Hidden Hero / Among Heroes |
| 49–52 | Soundquake, 2 Heroes | `timer_seconds` **20–25**. Keep marks. Race stays off |
| 53–56 | The Hidden Hero, 2 Heroes | All `?`. No blur, no Unknown Count |
| 57–60 | Among Heroes, ≥2 Heroes | `hidden_hero_id` Radiant; ≥1 Radiant portrait; no Unknown Count / Mirror Mode |

One level in 49–60 may drop the new Affix and use 3 Heroes + Hidden Marks instead (rollercoaster).

## Slice 4 — levels 61–85 (peak)

| Levels | Mix | Notes |
|---|---|---|
| 61–64 | Blurred Vision 2 | Stronger blur; real art only. Compatible with Partial Veil / Among Heroes later in **remix** |
| 65–68 | Soundquake Aftershock | Marks clear on quake. Interval 20–25 |
| 69–72 | Mirror Mode | `max_sounds` > correct count. No Among Heroes. **Teach** 69 with 2 familiar Heroes, Easy-feeling board |
| 73–76 | Soundquake Fracture | Heart on quake; Gate still allowed. Not Sudden Death |
| 77–80 | Mix: tighter Race (`timer_seconds` 60–75), Echo Limit Medium (+5), 3-Hero no-Affix | One load at a time per level |
| 81–85 | Peak **guests**: one 4-Hero no-Affix; one Cataclysm; one legal two-Affix stack (e.g. Hidden Marks + Echo Limit, Race + Blurred Vision) | Then remix starts |

## Slice 5–6 — levels 86–120 (**remix**)

No new Affix *family*. Variety is the job.

Rotate: 2 / 3 / 4 Heroes; 0–2 Affixes; decoy swings; Race vs Soundquake family (one timer Affix); heart guests (Fragile Spirit, Sudden Death, Fracture, Cataclysm) spaced — not clustered.

Expert-only (legal, late): Mirror Mode + The Hidden Hero; Mirror Mode + Partial Veil. A handful in 100–120, familiar Heroes, not on a 4-Hero board.

High-volume Heroes (Invoker, Kez, Ringmaster) live here or in 77–85, not earlier.

`max_sounds` 16–24. Echo Hard (`0`) only as a rare **guest** after Medium has been seen.

## Hero variety

- Track ids used in already-authored levels; fill unused roster before heavy repeats.
- Repeats are for **teach** (new Affix) and **remix** spice.
- Early 1-Hero boards: 2–3 spell Heroes, distinctive lines.
- Partial Veil / Among Heroes / blur membership: `CONTEXT.md`.

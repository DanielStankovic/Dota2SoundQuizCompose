# Journey catalog band map (150)

Peak toolkit around level **120**. Levels **121–150** are **remix** at that peak. `dire_heroes` stays empty. Active Issue Chapter sum is **150**; board Heroes do not pick the Issue.

**Assist-retuned (playtest 2026-10-10):** Journey Assists justify earlier Affix teaches and legal stacks. **Mirror Mode** teaches ~**40**. **Among Heroes** is sparse (teach + mini-set, then ~2 spaced returns — blocks Assists). **Aftershock / Cataclysm** (wipe marks) use long intervals (~60–75s) so the board can be sampled before reshuffle; prefer keep-marks Soundquake/Fracture for later stacks. High-volume Heroes (Invoker / Kez / Ringmaster) pair with **one** partner only. Unordered Hero **pair** / exact **roster** repeats capped at **3**. Never debut a Hero behind **The Hidden Hero** / **Among Heroes**.

**Toolkit** = loads already taught. A **guest** uses a higher load once or twice, then the curve returns to mix.

A **familiar** Hero has already appeared on an earlier Journey level (catalog sounds, not the Dota kit).

**Decoy density** is its own axis (skill `journey-level-authoring`): extra cards = `max_sounds` − roster spell count. Light 2–6, medium 7–10, heavy 11+. The seed does not list Decoy spell ids.

Source of truth: `scripts/journey-catalog/catalog.py` (regenerate `apply.sql` + `CATALOG.md`).

## Affix tiers

| Tier | Affixes | **Familiar** bar |
|---|---|---|
| Easy | Hidden Marks, Blurred Vision, Fragile Spirit | **Teach**: ≥1 familiar Hero on the board |
| Mid | Unknown Count, Echo Limit (Easy), Race Against Time (generous), Partial Veil, Soundquake, The Hidden Hero, Blurred Vision 2, Mirror Mode (on 1–2 Hero boards) | **Teach** + next 1–2 levels: **every** Hero familiar |
| Hard | Sudden Death, Echo Limit Medium/Hard, Race (tight), Among Heroes, Soundquake Aftershock, Soundquake Fracture, Soundquake Cataclysm, expert stacks, dense stacks | Same as mid; Sudden Death **teach** is 1 familiar Hero and no other Affix |

New Heroes belong on **no-Affix** or easy / visible-portrait boards. Never stack until each Affix in the stack was taught solo.

## Unlock schedule

| Taught by | Load now in the toolkit |
|---|---|
| 1 | 1 Hero, decoy density |
| 11 | 2 Heroes |
| 21 | Hidden Marks |
| 24 | 3 Heroes as **guest** |
| 29 | Blurred Vision |
| 32 | First **2**-stack (easy+easy) |
| 36 | Fragile Spirit |
| 40 | **Mirror Mode** (easy/mid; 1–2 Heroes, fat Decoys OK) |
| 46 | Sudden Death **guest** |
| 50 | Unknown Count |
| 56 | Echo Limit Easy (`+10`) |
| 62 | Race Against Time (generous teach) |
| 70 | Partial Veil |
| 78 | Soundquake (keep-marks; 25→20) |
| 85 | Soundquake Aftershock (**clear marks** → timer **~65–70s** on 2 Heroes) |
| 92 | The Hidden Hero |
| 98 | Among Heroes (teach + mini-set; returns ~109 / ~135) |
| 104 | Blurred Vision 2 |
| 121–150 | **Remix**: Fracture (keep-marks), Cataclysm (**clear marks**, **~70–75s**; few boards), high-volume + **1** partner, expert Mirror stacks, 3–4 stacks preferring keep-marks quakes |

## Param ramps

| Affix field | Teach | Later pressure |
|---|---|---|
| Echo `echo_limit_offset` | **+10** | **+5** Medium, rare **0** Hard |
| Race `timer_seconds` | **120 → 100** | **95 / 80 / 75 / 70** |
| Soundquake (keep marks) | **25 → 20** | **20–22** |
| Aftershock / Cataclysm (wipe marks) | **70 → 65** / **75 → 70** | stacks **≥60**; prefer fewer wipe boards than keep-marks |
| Fracture (keep marks) | **~28** | **~26** |
| Race Gate buyback | `timer_extension_seconds` **20** | unchanged |

## Stack variety

After a solo teach mini-set, the next stack should **rotate** partners: Blur, Echo, Mirror, Fragile, Race, Unknown Count, etc. Hidden Marks is fine sometimes — not the default every time.

## Hero variety

- Spread mid/hard teach boards across many familiar pairs (≤3 repeats per unordered pair / exact roster). Prefer fresh partners after a teach window.
- Catalog validator flags any Hero with **>16** appearances.
- High-volume (Invoker, Kez, Ringmaster) ≥106 / remix.
- Partial Veil / Among / blur membership: `CONTEXT.md`.

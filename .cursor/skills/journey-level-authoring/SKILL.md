---
name: journey-level-authoring
description: >
  Author Journey levels — create, extend, or retune the Journey catalog
  (Hero count, Affix mix, decoy density, timers). Use when writing staging
  `journey` rows, replacing placeholder levels, or pacing a band.
---

# Journey level authoring

Write staging `journey` rows as a **toolkit** **rollercoaster**: new loads unlock slowly; inside the current toolkit, mix axes so a **guest** spike is not a new floor.

Launch catalog: **120** levels, peak around **85**, remix **86–120**. Six Issues × 20 Chapters. [ADR-0020](../../../docs/adr/0020-journey-level-catalog.md). Combo rules, Echo Limit confirm, portrait guardrails: `CONTEXT.md` (single source of truth).

## Leading words

**toolkit**, **rollercoaster**, **band**, **guest**, **teach**, **remix**.

## Steps

### 1. Load domain + band map

Read `CONTEXT.md` (Journey progression, hearts, portraits, Affix incompatibilities, Echo Limit, Mirror Mode, Soundquake). Read [`references/band-map.md`](references/band-map.md) for the slice you are writing.

**Done when:** you can name the slice’s toolkit (Hero counts and Affix families already unlocked) and which load this slice **teaches** vs only **remixes**.

### 2. Pull staging facts

Query staging (not prod): Affix rows (`id`, `affix` name); Hero sound counts (`casters` + `sounds`); existing `journey` rows in and before the slice (Hero ids already used).

**Done when:** every Affix you will attach is resolved **by name → staging id**, and you have a used-Hero set for variety.

### 3. Draft every level in the slice

For each `level` in the slice, fill the band-map row:

| Field | How to pick |
|---|---|
| `radiant_heroes` | Count from the band map. Prefer unused Heroes. **Teach** a new Affix with one familiar Hero from earlier levels. High-volume (Invoker, Kez, Ringmaster) only late act 2 / **remix**. |
| `dire_heroes` | Always `{}` until Dire portraits ship. |
| `affixes` | Names from the band map → staging ids. Empty when the map says none. Same Affix on 2–3 consecutive levels is a mini-set; then switch. |
| `max_sounds` | Decoy density from the map. Must be **≥** that roster’s correct-sound count. Mirror Mode: **>** correct count (room for decoys). |
| `masked_hero_ids` / `blurred_hero_ids` / `hidden_hero_id` | Only when that Affix is on; membership rules in `CONTEXT.md`. Among Heroes: hide a **Radiant** id; leave ≥1 Radiant portrait. |
| `timer_seconds` | Race / Soundquake only; interval from the map. |
| `timer_extension_seconds` | Race Gate buyback; default 20. |
| `echo_limit_offset` | Echo Limit only. Map gives Easy +10 / Medium +5 / Hard 0. If you want any other offset, **ask the owner before writing** (`CONTEXT.md`). |

**Done when:** every level in the slice has Heroes, Affixes, `max_sounds`, and membership/timer/echo fields filled, and you can point at the band-map row it implements.

### 4. Validate the slice

For every drafted level, apply `CONTEXT.md` incompatibilities and guardrails (hearts, identity, blur, timers, Soundquake variants, Among Heroes, Partial Veil, Mirror Mode, Echo Limit floor). Check the slice itself:

- One new cognitive load per **teach** level (not a new Affix *and* a Hero-count jump *and* a timer squeeze).
- **Guest** spikes revert: the next few levels mix a *different* axis (drop Affix and raise Heroes, or keep Affix and drop Heroes, or change decoys).
- Hero repeats are **teach** or late **remix**, not the same pair three times in a row.
- No leftover placeholder rows with `level` past the authored prefix (delete them).

**Done when:** every level in the slice is combo-legal and the guest/teach/variety checks above pass.

### 5. Write staging + sync note

`INSERT`/`UPDATE` staging `journey` by `level`. Remap nothing onto prod. Update the living staging→prod ticket (`docs/agents/staging-prod-sync.md`). When the playable level set changes for testers, reset staging `game_data.journey_level` to **0** and wipe `level_clear_time` (and Issue Progress when that table exists), unless the owner says skip.

**Done when:** staging rows match the draft, placeholders beyond the prefix are gone, and the sync ticket mentions this seed.

## Out of scope

HUD layout (`affix-gameplay-hud`). Issue prose. Copying staging numeric ids into prod.

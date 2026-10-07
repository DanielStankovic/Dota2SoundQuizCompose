---
name: journey-level-authoring
description: >
  Author Journey levels — create, extend, or retune the Journey catalog
  (Hero count, Affix mix, decoy density, timers). Use when writing staging
  `journey` rows, replacing placeholder levels, or pacing a band.
---

# Journey level authoring

Write staging `journey` rows as a **toolkit** **rollercoaster**: new loads unlock slowly; inside the current toolkit, mix axes so a **guest** spike is not a new floor.

Launch catalog: **150** levels, peak around **120**, remix **121–150**. [ADR-0020](../../../docs/adr/0020-journey-level-catalog.md). Combo rules, Echo Limit confirm, portrait guardrails: `CONTEXT.md`.

## Leading words

**toolkit**, **rollercoaster**, **band**, **guest**, **teach**, **familiar**, **remix**, **density**.

## Steps

### 1. Load domain + band map

Read `CONTEXT.md` (Journey progression, board composition / Decoys, hearts, portraits, Affix incompatibilities, Echo Limit, Mirror Mode, Soundquake). Read [`references/band-map.md`](references/band-map.md) for the slice you are writing.

**Done when:** you can name the slice’s toolkit (Hero counts and Affix families already unlocked) and which load this slice **teaches** vs only **remixes**.

### 2. Pull staging facts

Query staging (not prod): Affix rows (`id`, `affix` name); Hero sound counts (`casters` + `sounds`); existing `journey` rows in and before the slice (Hero ids already used, and how many times).

**Done when:** every Affix you will attach is resolved **by name → staging id**, and you have a used-Hero set with appearance counts.

### 3. Draft every level in the slice

For each `level` in the slice, fill the band-map row:

| Field | How to pick |
|---|---|
| `radiant_heroes` | Count from the band map. New Heroes belong on no-Affix or **easy** boards. **Familiar** = already appeared on an earlier Journey level (the catalog spell set). Easy Affix **teach**: at least one familiar Hero. Mid/hard Affix **teach** and the next 1–2 levels of that family: **every** Hero on the board is familiar (prefer two prior appearances). High-volume (Invoker, Kez, Ringmaster) only late act 2 / **remix**. |
| `dire_heroes` | Always `{}` until Dire portraits ship. |
| `affixes` | Names from the band map → staging ids. Empty when the map says none. Same Affix on 2–3 consecutive levels is a mini-set; then switch. **Teach solo** before any stack. |
| `max_sounds` | See **Decoy density** below. |
| `masked_hero_ids` / `blurred_hero_ids` / `hidden_hero_id` | Only when that Affix is on; membership rules in `CONTEXT.md`. Among Heroes: hide a **Radiant** id; leave ≥1 Radiant portrait. |
| `timer_seconds` | Race / Soundquake only; interval from the map. |
| `timer_extension_seconds` | Race Gate buyback; default 20. |
| `echo_limit_offset` | Echo Limit only. Map gives Easy +10 / Medium +5 / Hard 0. If you want any other offset, **ask the owner before writing** (`CONTEXT.md`). |

The seed does **not** pick which Decoy spells appear. Journey Round samples unique Sounds from Heroes **not** on the roster. Author only the **count** via `max_sounds`.

1. Sum catalog Sounds for that level’s Heroes (`correctCount`).
2. Pick a **density** from the band map (light / medium / heavy), not a raw card total.
3. `max_sounds = correctCount + decoys`, then raise to a multiple of 4 and to at least `correctCount + 2`.
4. Check the result is 8, 12, 16, 20, or 24 (or 28 only as a late **guest**).

| Density | Extra Decoys (after `correctCount`) | Use |
|---|---|---|
| Light | 2–6 | **Teach**, recover, first levels of a Hero count |
| Medium | 7–10 | Plateau |
| Heavy | 11+ | **Guest** pressure on the same Heroes / Affix |

Raising `max_sounds` with the roster held still is a valid **guest**. Dropping Decoys while adding an Affix is a valid recover. Pad in Journey Round is a safety net if the seed is short — still write the canonical `max_sounds` so pad stays unused.

**Done when:** every level in the slice has Heroes, Affixes, `max_sounds` (a density in that table, ≥2 Decoys after queried spell counts), and membership/timer/echo fields filled, and you can point at the band-map row it implements.

### 4. Validate the slice

For every drafted level, apply `CONTEXT.md` incompatibilities and guardrails (hearts, identity, blur, timers, Soundquake variants, Among Heroes, Partial Veil, Mirror Mode, Echo Limit floor). Check the slice itself:

- Board: density from the table; ≥2 **Decoys**; `max_sounds` multiple of 4; Decoys are non-roster Sounds only (engine rule, not a seed id list).
- One new cognitive load per **teach** level.
- **Guest** spikes revert: the next few levels mix a *different* axis.
- **Familiar** check: mid/hard boards in this slice have zero brand-new Heroes on **teach** + the next 1–2 of that family.
- Stacks only after each Affix in the stack was taught solo.
- Hero repeats are **teach**, **familiar** mid/hard boards, or **remix** — not the same pair three times in a row on easy levels.
- No leftover placeholder rows with `level` past the authored prefix (delete them).

**Done when:** every level in the slice is combo-legal and the guest/teach/familiar/variety checks above pass.

### 5. Write staging + sync note

`INSERT`/`UPDATE` staging `journey` by `level`. Remap nothing onto prod. Seed source of truth: [`scripts/journey-catalog/`](../../../scripts/journey-catalog/) (`catalog.py` → `apply.sql`; Affix/Hero **names**, not numeric ids). Same `apply.sql` is the prod copy. Staging tester reset is `reset-staging-testers.sql` (never on prod). Update the living staging→prod ticket (`docs/agents/staging-prod-sync.md`). When the playable level set changes for testers, reset staging `game_data.journey_level` to **0** and wipe `level_clear_time` (and Issue Progress when that table exists), unless the owner says skip. If you retune levels, edit `catalog.py`, regenerate, re-apply, and update that ticket.

**Done when:** staging rows match the draft, placeholders beyond the prefix are gone, and the sync ticket mentions this seed.

## Out of scope

HUD layout (`affix-gameplay-hud`). Issue prose. Copying staging numeric ids into prod.

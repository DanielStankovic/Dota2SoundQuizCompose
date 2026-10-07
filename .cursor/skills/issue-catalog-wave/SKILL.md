---
name: issue-catalog-wave
description: >
  Dispatch Journey Issue translation waves via subagents (all Issues × Reading
  Locales, resume from --check). Use when running the full i18n matrix, an AFK
  locale wave, or “translate every Issue into es/all locales” — not when retelling
  a single unit yourself.
---

# Issue catalog wave

You are the **dispatcher**. Workers **retell** and **edit**. This process never writes `docs/story/i18n` itself and never translates a Chapter.

**Matrix** = every slug in `scripts/story-catalog/seed.py` `ISSUES` × every key in `scripts/story-catalog/locales.py` (242 units). Worker skill: `issue-catalog-translation`. Check after every write is a second subagent on a cold context, not the translator grading itself.

## Leading words

**dispatch**, **wave**, **matrix**, **FAIL**.

## Steps

### 1. Load the matrix

Run `python3 scripts/story-catalog/seed_i18n.py --check` from the repo root. Read `ISSUES` and `LOCALES` from those two files. Do not invent slugs or locales.

**Done when:** you have the missing-unit list (locale/slug). If it is empty and `--check` is clean, stop and say the matrix is complete.

### 2. Dispatch one translate wave

Take the next locale that still has missing slugs. **Dispatch** one `generalPurpose` subagent per missing slug, all in **one** message, at most 11. `run_in_background: true`. Each prompt is the **retell** template in [`references/worker-prompts.md`](references/worker-prompts.md), with slug, locale, locale note, source path, and output path filled in.

Then stop this turn. Do not poll. Do not start another locale. Do not retell in this process.

**Done when:** those Task calls are sent. Missing files for that locale wait on the next turn.

### 3. Dispatch one edit wave

When the translate wave returns: confirm each output path exists. Missing file = **FAIL**. **Dispatch** one `generalPurpose` subagent per new file, same one-message / background / then-stop rule. Each prompt is the **edit** template in `references/worker-prompts.md`.

**Done when:** those Task calls are sent.

### 4. Retry FAIL once

When the edit wave returns: any `FAIL`, missing file, or wrong Chapter count gets **one** retell dispatch and then **one** edit dispatch (two turns, same stop-between-waves rule). Still FAIL after that: keep the file, record the path, continue the **matrix**.

**Done when:** every unit in this locale is present, or recorded as leftover FAIL.

### 5. Next locale, then close

Repeat from step 1 for the next locale with missing slugs. After the last locale, run `--check` again. Reply with: present / missing / invalid counts, leftover FAIL paths, and do not apply SQL.

**Done when:** `--check` has been run after the last wave and the leftover FAIL list is in the reply.

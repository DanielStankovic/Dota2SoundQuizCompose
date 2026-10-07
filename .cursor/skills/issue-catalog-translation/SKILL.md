---
name: issue-catalog-translation
description: >
  Retell a Journey Issue into one Reading Locale (title, synopsis, Chapter
  bodies). Use when translating docs/story Issues, writing or editing
  docs/story/i18n files, or seeding story_*_i18n SQL.
---

# Issue catalog translation

**Retell** one Issue into one Reading Locale. The **unit** is one slug × one locale. Source of the **notes** is that Issue’s `## Localisation notes` section. Output is a markdown file, not chat and not SQL.

English stays in `docs/story/{file}.md` and `seed.sql`. This skill never writes `en`.

## Leading words

**unit**, **retell**, **notes**, **edit**.

## Steps

### 1. Bind the unit

Read [`scripts/story-catalog/locales.py`](../../../scripts/story-catalog/locales.py) and the `ISSUES` list in `scripts/story-catalog/seed.py`. Take **one** slug and **one** locale from the user.

Stop and ask if either is missing. Stop if the locale is `en`, unknown, or `archive`. Stop if the user named a second Issue or a second locale.

Narrow exceptions, only when the user listed them: `zh` and `zh-Hant` together for the same Issue; Marci or Storm Spirit with at most three locales (those two Issues are short). Write one file per locale. Do not volunteer extra locales.

**Done when:** slug, locale, source path `docs/story/{file}.md`, and output path `docs/story/i18n/{locale}/{slug}.md` are named.

### 2. Load the English Issue and its notes

Read the source markdown. Copy the **notes** into a working checklist (every bullet, every named line, every “keep in English” fragment). Read the English title and synopsis from `ISSUES` in `seed.py`. Count `## Part N` bodies; that count is the Chapter count.

When a notes line says to use an official in-game string, look that string up for this locale and use that exact wording. When it does not, keep the English proper name from the source.

**Done when:** the checklist exists, title/synopsis are known, and Chapter count is known.

### 3. Draft or edit (one pass only)

Pick **retell** if the output file is missing or the user asked for a new translation. Pick **edit** if the file exists and the user asked to review or fix it. Never both in one run.

**Retell:** write the whole Issue in that language — title, synopsis, every Chapter body — following [`references/file-format.md`](references/file-format.md). Honour every notes item. Write as a children’s book in that language: short sentences, everyday words, the same jokes. Keep the source’s paragraph breaks. Keep `**bold**` and `*italic*` where the English has them. Headings stay `## Part {n}` so the seeder can parse them; text after the em dash may be a translated part title (catalog ignores it).

**Edit:** read the existing file against the English and the notes. Fix the file in place. Prefer a sentence that a child would say in that language over a sentence that follows English word order.

**Done when:** `docs/story/i18n/{locale}/{slug}.md` exists, Chapter count matches English, and this run did only one of retell or edit.

### 4. Check the notes

Walk the checklist from step 2 against the file. Every item lands. Hero names and “keep in English” fragments match the notes. Shop signs, shouts, and repeated lines stay identical across the Chapters that share them. Synopsis is one picker blurb, not a plot summary of later Chapters.

**Done when:** every notes item has been checked in the file, not from memory.

### 5. Stop

Reply with the output path and the Chapter count. Do not run `seed_i18n.py`, do not apply SQL, do not start another unit.

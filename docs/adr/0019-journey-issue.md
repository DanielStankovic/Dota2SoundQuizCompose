# Journey Issues are a remote catalog; Issue Progress is its own module

First-clear of a Journey level grants the next **Chapter** of the **Active Issue**, or opens the **Issue Picker** when there is none. Chapter count is per Issue. The gift is **not** authored on the Journey level row and is **not** XP. Catalog (Issues, Chapters, translations, art) lives on Supabase. English catalog plus Issue cover and Issue backdrop sync at boot like Affixes/sounds (`modified_at` incremental). The player's **Reading Locale** is chosen on first entry to Hero's Journey (mandatory gate; English default) and changeable in Options; confirming a locale syncs that locale's full i18n when not already complete locally. Curated locales ship pre-translated; on-demand Edge Function translation is deferred. Player **Issue Progress** lives in `JourneyIssue`, outside Player Progress (same seam as Level Clear Time). Mode Result calls it only on a Journey **first-clear**.

**Why remote catalog, not APK / `strings.xml`:** new Issues must ship without an app update; bodies need English plus other Reading Locales; art is remote files, not drawables. UI chrome (Continue, picker titles) may still use Android `values-*`.

**Why not `journey_level` as the story index:** the player **picks** the Active Issue, Chapter counts vary, and first-clears with no Active Issue accrue **owed grants** instead of mapping “level N → Chapter N.” `journey_level` is the playable-level watermark only. Hero’s Journey is unpublished at this feature’s launch, so Issue Progress starts empty for everyone — do **not** fill Issues from an existing `journey_level`.

**Why not auto-assign Issue 1:** the player must choose (cover, synopsis, Chapter count). Completed Issues stay out of the Picker. One Active Issue at a time. Completing an Issue with **owed grants still remaining** re-opens the Picker in the same session; if owed is already 0, the next Picker waits for the **next** first-clear.

**Catalog invariant:** count Chapter capacity from **active** Issues only. Do not author more Journey levels than that sum; author toward equal counts. Do not unpublish an Issue if that would make active Chapter capacity smaller than the Journey level count.

**Art:** two remote files per Issue — **Issue cover** (Picker / Library PNG) and **Issue backdrop** (Chapter-reader JPG). Same Storage prefix; not APK drawables. No per-Chapter images. Each Chapter has a short title in `story_chapter_i18n` (from the source `## Part N — Title` headings). Replacing a file bumps that Issue’s `modified_at` so boot re-downloads it.

**Reading Locale:** `story_progress.reading_locale` is null until the player confirms a curated code (mandatory gate on first Hero's Journey entry; same picker in Options). Device language does not select it. Boot syncs English always, plus existing rows for the preferred locale when set. Confirming a locale downloads that locale's full issue + chapter i18n when the local catalog is incomplete; skip the download when already complete. Boot continues `modified_at` incremental updates. Missing copy falls back to English. Launch corpus is pre-translated for all curated codes. An on-demand Edge Function translator (and reader “checking translation” UI) is deferred — not required while the matrix is complete.

Curated codes (one body per row; dialects not listed): `en`, `zh`, `zh-Hant`, `es`, `pt` (Brazilian text), `hi`, `ar`, `bn`, `ru`, `ja`, `pa`, `id` (Malay folded), `ur`, `de`, `fr`, `ko`, `tr`, `vi`, `sr-Latn` (also Bosnian, Montenegrin, Croatian; Latin only), `fil`, `uk`, `th`, `ro`. Source of truth: `scripts/story-catalog/locales.py`.

**Supabase (staging names):** `story_issue` (natural key `slug`; `protagonist_caster_id`; `sort`; `cover_path`; `backdrop_path`; `active`; `modified_at`; no starter flag). `story_chapter` (`issue_id`, `chapter_index` 1–N; `modified_at`; no image column). `story_issue_i18n` (`locale`, `title`, `synopsis`, `modified_at`). `story_chapter_i18n` (`locale`, `title`, `body`, `modified_at`). Storage bucket `story_art`. Player row `story_progress` PK `user_id`: nullable `active_issue_id`, `active_unlocked`, `owed_grants`, `completed_issue_ids`, nullable `reading_locale` (null = unset), `modified_at`. Catalog SELECT is public (Sync Session runs before Auth, same as Affixes). Authenticated read/write own `story_progress` row.

**Client:** `JourneyIssue` owns Room catalog + local art files + Issue Progress + grant/pick/owed-unlock + `setReadingLocale`. Sync Session downloads the English catalog + cover/backdrop files (and existing preferred-locale i18n) and hydrates Issue Progress (including Reading Locale). First Hero's Journey entry opens the Reading Locale gate when unset; Options reuses the same picker. First-clear path: drain owed with Picker(s) first (no reader between completing picks while owed remains) → **one** Chapter reader on the **newest** Chapter just granted (last Chapter of a just-completed Issue still shows this once) → **Continue** (always enabled) → existing Mode Result. Library uses close/back only and also lists locked unstarted Issues as teasers. Reader: one bundled music bed (local mute preference), repeating zoom, line-by-line reveal (tap = full text), scroll, Chapter title + `n / total`, previous/next within unlocked Chapters of the current reader Issue only. No Reading Locale picker on the Chapter reader, Issue Picker, or Issue Library.

**Rejected:** unique comic art or AI fumetti; per-Chapter landscapes; narrator VO; tying Chapter N to Journey level N; stuffing bodies in `strings.xml`; putting Issue Progress JSON on `game_data`; a second currency; missable Chapters on fail; auto-assigned first Issue; using `journey_level` as catch-up credit to fill Issues for veterans; calling the translation model from the Android client; free-text / dialect locales (`en-US`, `pt-PT`, `bs`, `hr`, …).

## Revert

1. Drop staging `story_*` tables, Storage `story_art`, and `story_progress`. Drop the translation Edge Function and its secrets.
2. Remove `JourneyIssue`, Sync Session story steps, Mode Result Chapter grant, Chapter/Picker/Library destinations.
3. Revert glossary Issue terms in `CONTEXT.md`. Supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Issue**, **Issue cover**, **Issue backdrop**, **Chapter**, **Protagonist**, **Active Issue**, **Issue Picker**, **Issue Library**, **Reading Locale**, **Issue Progress**, **Mode Result**)
- Spine: ADR-0015 (no XP / second currency; first-clear still +20 coins)
- Player Progress stays a thin row: ADR-0002
- Mode Result pairing: ADR-0010
- Catalog boot sync: ADR-0005
- Staging → prod remap: `docs/agents/staging-prod-sync.md` (use `slug` + `chapter_index` + `locale`, remap `protagonist_caster_id`)

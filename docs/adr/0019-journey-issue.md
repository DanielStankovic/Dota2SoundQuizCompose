# Journey Issues are a remote catalog; Issue Progress is its own module

First-clear of a Journey level grants the next **Chapter** of the **Active Issue**, or opens the **Issue Picker** when there is none. Chapter count is per Issue. The gift is **not** authored on the Journey level row and is **not** XP. Catalog (Issues, Chapters, translations, art) lives on Supabase. English bodies and the Issue illustration sync at boot like Affixes/sounds; other locales load on demand (local Room, then server, whole Issue; generate via Edge Function if still missing). Player **Issue Progress** lives in `JourneyIssue`, outside Player Progress (same seam as Level Clear Time). Mode Result calls it only on a Journey **first-clear**.

**Why remote catalog, not APK / `strings.xml`:** new Issues must ship without an app update; bodies need English plus other Reading Locales; art is remote files, not drawables. UI chrome (Continue, picker titles) may still use Android `values-*`.

**Why not `journey_level` as the story index:** the player **picks** the Active Issue, Chapter counts vary, and first-clears with no Active Issue accrue **owed grants** instead of mapping “level N → Chapter N.” `journey_level` is the playable-level watermark only. Hero’s Journey is unpublished at this feature’s launch, so Issue Progress starts empty for everyone — do **not** fill Issues from an existing `journey_level`.

**Why not auto-assign Issue 1:** the player must choose (cover, synopsis, Chapter count). Completed Issues stay out of the Picker. One Active Issue at a time. Completing an Issue with **owed grants still remaining** re-opens the Picker in the same session; if owed is already 0, the next Picker waits for the **next** first-clear.

**Catalog invariant:** count Chapter capacity from **active** Issues only. Do not author more Journey levels than that sum; author toward equal counts. Do not unpublish an Issue if that would make active Chapter capacity smaller than the Journey level count.

**Art:** one Protagonist illustration per Issue (picker cover and Chapter-reader backdrop). No per-Chapter images. No Chapter titles.

**Reading Locale:** default Chapter body is English; device language does not select it. Boot syncs English always, plus existing rows for the player’s Reading Locale if present. On a missing locale, a **Supabase Edge Function** (Gemini or equivalent, key on the server — never the client) translates the **whole Issue**, writes `story_*_i18n`, and returns it. Deduplicate in-flight jobs for the same Issue × locale. On publish, pre-translate the curated locales. Show a “checking translation” state in the reader; fall back to English until the rows exist.

Curated codes (one body per row; dialects not listed): `en`, `zh`, `zh-Hant`, `es`, `pt` (Brazilian text), `hi`, `ar`, `bn`, `ru`, `ja`, `pa`, `id` (Malay folded), `ur`, `de`, `fr`, `ko`, `tr`, `vi`, `th`, `fil`, `it`, `pl`, `uk`, `ro`, `nl`, `el`, `cs`, `sv`, `hu`, `sr` (also Bosnian, Montenegrin, Croatian).

**Supabase (staging names):** `story_issue` (natural key `slug`; `protagonist_caster_id`; `sort`; `cover_path`; `active`; no starter flag). `story_chapter` (`issue_id`, `chapter_index` 1–N; no image column). `story_issue_i18n` (`locale`, synopsis; no required title). `story_chapter_i18n` (`locale`, body). Storage prefix `story_art/`. Player row `story_progress` PK `user_id`: nullable `active_issue_id`, `active_unlocked`, `owed_grants`, `completed_issue_ids`, `reading_locale`, `modified_at`. Authenticated read/write own row; catalog SELECT for signed-in players (Journey is Auth-gated).

**Client:** `JourneyIssue` owns Room catalog + local art files + Issue Progress + grant/pick/owed-unlock. Sync Session downloads the English catalog + Issue illustration and hydrates Issue Progress (including Reading Locale). First-clear path: drain owed with Picker(s) first (no reader between completing picks while owed remains) → **one** Chapter reader on the **newest** Chapter just granted (last Chapter of a just-completed Issue still shows this once) → **Continue** (always enabled) → existing Mode Result. Library uses close/back only. Reader: one bundled music bed, repeating zoom, line-by-line reveal (tap = full text), scroll, `n / total`, previous/next within unlocked Chapters of the current reader Issue only.

**Rejected:** unique comic art or AI fumetti; per-Chapter landscapes; narrator VO; tying Chapter N to Journey level N; stuffing bodies in `strings.xml`; putting Issue Progress JSON on `game_data`; a second currency; missable Chapters on fail; auto-assigned first Issue; using `journey_level` as catch-up credit to fill Issues for veterans; calling the translation model from the Android client; free-text / dialect locales (`en-US`, `pt-PT`, `bs`, `hr`, …).

## Revert

1. Drop staging `story_*` tables, Storage `story_art/`, and `story_progress`. Drop the translation Edge Function and its secrets.
2. Remove `JourneyIssue`, Sync Session story steps, Mode Result Chapter grant, Chapter/Picker/Library destinations.
3. Revert glossary Issue terms in `CONTEXT.md`. Supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Issue**, **Chapter**, **Protagonist**, **Active Issue**, **Issue Picker**, **Issue Library**, **Reading Locale**, **Issue Progress**, **Mode Result**)
- Spine: ADR-0015 (no XP / second currency; first-clear still +20 coins)
- Player Progress stays a thin row: ADR-0002
- Mode Result pairing: ADR-0010
- Catalog boot sync: ADR-0005
- Staging → prod remap: `docs/agents/staging-prod-sync.md` (use `slug` + `chapter_index` + `locale`, remap `protagonist_caster_id`)

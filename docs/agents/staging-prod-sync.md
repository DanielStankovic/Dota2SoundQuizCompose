# Staging → prod sync (Journey / Affixes)

Living checklist for promoting Supabase **staging** Journey schema and seed data to **prod**. Do not run the sync until the product owner is ready.

## Canonical ticket

Asana: [Sync staging Journey schema & seed data to prod (remap IDs)](https://app.asana.com/1/1203335126561734/project/1203335128322637/task/1218151868987256)  
Task GID: `1218151868987256`

## When to update that ticket

If you change any of the following on **staging** (or in app logic that depends on them), **update the existing sync ticket** — add/adjust checklist items in the task notes or a comment. Do **not** create a second sync ticket.

- Journey / Affix / hero (caster) **schema** (columns, defaults, constraints)
- **Seed or level authoring** that prod must eventually receive
- FK arrays or id-keyed fields (`affixes`, `radiant_heroes`, `dire_heroes`, `masked_hero_ids`, `blurred_hero_ids`, `hidden_hero_id`, …)
- Scalar Journey authoring (`timer_seconds`, `timer_extension_seconds`, `echo_limit_offset`, …)
- **Issue / Chapter catalog** (`story_issue`, `story_chapter`, `story_*_i18n`, Storage `story_art/`) — remap via Issue `slug` + `chapter_index` + `locale`; remap `protagonist_caster_id` like other caster FKs. Do not copy staging numeric Issue ids into prod `story_progress.completed_issue_ids` or `active_issue_id`. Player columns to remap/copy by user: `active_unlocked`, `owed_grants`, `reading_locale`.
- Catalog rows whose **numeric ids** differ between environments

## Full transfer (when the owner is ready)

This ticket is the **one** staging → prod copy of the Journey catalog: schema, Affix catalog, rewritten Journey seed, and Issue/Chapter catalog when authored. Staging Journey levels 1–50 are placeholders — replace them from scratch before copy; do not promote the sandbox seed.

After the real catalog is on staging, reset staging testers (`game_data.journey_level` → 0, wipe `level_clear_time`, and Issue Progress when that table exists). Do not copy staging player rows as-is into prod.

## ID rule

Never copy staging numeric ids into prod FK columns. Remap via natural keys (e.g. Affix `affix` name; hero/caster identity prod already uses), then write **prod** ids into Journey rows.

Schema-only changes (new columns with safe defaults) can ship without row dumps; seed/level rows always need remap.

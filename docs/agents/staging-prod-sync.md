# Staging → prod sync (Journey / Affixes)

Living checklist for promoting Supabase **staging** Journey schema and seed data to **prod**. Do not run the sync until the product owner is ready.

## Canonical ticket

Asana: [Sync staging Journey schema & seed data to prod (remap IDs)](https://app.asana.com/1/1203335126561734/project/1203335128322637/task/1218151868987256)  
Task GID: `1218151868987256`

## When to update that ticket

If you change any of the following on **staging** (or in app logic that depends on them), **update the existing sync ticket** — add/adjust checklist items in the task notes or a comment. Do **not** create a second sync ticket.

- Journey / Affix / hero (caster) **schema** (columns, defaults, constraints)
- **Seed or level authoring** that prod must eventually receive
- FK arrays or id-keyed fields (`affixes`, `radiant_heroes`, `dire_heroes`, `masked_hero_ids`, `blurred_hero_ids`, …)
- Scalar Journey authoring (`timer_seconds`, `timer_extension_seconds`, …)
- Catalog rows whose **numeric ids** differ between environments

## ID rule

Never copy staging numeric ids into prod FK columns. Remap via natural keys (e.g. Affix `affix` name; hero/caster identity prod already uses), then write **prod** ids into Journey rows.

Schema-only changes (new columns with safe defaults) can ship without row dumps; seed/level rows always need remap.

# Journey catalog seed (150)

Source of truth: `catalog.py` (Hero names + Affix names, not numeric ids).

**Assist-retuned + playtest pass (2026-10-10):** Mirror teach ~40; Among Heroes sparse (blocks Assists); Aftershock/Cataclysm wipe timers ~60–75s; HV Heroes (Invoker/Kez/Ringmaster) + one partner; pair/roster repeats ≤3. Band map: `.cursor/skills/journey-level-authoring/references/band-map.md`. Full table: `CATALOG.md`.

```bash
python3 scripts/journey-catalog/catalog.py
```

Writes `apply.sql` and `CATALOG.md`. `apply.sql` looks up `casters.name` and `affixes.affix` at run time, so the **same file** loads staging or prod.

## Staging

1. Run `apply.sql` (replaces every `journey` row; `id = level`).
2. Run `reset-staging-testers.sql` so watermarks/times are not left on the old prefix.

## Prod

When the owner is ready, run **this same** `apply.sql` on prod (after Affix catalog + caster names exist). Do **not** copy staging `journey.id` / Affix ids / caster ids. Do **not** run the staging tester reset on prod.

If you retune levels: edit `catalog.py`, regenerate, re-apply staging, and update the living sync ticket (`docs/agents/staging-prod-sync.md`, Asana GID `1218151868987256`).

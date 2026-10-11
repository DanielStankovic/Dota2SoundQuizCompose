# Hero Journey production rollout

Executable runbook for shipping `hero_journey` (app **2.3.0** / `versionCode` **17**) with minimal disruption. Companion to [`staging-prod-sync.md`](staging-prod-sync.md) and Asana task `1218151868987256`.

## Freeze (Phase 0)

| Item | Value |
|------|--------|
| Branch base | `hero_journey` |
| `versionName` | `2.3.0` ([`app/build.gradle.kts`](../../app/build.gradle.kts)) |
| `versionCode` | `17` |
| Prod config before cutover | `forced_version=16`, `delete_version=15` |
| Dry-run (2026-10-10) | All Journey hero names except **Largo** already on prod |

SQL packages: [`scripts/prod-rollout/`](../../scripts/prod-rollout/).

## Additive vs cutover

### Phase 2 — additive (safe while old clients are live / during Play review)

- Schema: `journey`, `affixes`, LCT, Assists, `story_*`, `game_data.journey_level`
- Affix catalog + full Journey `apply.sql` (name lookup)
- Issue/Chapter English + Reading Locale i18n (slug remap)
- `game_mode` row **Hero Journey** (`code=07`)
- Insert **Largo** + Largo sounds + **Shadow Hex** (`is_local`)
- Storage: `story_art/` covers + backdrops (copy by path; bump `modified_at` if replacing)

### Phase 3 — cutover (only after new APK is installable on Play)

- Beastmaster renames: Call of the Wild Boar/Hawk → Summon Razorback/Raptors
- Deactivate Bad Juju, Recall, Sidekick
- Insert changelog `2.3.0`
- Set `config.data.forced_version` → **17**

Never: staged rollout % + force-update; force-update before Play publish; changelog before publish; `reset-staging-testers.sql` on prod; staging numeric FKs.

## Owner Play Console (Phase 1)

1. Enable **Managed publishing** (approval ≠ auto go-live).
2. Upload release AAB (`versionCode` 17) to Production.
3. Submit for review. Do **not** raise `forced_version` or insert changelog.
4. Wait until status is **Approved, ready to publish**.

## Agent / SQL (Phase 2)

```text
1. apply scripts/prod-rollout/phase2-schema.sql
2. apply scripts/prod-rollout/phase2-seed-catalog.sql
3. apply scripts/journey-catalog/apply.sql
4. apply scripts/story-catalog/seed.sql (English)
5. apply per-locale SQL from: python3 scripts/story-catalog/seed_i18n.py --locale <code>
6. Copy Storage story_art from staging → prod (same cover_path / backdrop_path)
```

## Go-live window (Phase 3)

Same sitting, ~30–60 minutes:

1. Publish Production at **100%** (no staged %).
2. Confirm Play listing installs **2.3.0**.
3. Run [`scripts/prod-rollout/phase3-cutover.sql`](../../scripts/prod-rollout/phase3-cutover.sql).
4. Smoke: update path, Force Update on old APK, Journey + Stories, Classic sounds, changelog.

## Abort

If review rejects: leave Phase 2 in place; do not run Phase 3. Classic stays on the old spell set. Fix APK, resubmit, cutover only after the next approval.

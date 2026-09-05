# Level Clear Time is its own data-layer module

Per-level best finish duration (local unsent best, upsert-if-faster RPC, cheap top-10 / own-rank reads) lives in `LevelClearTime`, outside Player Progress (ADR-0002). Mode Result calls it on a Journey win. The elapsed clock lives in Journey Round: start when the board is `Ready`, stop on `Correct`; pause whenever Journey Round Timer pauses (Extra Life Gate ad, overlay, `ON_PAUSE`). Fail / dismiss Gate does not upsert. Extra Life Gate use does not flag the row.

**Supabase (staging names):** table `level_clear_time` PK `(user_id, level)`; columns `clear_time_ms`, `achieved_at` (tie-break: faster, then earlier); index `(level, clear_time_ms ASC, achieved_at ASC)`. RPCs: `upsert_level_clear_time`, `get_top_10_level_clear_times` (`ORDER BY … LIMIT 10`), `get_user_level_clear_time` (PK + count of strictly better rows — not full-board `RANK() OVER`). Authenticated SELECT of a level’s board; writes only via the upsert RPC (`auth.uid()`). No FK to `journey.level`. Display names join `auth.users` metadata like arcade standings.

**Client submit:** one local best per level; mark unsent; RPC; flush on Sync Session / next win / Auth Session attach. Hydrate Room personal bests from the signed-in player's server rows after that flush (Auth attach and Sync Session) so the level list matches remote after reinstall. `fetchBoard` returns domain standings (wire DTOs stay inside the module) and merges the current player's row into Room. UI: dedicated Journey board screen (not arcade chrome), Mode Result this-run / new-PB / +20, level-select personal best, CoinInfoDialog first-clear line.

## Revert

1. Drop staging `level_clear_time` + the three RPCs; revert `game_data.journey_level` default if it must match the old `'1'`.
2. Remove `LevelClearTime`, Room `LevelClearTime` entity, Journey elapsed clock, board UI, and Mode Result LCT enqueue.
3. Supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Level Clear Time**, **Mode Result**, **Journey Round**)
- Spine: ADR-0015
- Player Progress stays outside boards: ADR-0002
- Mode Result pairing: ADR-0010
- Coin batching: ADR-0001

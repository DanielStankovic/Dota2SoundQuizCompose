# Deepen Sync Session into one boot-sync module

Boot sync (forced-update check, optional wipe, catalog syncs, leaderboard flush, Player Progress sync, sound downloads) lives in one concrete Data-layer module: `SyncSession`. Callers use `run(): Flow<SyncProgress>`. Shallow per-table `syncXxx` methods are private internals. Trivia rotation stays in the Sync screen ViewModel (presentation).

## Revert

1. Restore `SyncRepository` with public `syncXxx` / `syncSound` and re-orchestration in `SyncScreenViewModel`.
2. Restore `ProgressUpdateEvent` in the sync screen package; point the screen at it again.
3. Delete `SyncSession` / `SyncProgress`; supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Sync Session**, **Player Progress**)
- Player Progress sync intent: ADR-0002

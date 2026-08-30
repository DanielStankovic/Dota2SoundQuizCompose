# Deepen Player Progress into one data-layer repository

Player Progress (coins, mode highs/plays, journey level, cloud sync of that row) lives in one concrete Data-layer module: `PlayerProgressRepository`. Callers use intent methods and observe a non-Room `PlayerProgress` model. Shallow `ScoreRepository` is removed; scoring rules and sync math leave the ViewModels. Leaderboard updates stay outside this module. Auth keeps sign-in/out and may still expose progress for Profile; Home observes and adjusts coins via `HomeViewModel`. Domain Use Cases and a repository port are deferred until a second adapter or shared orchestration needs them (see grilling Q2C / Q15A).

## Revert

To undo this deepen without rediscovering the old shape:

1. Restore `ScoreRepository` (DAO pass-through) and `UserDataRepository` (sync + `updateCoinValue`) from git history before this change.
2. Move record-result / coin / journey mutations back into `ScoreViewModel` and `AuthViewModel.updateCoinValue`.
3. Point Home / Profile / Sync / Journey Level at those types again; delete `PlayerProgress` + `PlayerProgressRepository`.
4. Supersede this ADR and keep [ADR-0001](0001-coin-spend-sync-vs-batched-results.md) unless you also change sync-on-`adjustCoins` policy.

## Related

- Glossary: `CONTEXT.md` (**Player Progress**, **Auth**, **Invoker entry**)
- Sync policy: ADR-0001

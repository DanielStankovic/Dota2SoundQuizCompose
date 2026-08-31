# Deepen Invoker entry into one in-process module

**Invoker entry** (affordability, coin spend to start, matching rewarded-ad grant) lives in one concrete module: `InvokerEntry`. Cost and delta sign leave the UI and Auth. Spend and grant go through `PlayerProgressRepository.adjustCoins` so ADR-0001 sync-on-spend stays. Shallow `AuthViewModel.adjustCoins` for entry is removed.

## Revert

1. Restore `Constants.INVOKER_COIN_COST` and spend/grant via `AuthViewModel` / `HomeViewModel.adjustCoins` in Explanation and Home Invoker UI.
2. Delete `InvokerEntry` / `InvokerExplanationViewModel`; supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Invoker entry**, **Player Progress**)
- Sync policy: ADR-0001
- Player Progress: ADR-0002
- Invoker Round (gameplay, not entry): ADR-0008

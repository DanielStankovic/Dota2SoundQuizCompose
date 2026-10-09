# Deepen Invoker entry into one in-process module

**Invoker entry** (affordability, coin spend to start) lives in one concrete module: `InvokerEntry`. Cost leaves the UI and Auth. Spend goes through `PlayerProgressRepository.adjustCoins` so ADR-0001 sync-on-spend stays. Broke players use the shared **Coin Ad Grant** faucet (Home Invoker / Assist tray), not a matching Invoker-only ad payout.

## Revert

1. Restore a matching Invoker ad grant and prior entry cost; spend via `AuthViewModel` / `HomeViewModel.adjustCoins` in Explanation and Home Invoker UI.
2. Delete `InvokerEntry` / `InvokerExplanationViewModel`; supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Invoker entry**, **Player Progress**)
- Sync policy: ADR-0001
- Player Progress: ADR-0002
- Invoker Round (gameplay, not entry): ADR-0008

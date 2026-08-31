# Deepen Invoker Round into one data-layer module

One Invoker mode run (pool load, orb queue, spell recipes, hearts, dual timers, speed ladder, playback, connectivity) lives in one concrete Data-layer module: `InvokerRound`. Shallow `InvokerRepository` is removed. `OrbType` and round events sit with the module; the ViewModel is a thin adapter that passes `viewModelScope` and forwards UI intents. Invoker entry (coin spend) and mode-result recording stay outside this module.

## Revert

1. Restore `InvokerRepository` and move pool / orb / recipe / timer / hearts logic back into `InvokerViewModel`.
2. Restore presentation-local `OrbType` / `InvokerEventState`; delete `InvokerRound` and related types under `data/invoker/`.
3. Supersede this ADR; keep glossary **Invoker Round** only if still accurate.

## Related

- Glossary: `CONTEXT.md` (**Invoker Round**, **Sound Playback**, **Invoker entry**)
- Sound Playback: ADR-0004
- Sibling rounds: ADR-0003 (Journey), ADR-0006 (Multiple Choice)

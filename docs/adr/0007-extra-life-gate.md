# Extra Life Gate for classic Quiz (Journey keeps its own)

Classic Quiz’s one-shot rewarded continue is modeled as `ExtraLifeGate` (`canOfferContinue` / `markUsed`). Journey Round already owns a timer-coupled continue gate (ADR-0003); that path is not forced through `ExtraLifeGate` because pause/resume and sudden-death coupling belong with the round.

## Revert

1. Replace `ExtraLifeGate` usage in Quiz with a plain `additionalLifeUsed` boolean on the ViewModel.
2. Delete `ExtraLifeGate`; supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Extra Life Gate**)
- Journey continue: ADR-0003
- Multiple Choice Sound Round: ADR-0006

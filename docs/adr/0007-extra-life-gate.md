# Extra Life Gate for classic Quiz (Journey keeps its own)

Classic Quiz’s one-shot rewarded continue is modeled as `ExtraLifeGate` (`canOfferContinue` / `markUsed`). Journey Round already owns a timer-coupled continue gate (ADR-0003); that path is not forced through `ExtraLifeGate` because pause/resume and sudden-death coupling belong with the round.

Journey Screen calls `JourneyRound.continueFromExtraLifeGate` — one interface that orders dismiss → obscure → reward grant → surface clear. The ad SDK stays a presentation adapter callback.

## Revert

1. Replace `ExtraLifeGate` usage in Quiz with a plain `additionalLifeUsed` boolean on the ViewModel.
2. Delete `ExtraLifeGate`; supersede this ADR.
3. For Journey: restore Screen-owned dismiss / obscure / grant / clear sequencing; remove `continueFromExtraLifeGate`.

## Related

- Glossary: `CONTEXT.md` (**Extra Life Gate**)
- Journey continue: [ADR-0003](0003-journey-round.md)
- Multiple Choice Sound Round: [ADR-0006](0006-multiple-choice-sound-round.md)
- Journey Round Timer: [ADR-0013](0013-journey-round-timer.md)

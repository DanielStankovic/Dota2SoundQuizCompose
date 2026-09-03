# Name the Journey Round Timer inside Journey Round

**Journey Round Timer** (`JourneyRoundTimer`) is an internal deep module for Race Against Time and Soundquake intervals: tick loop, overlay/host pause, pending post–Extra Life Gate starts. Soundquake board reshuffle / heart drain stay on `JourneyRound`; the timer only signals timeout. Not a peer of Journey Round — AffixEngine remains the Affix seam ([ADR-0003](0003-journey-round.md)).

## Revert

1. Inline timer fields and coroutines back into `JourneyRound`.
2. Delete `JourneyRoundTimer.kt`; supersede this ADR; drop glossary **Journey Round Timer** if unused.

## Related

- Glossary: `CONTEXT.md` (**Journey Round Timer**, **Journey Round**, **Extra Life Gate**, **Affix**)
- Journey Round: [ADR-0003](0003-journey-round.md)
- Extra Life Gate: [ADR-0007](0007-extra-life-gate.md)

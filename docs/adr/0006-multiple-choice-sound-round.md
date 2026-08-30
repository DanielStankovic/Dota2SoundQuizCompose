# Deepen Multiple Choice Sound Round for Quiz and Fast Finger

Quiz and Fast Finger share one concrete in-process module: `MultipleChoiceSoundRound` (pool, next sound, options, play via Sound Playback, connectivity). Wrong-answer policy is `StayOnWrong` (Quiz) vs `AdvanceOnWrong` (Fast Finger). Shallow `QuizRepository` is removed. Animation triggers and score UI stay in the ViewModels/screens.

## Revert

1. Restore `QuizRepository` and duplicated pool/options/play logic in `QuizViewModel` / `FastFingerViewModel`.
2. Restore `QuizEventState` if screens need it; delete `MultipleChoiceSoundRound` / related types.
3. Supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Multiple Choice Sound Round**, **Sound Playback**)
- Extra life for Quiz: ADR-0007

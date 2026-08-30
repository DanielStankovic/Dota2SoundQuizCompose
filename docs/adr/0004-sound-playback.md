# Deepen Sound Playback behind play(SoundModel)

Playing a spell sound (local mapped raw or remote URL) lives in one concrete module: `SoundPlayback`. Callers use `play(SoundModel)`, `playRaw(resId)`, and `stop()` — they do not touch `SoundFileMapper` or build `Uri`s. `SoundPlayer` remains an internal MediaPlayer adapter injected only into `SoundPlayback`.

## Revert

1. Restore duplicated `playSoundFromSoundModel` (mapper + Uri + `SoundPlayer`) in Quiz / Invoker / FastFinger ViewModels (and JourneyRound / Home as before this ADR).
2. Point those call sites at `SoundPlayer` again; delete or gut `SoundPlayback` if unused.
3. Supersede this ADR; keep glossary **Sound Playback** only if still accurate.

## Related

- Glossary: `CONTEXT.md` (**Sound Playback**)
- First call site: ADR-0003 Journey Round

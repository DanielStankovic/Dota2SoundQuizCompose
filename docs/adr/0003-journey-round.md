# Deepen Journey Round into one data-layer module

A **Journey Round** (one playable level attempt: load through submit / continue / game-over) lives in one concrete Data-layer module: `JourneyRound`. Level list loading and affix→presentation mapping share that module. `AffixEngine` + strategies stay an **internal** seam (harden hooks as affixes grow; Mirror Mode validation gets the full sound-id set). Shallow `JourneyRepository` / `JourneyLevelRepository` are removed. Playback of a `SoundModel` goes through concrete `SoundPlayback` (Journey is the first caller; other modes retarget later). Player Progress advancement on win stays outside this module. Domain Use Cases and ports are deferred.

## Revert

To undo this deepen without rediscovering the old shape:

1. Restore `JourneyRepository` and `JourneyLevelRepository` from git history before this change.
2. Move load / affix mapping / hearts / timer / submit / continue orchestration back into `JourneyGameViewModel` and `JourneyLevelViewModel`.
3. Inline `SoundFileMapper` + `SoundPlayer` usage in the Game VM again; delete `JourneyRound`, round/levels state types, and (if unused elsewhere) `SoundPlayback`.
4. Revert Mirror Mode `modifyAnswerValidation` signature/body to the pre-full-board version if desired.
5. Supersede this ADR; keep glossary **Journey Round** / **Sound Playback** only if still accurate.

## Related

- Glossary: `CONTEXT.md` (**Journey Round**, **Sound Playback**, **Player Progress**)
- AffixEngine remains internal; Soundquake stays a stub until a later change
- Among Heroes: Affix enables omit-from-portraits; mystery membership is Journey `hidden_hero_id`

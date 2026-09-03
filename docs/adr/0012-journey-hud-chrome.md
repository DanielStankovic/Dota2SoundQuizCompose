# Stop leaking AffixUIState into Journey HUD

**Journey HUD Chrome** is the Round→UI contract for the Affix status band (`showHearts`, counters, `showTimer`). `AffixUIState` stays AffixEngine-internal; Round maps it via `toHudChrome()`. Portrait blur is on `HeroPortraitSlot` ([ADR-0011](0011-hero-portrait-policy.md)), not the HUD model.

## Revert

1. Put `AffixUIState` back on `JourneyRoundState.Ready` as `affixUI`.
2. Screen imports `data.affix` again for StatusInfoRow / blur.
3. Delete `JourneyHudChrome.kt`; supersede this ADR; drop glossary **Journey HUD Chrome** if unused.

## Related

- Glossary: `CONTEXT.md` (**Journey HUD Chrome**, **Journey Round**, **Affix**)
- AffixEngine internal: [ADR-0003](0003-journey-round.md)
- Hero portraits: [ADR-0011](0011-hero-portrait-policy.md)

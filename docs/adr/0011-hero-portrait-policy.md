# Deepen Hero Portrait Policy inside Journey Round

**Hero Portrait Policy** (mask / omit / blur by hero id, drawable resolve for Radiant and Dire) lives in one deep module internal to Journey Round: `HeroPortraitPolicy`. Affix strategies still only enable portrait Affixes; level membership (`masked_hero_ids`, `blurred_hero_ids`, `hidden_hero_id`) joins there. `JourneyGameModel` exposes `List<HeroPortraitSlot>` (image + blur radius) so the Screen does not re-join Affix blur intensity.

## Revert

1. Inline mask/omit/blur/drawable logic back into `JourneyRound.startRound`.
2. Restore parallel `radiantHeroImages` / `radiantHeroBlurred` (and Dire) lists on `JourneyGameModel`.
3. Delete `HeroPortraitPolicy.kt`; Screen reads Affix blur intensity again.
4. Supersede this ADR; drop glossary **Hero Portrait Policy** if unused.

## Related

- Glossary: `CONTEXT.md` (**Hero Portrait Policy**, **Hero**, **Affix**, **Journey Round**)
- AffixEngine remains internal: [ADR-0003](0003-journey-round.md)

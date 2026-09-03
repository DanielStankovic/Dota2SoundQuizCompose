---
name: affix-gameplay-hud
description: >
  Affix Journey gameplay HUD — declutter chrome, sound-board packing, hero
  portrait strip (Radiant/Dire), timer/hearts/veil density, progressive
  disclosure, touch-first portrait layout. Use when redesigning JourneyGameScreen
  HUD, AffixUIState chrome, sound grid packing, hero-row clutter, or Affix-mode
  gameplay layout.
---

# Affix gameplay HUD

Touch-first HUD craft for Journey Round. Pair with `game-ui-ux` (architecture vocabulary), `compose-ui` / `adaptive` / `edge-to-edge` (implementation), and `create-game-assets` or Figma skills (icons/chrome art). Domain rules live in `CONTEXT.md` — this skill does not redefine Affix incompatibilities.

## Leading words

Work in these tokens: **glanceable**, **Affix chrome**, **earn**, **clutter**, **progressive disclosure**, **sound board**, **hero strip**.

## Steps

### 1. Load domain + current chrome

Read `CONTEXT.md` (Journey Affix + hero portrait sections) and `AffixUIState` in `AffixStrategy.kt`. List which Affix flags the target scenario turns on (`showTimer`, hearts, counters, veil/blur, plays left).

**Done when:** you can name every persistent HUD region for that scenario and which Affix owns it.

### 2. Rank information (earn every pixel)

Apply the hierarchy in [`references/information-hierarchy.md`](references/information-hierarchy.md). Ask of each element: does the player need this **right now** for listen → mark → submit?

**Done when:** every region is tagged glanceable, Affix-conditional, or on-demand — and on-demand items have a sheet/toggle path (e.g. `AffixInfoBottomSheet`).

### 3. Pick layout options (do not invent from scratch)

Choose **sound board** and **hero strip** patterns from [`references/layout-options.md`](references/layout-options.md). Prefer options that keep SoundCard tap targets ≥ 48.dp and free vertical space for Affix chrome. Document the pick + why rejected alternatives fail this round.

**Done when:** one sound-board option and one hero-strip option are chosen with a one-line rationale each.

### 4. Implement in Compose

Change `JourneyGameScreen` / related composables. Drive visibility from `AffixUIState` (and future hero-id-keyed veil/blur). Keep WindowInsets / edge-to-edge. Follow `compose-ui` state-hoisting. Touch rules: [`references/compose-touch-insets.md`](references/compose-touch-insets.md).

**Done when:** layout responds to Affix flags without hardcoding a single Affix combo, and Radiant+Dire hero data can plug in without index-only Affix logic (`CONTEXT.md`).

### 5. Validate densest stacks

Mentally (or in Preview) check at least: Race Against Time + Partial Veil + Fragile Spirit; Echo Limit + Unknown Count; Among Heroes with many board sounds. Confirm Submit stays reachable; timer/hearts readable; portraits Affix-legal per `CONTEXT.md`.

**Done when:** each stack has a note: what stays glanceable, what moved on-demand, what still risks clutter.

## Related skills

| Need | Skill |
|------|--------|
| Anchors, safe area, event-driven HUD vocabulary | `game-ui-ux` |
| Composable craft | `compose-ui` |
| Window size / adaptive | `jetpack-compose/adaptive` |
| Insets / system bars | `system/edge-to-edge` |
| Affix icons / HUD art family | `create-game-assets` + Figma skills |
| Domain Affix stacking | `CONTEXT.md` via domain docs |

## Out of scope

Rewriting Affix game rules, inventing new Affixes, or installing psychology/FTUE audit packs. Layout and chrome only.

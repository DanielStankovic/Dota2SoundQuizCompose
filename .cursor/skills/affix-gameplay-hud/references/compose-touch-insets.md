# Compose touch & insets — Affix HUD

Implementation checklist for step 4. Prefer Android primary docs over console/TV safe-zone numbers.

## Touch targets

- Interactive SoundCard / play control / Submit: at least **48.dp** ([Compose accessibility API defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults); [M3 target sizes](https://m3.material.io/foundations/designing/structure)).
- Use `Modifier.minimumInteractiveComponentSize()` when the visual is smaller than the hit target.
- Prefer ~8.dp gap between adjacent targets.

## Insets & edges

- Consume WindowInsets via Scaffold / `safeDrawing` patterns ([insets](https://developer.android.com/develop/ui/compose/system/insets-ui); project `system/edge-to-edge`).
- Keep critical Affix chrome (timer, hearts) inside the safe drawing area — notches clip fixed top padding.
- Journey screen today uses bottom-only Scaffold insets; when relocating chrome, re-verify status bar / display cutout.

## Adaptive packing

- Prefer `GridCells.Adaptive(minSize = …)` over a fixed 4-column grid when card count or width varies ([canonical layouts — feed/grid](https://developer.android.com/develop/adaptive-apps/guides/canonical-layouts)).
- Portrait phone is the primary viewport; tablet can add columns without shrinking hit targets.

## State wiring

- HUD visibility from `AffixUIState` / round Ready state — event-driven recomposition (Compose `StateFlow`), not ad-hoc booleans per Affix name in the UI layer.
- Portrait Affix effects (veil, blur): **hero id** keys so Dire slots share one strip component.

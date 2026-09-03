# Information hierarchy — Affix Journey HUD

Source of truth for step 2 of `affix-gameplay-hud`. Aligns with Material “key information discernable at a glance” ([M3 structure](https://m3.material.io/foundations/designing/structure)) and game-UI contextual HUD / clutter discipline (`game-ui-ux`, game-ui-design patterns).

## Regions (current screen shape)

Grounded in `JourneyGameScreen` / `JourneyGameData` today:

| Region | Role | Default tier |
|--------|------|----------------|
| Timer (`TimerDisplay`) | Race Against Time / Soundquake clock | Affix-conditional (`showTimer`) |
| Hearts | Lives | Glanceable when `showHearts`; Sudden Death still needs a clear “1 life” read |
| Marked / total counters | Selection progress | Glanceable when Affix allows; Unknown Count / Hidden Marks hide digits |
| Echo Limit plays left | Resource | Affix-conditional; high urgency when low |
| **Hero strip** | Identity / Affix veil-blur canvas | Glanceable identity; keep compact — not the primary action surface |
| Submit | Commit marks | Glanceable primary action — **thumb-reachable** |
| **Sound board** (`SoundCard` grid) | Listen + mark — **main** | Always glanceable primary content |
| Affix rules copy | Teaching | On-demand (`AffixInfoBottomSheet` / level entry) |

## Earn rules

1. **Sound board earns space first.** Shrinking SoundCards below reliable tap size to fit chrome fails the core loop.
2. **Affix chrome appears only when its Affix is active** — driven by `AffixUIState`, not always-on placeholders.
3. **Hero strip is supporting pane**, not a second full board. Portraits inform identity Affixes; they do not compete with SoundCards for “main.”
4. **Explain Affixes outside the play surface.** Persistent banners that restate Affix flavor text are clutter; use sheet / level briefing.
5. **Color alone is not state** (plays-left red/yellow needs a label or icon backup — Material / accessibility).

## Progressive disclosure map

| Player need | Reveal |
|-------------|--------|
| What Affix does | Level entry + bottom sheet |
| Who is veiled / blurred | Portrait treatment on strip (already Affix-owned) |
| How many correct marks | Counter when Affix allows |
| Time pressure | Timer only when Affix configures a timer |
| Full Radiant+Dire roster when dense | Prefer toggle / scroll / sheet — see `layout-options.md` |

## Anti-patterns (positive targets)

- Prefer **collapsing chrome** over **smaller SoundCards**.
- Prefer **one primary vertical flow**: status → compact hero strip → submit → expandable board.
- Prefer **hero-id-keyed** Affix portrait effects so Dire slots plug in without a second layout paradigm (`CONTEXT.md`).

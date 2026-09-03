# Layout options — sound board & hero strip

Ranked for this app: portrait phone, listen → mark → submit, Affix density, Radiant today / Radiant+Dire later (`CONTEXT.md`). Cite Android adaptive feeds ([canonical layouts](https://developer.android.com/develop/adaptive-apps/guides/canonical-layouts)), Material targets ([M3 structure](https://m3.material.io/foundations/designing/structure)), and HUD progressive disclosure (`game-ui-ux` references). Full citations: [`docs/research/journey-hud-layout-patterns.md`](../../../../docs/research/journey-hud-layout-patterns.md).

**Universal shell:** one Journey HUD; Affix flags compose chrome. Options below are packing strategies for that shell — not per-Affix screens.

Current code: fixed **4-column** `LazyVerticalGrid` of `SoundCard`; hero row sizes from `radiantHeroImages.size` only (`JourneyGameScreen`).

---

## Sound board options

| ID | Pattern | Pros | Cons | Rank for Journey |
|----|---------|------|------|------------------|
| **SB-A** | Keep full scrollable grid; **adaptive columns** (`GridCells.Adaptive(minSize = …)`) so cards stay ≥ ~48.dp tap | Preserves “see all sounds” mental model; Affix Soundquake reshuffles still make sense | Tall boards still scroll under Affix chrome | **Default improve** — first refactor |
| **SB-B** | Compact grid (smaller cards / 5 cols) | Fits more without scroll | Breaks 48.dp discipline; harder listen targets | Avoid unless cards stay ≥ 48.dp hit area |
| **SB-C** | **Paged board** (2–3 pages / HorizontalPager) | Caps vertical chrome; clearer “chunk” of sounds | Extra gesture; Soundquake UX must reset page | Strong when sound count is high |
| **SB-D** | Two-tier: **marked strip + searchable/scroll grid** | Focuses selection | More states; discovery cost | Later if mark count grows |
| **SB-E** | Replace cards with **chip/list rows** | Dense | Weak “play icon” affordance; brand break | Only if art direction changes |

**Recommendation:** Start **SB-A** (adaptive min size + weight scroll as today). If Affix chrome + many sounds still crowd Submit, move to **SB-C** for high-count levels only (level- or count-gated), keeping SB-A for small boards.

**Completion check:** primary play control on each card remains ≥ 48.dp; Submit never buried under the fold on a common phone height with timer+hearts on.

---

## Hero strip options (Radiant + Dire)

| ID | Pattern | Pros | Cons | Rank for Journey |
|----|---------|------|------|------------------|
| **HS-A** | Single row, **all portraits**, shrink-to-fit | Simple | Dire doubles count → tiny or overflow; fights Affix veil readability | Bad once Dire ships |
| **HS-B** | Single **horizontally scrollable** row (Radiant then Dire, faction tint) | One strip; scales to N heroes; Affix `?`/blur stay per slot | Off-screen heroes easy to miss | **Good default** if scroll affordance is obvious |
| **HS-C** | **Faction toggle** (Radiant \| Dire) — one faction visible | Clear MOBA metaphor; stable portrait size; Affix masks stay legible | Mystery Affixes that span factions need a “both” cue (badge / count) | **Best for Affix readability** |
| **HS-D** | Two stacked rows (Radiant / Dire) | All visible | Steals vertical space from sound board — worst Affix density | Avoid on portrait |
| **HS-E** | Compact avatars + **“roster” sheet** for full size | Minimal HUD | Extra tap for identity Affixes | Pair with HS-B/C when N is large |
| **HS-F** | Tab row + strip | Familiar | Chrome weight similar to HS-C | Equivalent to HS-C |

**Recommendation:** Prefer **HS-C** (faction toggle) when Dire ships — keeps portrait size for Partial Veil / Blurred Vision / Among Heroes. Use **HS-B** if product insists all portraits visible without a tap. Never **HS-D** on phone portrait if the sound board is the main surface.

**Affix constraint:** Partial Veil / blur / Hidden Hero must key **hero id**, not radiant-only index (`CONTEXT.md`). Toggle must not drop masked-slot semantics for off-faction heroes (show badge “masked on other faction” or always show masked ids in a mini overflow).

---

## Combined “clear, not cluttered” target layout

```
[ status: hearts · counter · plays ]     ← Affix-conditional bits only
[ hero strip: compact; toggle or scroll ] ← supporting
[ Submit ]                               ← thumb-friendly mid/lower
[ sound board: adaptive grid / pager ]   ← main, weight(1f)
```

Timer: single top-leading chip when `showTimer` — not a second status bar.

---

## Decision record template (paste into PR / chat)

- Sound board: SB-__ because __
- Hero strip: HS-__ because __
- Affix stacks checked: __
- Space freed vs current: __

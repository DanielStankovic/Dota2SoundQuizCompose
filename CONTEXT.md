# Dota 2 Spell Sound Quiz

Android quiz app where players identify Dota 2 spell sounds across game modes, track scores and coins, and optionally sync progress when signed in.

## Language

**Player Progress**:
The player's persistent game state: coins, per-mode high scores and play counts, and journey level. Distinct from who is signed in.
_Avoid_: User data (as the domain name), score row, game stats

**Auth**:
Whether the player is signed in to the cloud account used for sync and leaderboard identity.
_Avoid_: User (when you mean signed-in state), profile (when you mean auth)

**Invoker entry**:
Paying coins to start an Invoker mode run (`InvokerEntry`: affordability, spend, matching ad grant). A coin spend, not a mode result and not the Invoker Round itself.
_Avoid_: Invoker cost (as the domain name for the action), unlock

**Invoker Round**:
One Invoker mode run: pool load, orb queue, spell recipes, hearts, dual timers, speed ladder, playback, and connectivity. Distinct from Invoker entry (the coin spend to start) and from recording the mode result.
_Avoid_: Invoker session, Invoker game (as the domain name for the module)

**Journey Round**:
One playable Journey level attempt: loading the level, affix rules, marks, sound plays, submit, hearts, timer, and the extra-life continue gate. Distinct from the Journey level list UI and from Player Progress journey level.
_Avoid_: Journey session, Journey game (as the domain name for the module)

**Affix**:
A named rule from the remote `affixes` catalog that modifies a Journey Round (UI flags, hearts, timer, play limits, validation, board composition). A Journey level references affixes by id. AffixEngine applies strategies for known keys; unknown keys are skipped.
_Avoid_: modifier, perk, challenge rule (when you mean the catalog Affix)

**Sound Playback**:
Playing a spell `SoundModel` (local resource or remote URL), or a raw resource id, without callers knowing mapper/Uri details.
_Avoid_: SoundPlayer (when you mean the domain capability), play helper

**Sync Session**:
One boot-time sync of catalog data, optional wipe, leaderboard flush, Player Progress, and sound downloads, reported as progress events.
_Avoid_: Sync repository (as the domain name), boot sync pipeline

**Multiple Choice Sound Round**:
A Quiz or Fast Finger run over a shuffled sound pool with four name options per sound. Wrong-answer policy differs by mode (stay vs advance).
_Avoid_: Quiz round (when you mean the shared module), sound deck

**Extra Life Gate**:
Whether the player has already used the one-shot rewarded continue in a classic Quiz run.
_Avoid_: additional life flag (as the domain name), continue dialog (the UI)

**Mode Result**:
Submitting the outcome of a finished mode run: write Player Progress (highs, plays, journey level, result coins) and, when applicable, enqueue an offline leaderboard score. Distinct from Invoker entry (coin spend to start) and from Sync Session.
_Avoid_: score update (as the domain name), play-again submit

## Journey Round hearts (baseline)

Default Journey Round starts with **2 hearts**. Wrong submit drains a heart; at 0 hearts the player may use the one-shot **Extra Life Gate** (rewarded ad) unless an Affix disables it.

Do **not** raise the default to 3 — 3 hearts + Extra Life Gate is too forgiving.

| Affix | Starting hearts | Extra Life Gate |
|-------|-----------------|-----------------|
| (none) | 2 | Allowed (once) |
| **Fragile Spirit** | 1 | Allowed (once) — still one ad save |
| **Sudden Death** | 1 | **Disabled** — first fail ends the round |

Fragile Spirit ↔ Sudden Death remain mutually exclusive (same hearts aspect; different Gate policy).

## Journey Affix incompatibilities

When authoring Journey levels (affix + hero combinations), do **not** stack affixes that fight over the same mechanic. Code today does not enforce this — last strategy wins or effects noop — so level design must respect these pairs.

| Aspect | Mutually exclusive | Why |
|--------|--------------------|-----|
| Hearts / lives | **Fragile Spirit** ↔ **Sudden Death** | Both set starting hearts to 1; Sudden Death also disables Extra Life Gate. Pick one heart policy. |
| Hero portrait | **Blurred Vision** / **Blurred Vision 2** ↔ **The Hidden Hero** | Blur needs a visible hero portrait; Hidden Hero uses the same hero-row Image slot(s) but swaps in the `?` drawable (transparent PNG) instead of the hero art. Blurred Vision vs Blurred Vision 2: pick one intensity. |
| Hidden identity | **The Hidden Hero** ↔ **Among Heroes** | Different mystery UX: Hidden Hero keeps the hero layout and shows `?` art in the portrait slot; Among Heroes shows only real visible heroes (no `?`) and the player must infer a hidden hero from sounds / sound count. Do not stack. |
| Counter + hidden portrait | **Unknown Count** ↔ **The Hidden Hero** | Without a visible correct-count, players cannot tell there are more correct sounds than the visible heroes imply. |
| Round timer semantics | **Race Against Time** ↔ **Soundquake** / **Soundquake Aftershock** | Race Against Time ends the round on timeout. Soundquake variants reshuffle the board on timeout. Engine only takes the first timer config. |
| Soundquake variants | **Soundquake** ↔ **Soundquake Aftershock** | Same timer/reshuffle aspect; pick keep-marks vs clear-marks. |
| Decoy heroes | **Among Heroes** requires ≥2 heroes on the level | Needs visible heroes plus one mystery hero's sounds; meaningless on a single-hero level. |

Compatible examples (different aspects): Hidden Marks + Unknown Count; Echo Limit + Mirror Mode; Race Against Time + Blurred Vision; Among Heroes + visible sound count (so players can notice “too many” correct sounds).

### Echo Limit — level design

`data.limit` is per-level. When designing a level with Echo Limit:

1. Compute `boardSize` = sounds shown on that level (`max_sounds` / board composition).
2. Propose a limit tier, then **ask the product owner to confirm** before writing the level:
   - Hard: `boardSize` (must hear each tile at most once on average)
   - Medium: `boardSize + 5`
   - Easy: `boardSize + 10`
3. Never set limit below the number of correct sounds the player must identify (and prefer never below `boardSize` without an explicit product call).

### Soundquake — variants (catalog + data)

Two Affixes share the Soundquake timer/reshuffle loop; differ on mark handling. Both read:

- `timer` (seconds) — interval between quakes
- `remove_heart` (boolean) — if true, each quake also costs one heart

| Affix | Marks on quake | Icon |
|-------|----------------|------|
| **Soundquake** | Keep player marks; only positions shuffle | Existing `affix_soundquake` (+ optional heart-loss badge when `remove_heart`) |
| **Soundquake Aftershock** | Clear all marks, then shuffle | New distinct icon (+ same optional heart-loss badge) |

Research notes: `docs/research/hero-journey-affixes.md`.

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (ADR-0001 through ADR-0010). ADR-0002–0010 include **Revert** sections where those deepens need to be rolled back.

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

**Hero**:
A Dota 2 playable character whose portrait and spell sounds appear in a Journey Round (and elsewhere in the app). Use this word in player-facing copy and domain language.
_Avoid_: champion, character (when you mean a Dota 2 Hero)

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

## Journey hero portraits (Radiant today, Dire later)

Journey Round UI currently renders **Radiant** hero portraits only (`radiant_heroes`). **Dire** heroes (`dire_heroes`) already contribute sounds but are not shown in the hero row.

**Future:** show Dire portraits in the hero row as well. Any Affix that targets individual portraits (Partial Veil masks, partial Blurred Vision) must key off **hero id**, not radiant-only index, so Dire slots plug in without a redesign.

### Hidden-identity portrait modes

Three mutually exclusive Affixes own “who is shown vs masked” in the hero row:

| Affix | What the player sees | Sounds of masked / mystery heroes |
|-------|----------------------|-----------------------------------|
| **The Hidden Hero** | Every portrait slot is the `?` drawable | Still on the board (identity fully hidden) |
| **Partial Veil** | Mix of real portraits + one or more `?` slots; level authors which hero ids are masked via `masked_hero_ids` | Still on the board for masked ids |
| **Among Heroes** | Only real visible heroes (**no** `?`); infer the mystery hero from sounds / sound count | Mystery hero’s sounds are on the board |

**Partial Veil** guardrails (level design): ≥2 heroes on the level; ≥1 real portrait; ≥1 masked; masking **all** heroes is invalid — use **The Hidden Hero** instead. Catalog Affix is rule-only; mask membership lives on the Journey level.

### Blurred Vision — portrait membership

**Blurred Vision** / **Blurred Vision 2** turn blur on and set **strength** via Affix `data.blur`. Which portraits blur is level-authored on the Journey level as `blurred_hero_ids` (same split as Partial Veil / `masked_hero_ids`). Pick one of Blurred Vision / Blurred Vision 2 per level.

- Empty `blurred_hero_ids` → blur **nobody** (Affix intensity unused until authors fill ids).
- Listing every hero id → full-row blur is allowed (blur is readability, not identity mask).
- **Compatible** with **Partial Veil** and **Among Heroes** — blur **real art only**; never blur a `?` slot (mask wins on overlap).
- **Still incompatible** with **The Hidden Hero** — no meaningful real art to blur when every slot is `?`.

## Journey Affix incompatibilities

When authoring Journey levels (affix + hero combinations), do **not** stack affixes that fight over the same mechanic. Code today does not enforce this — last strategy wins or effects noop — so level design must respect these pairs.

| Aspect | Mutually exclusive | Why |
|--------|--------------------|-----|
| Hearts / lives | **Fragile Spirit** ↔ **Sudden Death** | Both set starting hearts to 1; Sudden Death also disables Extra Life Gate. Pick one heart policy. |
| Hidden identity (portrait policy) | **The Hidden Hero** ↔ **Partial Veil** ↔ **Among Heroes** | Three different mystery UXs for the same hero-row slots. Pick exactly one. |
| Full mask + blur | **Blurred Vision** / **Blurred Vision 2** ↔ **The Hidden Hero** | Blur needs real portrait art; Hidden Hero is all `?`. Blurred Vision vs Blurred Vision 2: pick one intensity. |
| Counter + full hidden portrait | **Unknown Count** ↔ **The Hidden Hero** | Without a visible correct-count, players cannot tell there are more correct sounds than the visible heroes imply. |
| Round timer semantics | **Race Against Time** ↔ **Soundquake** / **Soundquake Aftershock** | Race Against Time ends the round on timeout. Soundquake variants reshuffle the board on timeout. Engine only takes the first timer config. |
| Soundquake variants | **Soundquake** ↔ **Soundquake Aftershock** | Same timer/reshuffle aspect; pick keep-marks vs clear-marks. |
| Decoy heroes | **Among Heroes** requires ≥2 heroes on the level | Needs visible heroes plus one mystery hero's sounds; meaningless on a single-hero level. |
| Partial mask | **Partial Veil** requires ≥2 heroes; ≥1 visible + ≥1 masked | See Partial Veil guardrails above. |

Compatible examples (different aspects): Hidden Marks + Unknown Count; Echo Limit + Mirror Mode; Race Against Time + Blurred Vision; Among Heroes + visible sound count; **Partial Veil + Blurred Vision** (blur real slots only); **Among Heroes + Blurred Vision**.

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

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (ADR-0001 through ADR-0010). ADR-0002–0010 include **Revert** sections where those deepens need to be rolled back.

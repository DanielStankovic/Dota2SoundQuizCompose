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

**Hero Portrait Policy**:
Resolved Radiant/Dire portrait slots for a Journey Round (mask, omit, blur by hero id, drawable). Affix enable flags plus level-authored membership; distinct from HUD chrome.
_Avoid_: hero row resolver, portrait Affix join

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
The one-shot rewarded-ad continue in a Quiz or Journey Round. On Journey, one recover per round: **+1 heart** after a life fail, or **+`timer_extension_seconds`** after a Race Against Time timeout — not both. Sudden Death disables the Gate.
_Avoid_: continue dialog (the UI), additional life flag (as the domain name)

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

### Race Against Time — level timer + Gate

**Race Against Time** Affix turns on the round timer HUD and **ends the round on timeout**. Duration and ad buyback live on the Journey level (not Affix `data`):

- `timer_seconds` — starting countdown (level-authored; Affix `data.timer` is legacy fallback only)
- `timer_extension_seconds` — Extra Life Gate recover on timeout (default **20** if unset)

If the Gate is still unused when the Race timer hits 0, offer the ad → add `timer_extension_seconds` and continue. If the Gate was already spent on a heart recover (or Sudden Death disables it), timeout is Game Over.

Journey round timers (Race and Soundquake family) run only while the gameplay surface is clear — paused for Extra Life Gate dialog, fullscreen ads, and app background; they resume when the player is back on the uncovered game screen.

Time buyback is **Race-only**. Soundquake does not offer +time.

### Soundquake — timer source

Journey `timer_seconds` is the quake **interval** (same column as Race; Affix only picks reshuffle / marks / heart policy). Four catalog Affixes share the reshuffle loop. Race ↔ Soundquake family stay mutually exclusive, so one level timer column is enough.

- Author `timer_seconds` on the Journey level (recommended).
- If unset / non-positive: default **20** seconds (Soundquake Affix fallback; Affix `data.timer` is unused for this family).

A quake that drains the last heart (Fracture / Cataclysm) uses the same Extra Life Gate (+1 heart). Granting the heart continues the round so the next quake interval can run — not a time extension.

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

**Among Heroes** guardrails (level design): ≥2 heroes on the level; exactly one mystery hero authored as Journey `hidden_hero_id` (must be a member of `radiant_heroes` / `dire_heroes`). That id is **omitted** from the portrait row — never replaced with `?`. Null `hidden_hero_id` with the Affix on → noop (show everyone). The mystery id must **not** also appear in `masked_hero_ids` or `blurred_hero_ids` (no `?`/blur on a portrait that isn’t shown). Do **not** stack with **Unknown Count** — the sound counter is the main hint that a mystery hero exists. While only Radiant portraits render, authors should hide a Radiant id and leave ≥1 Radiant portrait visible. Catalog Affix is rule-only; mystery membership lives on the Journey level.

**Partial Veil** guardrails (level design): ≥2 heroes on the level; ≥1 real portrait; ≥1 masked; masking **all** heroes is invalid — use **The Hidden Hero** instead. Catalog Affix is rule-only; mask membership lives on the Journey level.

### Blurred Vision — portrait membership

**Blurred Vision** / **Blurred Vision 2** turn blur on and set **strength** via Affix `data.blur`. Which portraits blur is level-authored on the Journey level as `blurred_hero_ids` (same split as Partial Veil / `masked_hero_ids`). Pick one of Blurred Vision / Blurred Vision 2 per level.

- Empty `blurred_hero_ids` → blur **nobody** (Affix intensity unused until authors fill ids).
- Listing every hero id → full-row blur is allowed (blur is readability, not identity mask).
- **Compatible** with **Partial Veil** and **Among Heroes** — blur **real art only**; never blur a `?` slot (mask wins on overlap); never list the Among Heroes `hidden_hero_id` in `blurred_hero_ids` (that portrait is absent).
- **Still incompatible** with **The Hidden Hero** — no meaningful real art to blur when every slot is `?`.

## Journey Affix incompatibilities

When authoring Journey levels (affix + hero combinations), do **not** stack affixes that fight over the same mechanic. Code today does not enforce this — last strategy wins or effects noop — so level design must respect these pairs.

| Aspect | Mutually exclusive | Why |
|--------|--------------------|-----|
| Hearts / lives | **Fragile Spirit** ↔ **Sudden Death** | Both set starting hearts to 1; Sudden Death also disables Extra Life Gate. Pick one heart policy. |
| Hidden identity (portrait policy) | **The Hidden Hero** ↔ **Partial Veil** ↔ **Among Heroes** | Three different mystery UXs for the same hero-row slots. Pick exactly one. |
| Full mask + blur | **Blurred Vision** / **Blurred Vision 2** ↔ **The Hidden Hero** | Blur needs real portrait art; Hidden Hero is all `?`. Blurred Vision vs Blurred Vision 2: pick one intensity. |
| Counter + mystery without portrait cue | **Unknown Count** ↔ **The Hidden Hero** / **Among Heroes** | Without a visible correct-count, players cannot tell there are more correct sounds than the visible heroes imply. Among Heroes is stricter: there is also no `?` slot, so the counter is the main hint that a mystery hero exists. |
| Round timer semantics | **Race Against Time** ↔ **Soundquake** / **Soundquake Aftershock** / **Soundquake Fracture** / **Soundquake Cataclysm** | Race ends the round on timeout (with optional Extra Life Gate +time). Soundquake family reshuffles on interval. Engine only takes the first timer Affix; duration comes from Journey `timer_seconds`. |
| Soundquake variants | **Soundquake** ↔ **Soundquake Aftershock** ↔ **Soundquake Fracture** ↔ **Soundquake Cataclysm** | Same timer/reshuffle aspect; pick exactly one marks + heart policy. |
| Decoy heroes | **Among Heroes** requires ≥2 heroes on the level | Needs visible heroes plus one mystery hero's sounds; meaningless on a single-hero level. Mystery id = Journey `hidden_hero_id`; must not overlap `masked_hero_ids` / `blurred_hero_ids`. |
| Partial mask | **Partial Veil** requires ≥2 heroes; ≥1 visible + ≥1 masked | See Partial Veil guardrails above. |

Compatible examples (different aspects): Hidden Marks + Unknown Count; Echo Limit + Mirror Mode; Race Against Time + Blurred Vision; Among Heroes + visible sound count; **Partial Veil + Blurred Vision** (blur real slots only); **Among Heroes + Blurred Vision**.

### Echo Limit — level design

Echo Limit Affix **enables** the play budget; the budget itself is authored on the Journey level as `echo_limit_offset` (not Affix `data`):

`effectivePlays = max_sounds + echo_limit_offset`

- Missing / unset offset → **+5** (Medium default)
- Authors may set **`0`** for Hard (plays = board size)
- Offset is never negative (floor = board size)

When designing a level with Echo Limit:

1. Compute `boardSize` = sounds shown on that level (`max_sounds`).
2. Propose an offset tier, then **ask the product owner to confirm** before writing the level:
   - Hard: `echo_limit_offset = 0` → `boardSize`
   - Medium: `+5` (default) → `boardSize + 5`
   - Easy: `+10` → `boardSize + 10`
3. Never set plays below the number of correct sounds the player must identify (and prefer never below `boardSize` without an explicit product call).

### Soundquake — variants (catalog)

Four Affixes share the Soundquake timer/reshuffle loop. Interval seconds come from Journey **`timer_seconds`** (default **20** if unset). Affix `data` is empty (`{}`) — marks and heart policy are encoded by which Affix is chosen (no `remove_heart` flag).

| Affix | Marks on quake | Heart on quake | Icon |
|-------|----------------|----------------|------|
| **Soundquake** | Keep player marks; only positions shuffle | No | `affix_soundquake` |
| **Soundquake Aftershock** | Clear all marks, then shuffle | No | `affix_soundquake_aftershock` |
| **Soundquake Fracture** | Keep player marks | Yes (−1 heart; Extra Life Gate on last heart) | `affix_soundquake_fracture` (shared cracked-heart glyph with Cataclysm) |
| **Soundquake Cataclysm** | Clear all marks, then shuffle | Yes (−1 heart; Extra Life Gate on last heart) | `affix_soundquake_cataclysm` (same cracked-heart glyph as Fracture) |

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (ADR-0001 through ADR-0011). ADR-0002–0011 include **Revert** sections where those deepens need to be rolled back.

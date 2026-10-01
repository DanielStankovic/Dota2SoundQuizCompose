# Dota 2 Spell Sound Quiz

Android quiz app where players identify Dota 2 spell sounds across game modes, track scores and coins, and optionally sync progress when signed in.

## Language

**Player Progress**:
The player's persistent game state: coins, per-mode high scores and play counts, and journey level. Distinct from who is signed in and from Issue Progress.
_Avoid_: User data (as the domain name), score row, game stats

**Journey level**:
Highest Journey level the player has cleared. The level list unlocks the next playable level at `journey level + 1`. Distinct from a Journey Round (one attempt) and from arcade mode high scores.
_Avoid_: XP, player level, rank (when you mean Journey progress)

**Auth**:
Whether the player is signed in to the cloud account used for sync and leaderboard identity.
_Avoid_: User (when you mean signed-in state), profile (when you mean auth)

**Auth Session**:
Sign-in / sign-out orchestration: Google ID token Auth, Player Progress create-or-sync, leaderboard attach, and local reset on sign-out. Distinct from Auth as mere signed-in state and from Player Progress itself.
_Avoid_: AuthViewModel (as the domain name), login pipeline

**Invoker entry**:
Paying coins to start an Invoker mode run (`InvokerEntry`: affordability, spend, matching ad grant). A coin spend, not a mode result and not the Invoker Round itself.
_Avoid_: Invoker cost (as the domain name for the action), unlock

**Invoker Round**:
One Invoker mode run: pool load, orb queue, spell recipes, hearts, dual timers, speed ladder, playback, and connectivity. Distinct from Invoker entry (the coin spend to start) and from recording the mode result.
_Avoid_: Invoker session, Invoker game (as the domain name for the module)

**Journey Round**:
One playable Journey level attempt: loading the level, optional Journey Round briefing, affix rules, marks, sound plays, submit, hearts, timer, and the extra-life continue gate. Also owns Journey level list load and Affix fog / unlock presentation. Distinct from the Journey level list UI and from Player Progress journey level.
_Avoid_: Journey session, Journey game (as the domain name for the module)

**Affix**:
A named rule from the remote `affixes` catalog that modifies a Journey Round (UI flags, hearts, timer, play limits, validation, board composition). A Journey level references affixes by id. AffixEngine applies strategies for known keys; unknown keys are skipped.
_Avoid_: modifier, perk, challenge rule (when you mean the catalog Affix)

**Affix tray**:
On-demand list of the Affixes on the current Journey Round, opened from the play surface. Distinct from Journey HUD Chrome, Affix fog, and Affix icons on the Journey level list.
_Avoid_: affix drawer, side menu, modifier panel

**Affix fog**:
Journey level list rule that hides Affix identity beyond a look-ahead from journey level. Cleared levels, the next playable level (`journey level + 1`), and the next 3 locked levels show Affix icons; farther cards show one mystery token per Affix and no Affix info sheet. Distinct from Affix tray, Journey Round briefing, and Partial Veil.
_Avoid_: spoiler, lock, veil (when you mean this list rule)

**Journey Round briefing**:
The hold at the start of a Journey Round after Continue Journey when that level has Affixes: the Affix tray is open and clocks have not started. Distinct from Extra Life Gate pause and from mid-round Affix tray lookup.
_Avoid_: tutorial overlay, countdown gate, pause menu (when you mean this hold)

**Hero**:
A Dota 2 playable character whose portrait and spell sounds appear in a Journey Round (and elsewhere in the app). Use this word in player-facing copy and domain language.
_Avoid_: champion, character (when you mean a Dota 2 Hero)

**Hero Catalog**:
Inspection of every Hero and that Hero's Sounds — listen, not play. Distinct from a Journey Round board and from Issue Library.
_Avoid_: hero overview, ability browser, Game Mode (when you mean this)

**Sound**:
A named spell clip on a Hero (spell name + audio). Distinct from Sound Playback (the act of playing it).
_Avoid_: ability, spell (when you mean this catalog row)

**Hero Portrait Policy**:
Resolved Radiant/Dire portrait slots for a Journey Round (mask, omit, blur by hero id, drawable). Affix enable flags plus level-authored membership; distinct from HUD chrome.
_Avoid_: hero row resolver, portrait Affix join

**Journey HUD Chrome**:
Round-owned Affix status-band flags for the Journey game screen (hearts, counters, timer visibility). Distinct from AffixUIState and from Hero Portrait Policy.
_Avoid_: AffixUIState (when you mean the Screen contract)

**Journey Round Timer**:
The Race Against Time / Soundquake interval controller inside a Journey Round (tick, pause under ads, Extra Life Gate +time / interval restart). Distinct from Affix timer enable flags.
_Avoid_: countdown helper, Soundquake loop (as the module name)

**Sound Playback**:
Playing a spell `SoundModel` (local resource or remote URL), or a raw resource id, without callers knowing mapper/Uri details.
_Avoid_: SoundPlayer (when you mean the domain capability), play helper

**Sync Session**:
One boot-time sync of catalog data, optional wipe, leaderboard flush, Player Progress, Issue catalog and art, Issue Progress, and sound downloads, reported as progress events.
_Avoid_: Sync repository (as the domain name), boot sync pipeline

**Multiple Choice Sound Round**:
A Quiz or Fast Finger run over a shuffled sound pool with four name options per sound. Wrong-answer policy differs by mode (stay vs advance).
_Avoid_: Quiz round (when you mean the shared module), sound deck

**Extra Life Gate**:
The one-shot rewarded-ad continue in a Quiz or Journey Round. On Journey, one recover per round: **+1 heart** after a life fail, or **+`timer_extension_seconds`** after a Race Against Time timeout — not both. Sudden Death disables the Gate.
_Avoid_: continue dialog (the UI), additional life flag (as the domain name)

**Mode Result**:
Submitting the outcome of a finished mode run: write Player Progress (highs, plays, journey level, result coins) and, when applicable, enqueue an offline leaderboard score and grant the next Chapter of the Active Issue (or open the Issue Picker when there is none). Distinct from Invoker entry (coin spend to start) and from Sync Session.
_Avoid_: score update (as the domain name), play-again submit

**Level Clear Time**:
The player's best finish duration for one Journey level, used for that level's ranking when Auth is present. The board shows the top 10; if the signed-in player is outside the top 10, their row is appended last with their real rank (same pattern as the arcade leaderboard). Extra Life Gate use does not disqualify the record. Distinct from arcade mode high scores and from journey level.
_Avoid_: score (when you mean clear time), Journey high score

**Issue**:
A serial of Chapters about one Protagonist. Chapter count is authored per Issue. Distinct from Journey level, from a Journey Round, and from the Heroes on that round's board.
_Avoid_: comic, story (when you mean this serial), season, album, campaign

**Chapter**:
One beat of an Issue: prose over that Issue’s Protagonist illustration, shown fullscreen after a first-clear. No Chapter title; chrome is `n / total`. Distinct from Journey Round briefing and from Mode Result coins / Level Clear Time.
_Avoid_: page, scene, lore entry

**Protagonist**:
The Hero an Issue is about. Distinct from Heroes on the Journey Round board that granted the Chapter.
_Avoid_: featured hero (when you mean the Issue lead), champion

**Active Issue**:
The Issue that receives the next Chapter grant. None until the player picks; they cannot pick another until this one is complete.
_Avoid_: current story, selected campaign

**Issue Picker**:
The mandatory choice among uncompleted Issues when the player has no Active Issue (cover, synopsis, Chapter count). Distinct from the Chapter reader and from the Issue Library.
_Avoid_: story select, campaign picker

**Issue Library**:
The Journey level-list entry into the Active Issue and completed Issues. Distinct from the Issue Picker (Issues not yet started).
_Avoid_: story archive, comic rack

**Reading Locale**:
The player's preferred language for Chapter bodies, chosen from a curated list of major world languages (one code per language; regional dialects share that code). Distinct from the app UI locale. Missing bodies fall back to English until a translation exists.
_Avoid_: story language, app language (when you mean Chapter bodies), dialect, regional variant (en-US, pt-BR as a second English/Portuguese)

**Issue Progress**:
The player's completed Issues, Active Issue (or none), Chapters unlocked in the Active Issue, owed Chapter grants, and Reading Locale. Persistent game state; not a currency; not journey level.
_Avoid_: XP, story level, album, sticker book

## Journey progression (spine)

Journey is the primary engagement path. Quiz, Fast Finger, and Invoker are side modes.

- Advance **journey level** only via Mode Result on a win: `max(current, cleared)` — never regress on replay.
- First clear of a level grants **20** coins via Mode Result, and the next **Chapter** of the **Active Issue** (or the **Issue Picker** when there is none). Replay grants **0** coins and no Chapter.
- Fail → free retry. No post-fail assist beyond the **Extra Life Gate**. If an Affix disables the Gate, the level stays as authored for all players. Fail does not grant or revoke a Chapter.
- Affix / Hero difficulty: introduce **one** new cognitive load per band (more Heroes, or a new Affix family, or a stack — not all at once). Mix those axes inside the current set: a hard level is a **guest**, not a new floor (later levels may drop that Affix and raise Hero count or wrong-sound count). Launch catalog length and remix peak: [ADR-0020](docs/adr/0020-journey-level-catalog.md).
- **Affix fog** on the Journey level list: Affix identity is visible through the next 3 locked levels after the playable one; farther cards show mystery tokens only (count, not names). The Journey Round still reveals Affixes at play time.
- Do **not** add a second currency or XP system for Journey progress. **Issue Progress** is a checklist of Chapters, not XP.
- An Issue's Protagonist and Chapters are catalog. They are **not** authored on the Journey level row (board Heroes / Affixes do not pick the Issue).
- Invoker entry cost remains **200** coins unless economy is revisited after Journey first-clear coins ship.
- Near-term competition: per-level **Level Clear Time** boards (Auth) — top 10, plus the current player's standing appended last when outside the top 10. Weekly Affix gauntlet and reward arcade leaderboards are out of scope for this spine decision (see ADR-0015).
- Implementation: [ADR-0015](docs/adr/0015-journey-progression-spine.md) (spine), [ADR-0016](docs/adr/0016-level-clear-time.md) (`LevelClearTime` module), [ADR-0019](docs/adr/0019-journey-issue.md) (`JourneyIssue` catalog + Issue Progress), [ADR-0020](docs/adr/0020-journey-level-catalog.md) (120-level catalog).

## Journey Issues

- One **Issue** = one **Protagonist**, one illustration (picker cover and reader backdrop), and an authored Chapter count (10, 15, 20, …). Board Heroes / Affixes do not choose the Issue. Chapters have no titles.
- **Catalog invariant:** every playable Journey level has a Chapter grant behind it. Count only **active** Issues. Do not author more Journey levels than that sum; author toward **equal** counts. Do not unpublish an Issue if that would make active Chapter capacity smaller than the Journey level count.
- Players start with **no Active Issue**. Hero’s Journey is unpublished at this feature’s launch, so every player starts Issue Progress from scratch (no fill-from-existing-`journey level`; staging testers included).
- A first-clear with no Active Issue opens the **Issue Picker** (cover, short synopsis, Chapter count). Completed Issues are omitted. The player must pick; quitting without a pick leaves owed grants. The next first-clear shows the Picker again, then unlocks one Chapter per owed first-clear (e.g. pick after two first-clears → two Chapters). If applying owed **completes** that Issue and owed remains, the Picker opens **again in the same session** (no Chapter reader between picks) until owed is 0 or nothing is left to pick. If they complete an Issue with owed already 0, the next Picker waits until the **next** first-clear.
- While an Issue is Active, the player cannot start another.
- First-clear path: drain owed via Picker(s) if needed → **one** Chapter reader on the **newest** Chapter just granted (if that grant completed the Issue, still show its last Chapter this once; previous/next only inside that Issue) → **Continue** (always enabled, even on an older Chapter) → existing Mode Result (coins / time / LCT). Reader: one bundled music bed, repeating zoom, prose revealed line-by-line (tap shows the rest), scroll if needed, `n / total`. Earlier Issues completed in a picker chain are in the Library only.
- **Issue Library** on the Journey level list re-opens the Active Issue and completed Issues (close/back only — no Continue to Mode Result). The post-clear reader does not switch Issues.
- English Chapter bodies sync at boot. **Reading Locale** is stored locally and on the server. Opening a locale uses local Room first, then the server catalog, and fetches the **whole Issue** (not one Chapter). If that locale is still missing, a server function translates the whole Issue, stores it, and syncs it down. Missing locale falls back to English until that finishes. Device language does not pick Reading Locale.

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

Journey round timers (Race and Soundquake family) run only while the gameplay surface is clear — paused for Extra Life Gate dialog, fullscreen ads, and app background; they resume when the player is back on the uncovered game screen. The Affix tray and Affix description sheet do not pause clocks.

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

**The Hidden Hero** with **Mirror Mode** is allowed but **expert-only** (see Mirror Mode).

**Among Heroes** guardrails (level design): ≥2 heroes on the level; exactly one mystery hero authored as Journey `hidden_hero_id` (must be a member of `radiant_heroes` / `dire_heroes`). That id is **omitted** from the portrait row — never replaced with `?`. Null `hidden_hero_id` with the Affix on → noop (show everyone). The mystery id must **not** also appear in `masked_hero_ids` or `blurred_hero_ids` (no `?`/blur on a portrait that isn’t shown). Do **not** stack with **Unknown Count** or **Mirror Mode** — the sound counter’s correct-sound total is the main hint that a mystery hero exists (Unknown Count hides it; Mirror Mode retargets it to decoys). While only Radiant portraits render, authors should hide a Radiant id and leave ≥1 Radiant portrait visible. Catalog Affix is rule-only; mystery membership lives on the Journey level.

**Partial Veil** guardrails (level design): ≥2 heroes on the level; ≥1 real portrait; ≥1 masked; masking **all** heroes is invalid — use **The Hidden Hero** instead. Catalog Affix is rule-only; mask membership lives on the Journey level. Stacking with **Mirror Mode** is allowed but **expert-only** (see Mirror Mode).

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
| Submit quota + mystery hint | **Mirror Mode** ↔ **Among Heroes** | Mirror Mode’s HUD denominator is decoys (required marks). Among Heroes uses the visible correct-count as the hint a mystery hero exists. |
| Round timer semantics | **Race Against Time** ↔ **Soundquake** / **Soundquake Aftershock** / **Soundquake Fracture** / **Soundquake Cataclysm** | Race ends the round on timeout (with optional Extra Life Gate +time). Soundquake family reshuffles on interval. Engine only takes the first timer Affix; duration comes from Journey `timer_seconds`. |
| Soundquake variants | **Soundquake** ↔ **Soundquake Aftershock** ↔ **Soundquake Fracture** ↔ **Soundquake Cataclysm** | Same timer/reshuffle aspect; pick exactly one marks + heart policy. |
| Decoy heroes | **Among Heroes** requires ≥2 heroes on the level | Needs visible heroes plus one mystery hero's sounds; meaningless on a single-hero level. Mystery id = Journey `hidden_hero_id`; must not overlap `masked_hero_ids` / `blurred_hero_ids`. |
| Partial mask | **Partial Veil** requires ≥2 heroes; ≥1 visible + ≥1 masked | See Partial Veil guardrails above. |

Compatible examples (different aspects): Hidden Marks + Unknown Count; Echo Limit + Mirror Mode; Race Against Time + Blurred Vision; Among Heroes + visible sound count; **Partial Veil + Blurred Vision** (blur real slots only); **Among Heroes + Blurred Vision**.

Expert-only (legal, late band — same listen→classify loop, second load): **Mirror Mode** + **The Hidden Hero**; **Mirror Mode** + **Partial Veil**. See Mirror Mode.

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
3. Never set plays below the number of marks the player must identify — correct sounds normally, decoys under **Mirror Mode** (and prefer never below `boardSize` without an explicit product call).

### Mirror Mode — level design

**Mirror Mode** inverts a legal submit: mark every decoy on the board, leave every correct sound unmarked.

The Journey HUD sound counter denominator is **required marks** = decoys on the board (`max_sounds` − correct sounds), not the hero-sound count. Authors must leave room for decoys (`max_sounds` > correct-sound count); a board of only correct sounds is unwinnable.

Do **not** stack with **Among Heroes**. **Unknown Count** still hides the denominator; **Hidden Marks** still hides the marked count.

**Expert-only stacks** (allowed, late band only — do not treat like Echo Limit + Mirror Mode):

| Stack | Why it is legal | Why it is expert |
|-------|-----------------|------------------|
| **Mirror Mode** + **The Hidden Hero** | `?` slots stay the identity cue; HUD quota is decoys | Player must identify the whole hidden roster, then mark only leftovers |
| **Mirror Mode** + **Partial Veil** | `?` slots still prove a masked hero exists | Three buckets: visible-hero sounds, `?`-hero sounds, true decoys. Mirror marks **only decoys**; `?` hero sounds stay unmarked. “Wrong” is not “isn’t a visible portrait.” |

### Soundquake — variants (catalog)

Four Affixes share the Soundquake timer/reshuffle loop. Interval seconds come from Journey **`timer_seconds`** (default **20** if unset). Affix `data` is empty (`{}`) — marks and heart policy are encoded by which Affix is chosen (no `remove_heart` flag).

| Affix | Marks on quake | Heart on quake | Icon |
|-------|----------------|----------------|------|
| **Soundquake** | Keep player marks; only positions shuffle | No | `affix_soundquake` |
| **Soundquake Aftershock** | Clear all marks, then shuffle | No | `affix_soundquake_aftershock` |
| **Soundquake Fracture** | Keep player marks | Yes (−1 heart; Extra Life Gate on last heart) | `affix_soundquake_fracture` (shared cracked-heart glyph with Cataclysm) |
| **Soundquake Cataclysm** | Clear all marks, then shuffle | Yes (−1 heart; Extra Life Gate on last heart) | `affix_soundquake_cataclysm` (same cracked-heart glyph as Fracture) |

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (ADR-0001 through ADR-0020). ADR-0002–0014 and ADR-0016 include **Revert** sections where those deepens need to be rolled back. Journey progression spine: [ADR-0015](docs/adr/0015-journey-progression-spine.md). Level Clear Time module: [ADR-0016](docs/adr/0016-level-clear-time.md). Journey Issue catalog + Issue Progress: [ADR-0019](docs/adr/0019-journey-issue.md). Journey level catalog pacing: [ADR-0020](docs/adr/0020-journey-level-catalog.md).

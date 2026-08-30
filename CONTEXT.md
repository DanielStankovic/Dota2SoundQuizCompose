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
Paying coins to start an Invoker mode run. A coin spend, not a mode result.
_Avoid_: Invoker cost (as the domain name for the action), unlock

**Journey Round**:
One playable Journey level attempt: loading the level, affix rules, marks, sound plays, submit, hearts, timer, and the extra-life continue gate. Distinct from the Journey level list UI and from Player Progress journey level.
_Avoid_: Journey session, Journey game (as the domain name for the module)

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

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (ADR-0001 through ADR-0007). ADR-0002–0007 include **Revert** sections where those deepens need to be rolled back.

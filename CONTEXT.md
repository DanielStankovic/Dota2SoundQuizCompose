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
Playing a spell `SoundModel` (local resource or remote URL) without callers knowing mapper/Uri details.
_Avoid_: SoundPlayer (when you mean the domain capability), play helper

## Decisions

Architecture decisions that affect these terms live in `docs/adr/` (notably ADR-0001 sync policy, ADR-0002 Player Progress repository, ADR-0003 Journey Round). ADR-0002 and ADR-0003 include **Revert** sections if those deepens need to be rolled back.

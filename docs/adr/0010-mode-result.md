# Deepen Mode Result submission

Finishing a mode run (Player Progress record + optional offline leaderboard enqueue, game-mode codes, Fast Finger time→mode map) lives in one concrete module: `ModeResult`. ADR-0002 left leaderboard outside Player Progress; this module is the deepen across that seam. `ScoreViewModel` stays a thin adapter for progress observation and leaderboard error events. Play-again / Journey result screens submit once via `LaunchedEffect` (not during composition).

## Revert

1. Move progress + leaderboard pairing and game-code mapping back into `ScoreViewModel`.
2. Delete `ModeResult`; restore composition-time `update*` calls if desired.
3. Supersede this ADR.

## Related

- Glossary: `CONTEXT.md` (**Mode Result**, **Player Progress**)
- Batching vs coin sync: ADR-0001
- Player Progress: ADR-0002

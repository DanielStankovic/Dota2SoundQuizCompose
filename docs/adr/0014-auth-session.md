# Deepen Auth Session into one data-layer module

**Auth Session** (Google ID token sign-in, Player Progress create-or-sync, leaderboard attach, sign-out wipe) lives in one concrete Data-layer module: `AuthSession`. `AuthViewModel` stays a thin presentation adapter (events, strings, Profile progress observe). Complements [ADR-0002](0002-player-progress-repository.md) (“Auth keeps sign-in/out”) without reopening Player Progress or Mode Result.

## Revert

1. Move `signInWithGoogle` / `signOut` / existence check back into `AuthViewModel`.
2. Delete `AuthSession.kt`; supersede this ADR; drop glossary **Auth Session** if unused.

## Related

- Glossary: `CONTEXT.md` (**Auth Session**, **Auth**, **Player Progress**)
- Player Progress: [ADR-0002](0002-player-progress-repository.md)

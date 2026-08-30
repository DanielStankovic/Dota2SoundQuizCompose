# Coin spends sync immediately; mode results batch

Player Progress mutations are not all synced the same way. **`adjustCoins`** (Invoker entry spends and other signed coin balance changes such as ad grants) writes locally and then attempts an immediate cloud sync when the player is signed in. **Mode results** (highs, play counts, journey level, and coins granted as part of finishing a mode) stay local until an explicit sync point (boot Sync, profile, sign-in/out).

The motivating case is **Invoker entry**: pushing that payment ASAP reduces abuse windows and keeps the server close to the paid balance. Mode results can batch. If `adjustCoins` sync fails (e.g. offline), the local change still stands and play may continue; a later sync applies a coin **delta** via local `syncedCoinValue`, not a blind overwrite from the server.

**Rejected**: syncing every mutation immediately (more races, worse offline UX); batching Invoker entry with mode results (larger window where a reinstall or `synced*` reset can disagree with the server about a paid entry).

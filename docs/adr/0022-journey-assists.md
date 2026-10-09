# Journey Assists are mid-round knowledge buys, not fail-recover mercy

Optional **Journey Assists** soften a Journey Round by selling information (not skipping the level). They sit beside the Extra Life Gate: Gate recovers after fail; Assists help before fail. Soft-locks still prefer free retries and level retuning; Assists are the paid valve when Affix stacks get denser.

**Two Assists (permanent per Journey level once unlocked, synced):**

- **Sound Count Reveal** — portrait badges show how many correct Sounds each visible Hero has on the board. Cost **60** coins. Disabled under **Among Heroes** and **Unknown Count** (would soft-wipe those Affixes).
- **Sound Sampler** — portrait (or Sampler badge) opens an in-round listen overlay of that Hero’s catalog Sounds (Hero Catalog–like). Unlock via rewarded ad (whole level). Timer keeps running; plays do **not** consume Echo Limit. Disabled under **Among Heroes**. Allowed on Blur / Partial Veil / Hidden Hero (`?`) — paid identity leak is intentional. **3** new Sound Sampler unlocks per UTC day (local anti-farm); already-unlocked levels stay unlocked. Wire/DB column remains `primer_unlocked` (legacy name).

**Monetization:** Count = coins only; Sound Sampler = rewarded ad only (**3**/UTC-day new unlocks). **Coin Ad Grant** (+**40** coins) is the shared faucet from Assist tray (when broke) and Home Invoker: **5 min** cooldown + **5**/UTC-day cap (anti-farm). Invoker entry costs **40** coins (no matching Invoker-only ad grant). Local day buckets use wall UTC date cross-checked with `elapsedRealtime` so casual device date changes do not reset caps; not cheat-proof (reboot/prefs wipe) — server caps if this must be hard.

**LCT:** Assists do not disqualify Level Clear Time; Sound Sampler overlay does not pause the clear clock (same as Affix tray). Rewarded ads pause clocks as today.

**Storage:** Room + Supabase `journey_assist_unlock` per `(user_id, level)` with OR-merge unlocks; flush/hydrate like Level Clear Time. Do not copy staging player unlocks to prod.

## Related

- Spine: ADR-0015
- Glossary: `CONTEXT.md` (Journey Assist, Sound Count Reveal, Sound Sampler, Coin Ad Grant)
- Gate: ADR-0007
- LCT: ADR-0016
- Coins sync: ADR-0001

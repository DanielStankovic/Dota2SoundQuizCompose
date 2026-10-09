# Journey Assists are mid-round knowledge buys, not fail-recover mercy

Optional **Journey Assists** soften a Journey Round by selling information (not skipping the level). They sit beside the Extra Life Gate: Gate recovers after fail; Assists help before fail. Soft-locks still prefer free retries and level retuning; Assists are the paid valve when Affix stacks get denser.

**Two Assists (permanent per Journey level once unlocked, synced):**

- **Sound Count Reveal** — portrait badges show how many correct Sounds each visible Hero has on the board. Cost **60** coins. Disabled under **Among Heroes** and **Unknown Count** (would soft-wipe those Affixes).
- **Hero Sound Primer** — portrait (or Primer badge) opens an in-round listen overlay of that Hero’s catalog Sounds (Hero Catalog–like). Unlock via rewarded ad (whole level). Timer keeps running; plays do **not** consume Echo Limit. Disabled under **Among Heroes**. Allowed on Blur / Partial Veil / Hidden Hero (`?`) — paid identity leak is intentional.

**Monetization:** Count = coins only; Primer = rewarded ad only. **Coin Ad Grant** (+**40** coins) from Assist tray (when broke) and Home / coin-info: **5 min** cooldown + **5**/UTC-day cap (anti-farm). Distinct from Invoker entry’s situational +200 ad grant.

**LCT:** Assists do not disqualify Level Clear Time; Primer overlay does not pause the clear clock (same as Affix tray). Rewarded ads pause clocks as today.

**Storage:** Room + Supabase `journey_assist_unlock` per `(user_id, level)` with OR-merge unlocks; flush/hydrate like Level Clear Time. Do not copy staging player unlocks to prod.

## Related

- Spine: ADR-0015
- Glossary: `CONTEXT.md` (Journey Assist, Sound Count Reveal, Hero Sound Primer, Coin Ad Grant)
- Gate: ADR-0007
- LCT: ADR-0016
- Coins sync: ADR-0001

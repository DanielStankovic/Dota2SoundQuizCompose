# Affix tray is on-demand; Continue Journey holds clocks until Begin

A Journey Round exposes Affix identity through an **Affix tray** (right-of-Submit tab → side list → existing description sheet), not a persistent banner. **Journey Round briefing** applies only to **Continue Journey** onto a level that has Affixes: the tray opens and Journey Round Timer / Level Clear Time do not start until **Begin**. Play from the Journey level list and Try Again hot-start. Mid-round tray lookup does not pause clocks (not an Extra Life Gate / ad interruption).

## Revert

1. Drop `briefing` from `JourneyGameDestination`; always start clocks in `startRound`.
2. Remove Affix tray UI, `awaitingBegin` / `affixes` on Ready, and `beginRound()`.
3. Supersede this ADR; drop glossary **Affix tray** / **Journey Round briefing** if unused.

## Related

- Glossary: `CONTEXT.md` (**Affix tray**, **Journey Round briefing**, **Journey Round Timer**, **Affix**)
- HUD chrome stays the status band: [ADR-0012](0012-journey-hud-chrome.md)
- Timer pause policy: [ADR-0013](0013-journey-round-timer.md)

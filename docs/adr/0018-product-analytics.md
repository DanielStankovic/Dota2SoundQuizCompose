# Product analytics is one Firebase module

Studio-facing product events (mode mix, Journey Round start/end, Extra Life Gate, coins, Auth) live in `ProductAnalytics`. Firebase Analytics stays behind that module. Player Progress, Mode Result, and Level Clear Time stay player save / competition — they are not the dashboard.

Event names, parameters, and when they fire: [analytics-events.md](../analytics-events.md).

## Revert

1. Remove `ProductAnalytics` / `AnalyticsMode`, `FirebaseAnalytics` from `FirebaseModule`, and call sites.
2. Delete `docs/analytics-events.md` and this ADR.

## Related

- Glossary: `CONTEXT.md` (Journey Round, Mode Result, Extra Life Gate, Invoker entry, Player Progress)
- Research (gitignored): `docs/research/journey-product-analytics.md`

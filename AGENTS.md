# Agent notes

`AGENTS.md` is always-loaded guidance for agents in this repo: short pointers to where work lives and which conventions apply. Prefer editing the linked docs over bloating this file.

## Agent skills

### Issue tracker

Asana project **Dota 2 Spell Sound Quiz** only (via Asana MCP). See `docs/agents/issue-tracker.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

### Product analytics

Firebase / GA4 event names, parameters, and user-flow timing: `docs/analytics-events.md`. ADR-0018.

### Journey Affix HUD

Touch-first Journey gameplay layout / Affix chrome / sound board / hero strip: skill `affix-gameplay-hud`. Related: `game-ui-ux`, `create-game-assets`.

### Journey level catalog

Author Journey levels (toolkit rollercoaster, 120-level seed, Affix / Hero / decoy mix): skill `journey-level-authoring`. Combo rules: `CONTEXT.md`. Prod copy: `docs/agents/staging-prod-sync.md`. ADR-0020.

### Gameplay / progression meta

Journey and app-wide progression, difficulty pacing, economy friction, leaderboards: Stanestane skills `game-design-core-loop-extractor`, `game-design-design-pillars-extractor`, `game-design-goal-density-and-immediacy-audit`, `game-design-flow-audit`, `game-design-failure-loop-audit`, `game-design-friction-journey-audit`, `game-design-leaderboard-audit`, `game-design-leaderboard-builder`, `game-design-feature-prioritization`. Prefer existing `CONTEXT.md` terms (coins, journey level, Mode Result) over inventing parallel XP/economy language.

### Verification

Default: **lint only** — do not build or run the app unless the user explicitly asks. **Never** add or expand automated unit or instrumented tests. See `docs/agents/verification.md`.

### Staging → prod sync

Journey/Affix schema or seed that must reach prod later: update the living Asana sync ticket (remap ids; no staging FK copy). See `docs/agents/staging-prod-sync.md`.

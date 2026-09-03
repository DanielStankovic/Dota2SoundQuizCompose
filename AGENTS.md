# Agent notes

`AGENTS.md` is always-loaded guidance for agents in this repo: short pointers to where work lives and which conventions apply. Prefer editing the linked docs over bloating this file.

## Agent skills

### Issue tracker

Asana project **Dota 2 Spell Sound Quiz** only (via Asana MCP). See `docs/agents/issue-tracker.md`.

### Domain docs

Single-context: root `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

### Journey Affix HUD

Touch-first Journey gameplay layout / Affix chrome / sound board / hero strip: skill `affix-gameplay-hud`. Related: `game-ui-ux`, `create-game-assets`.

### Verification

Default: **lint only** — do not build or run the app unless the user explicitly asks. **Never** add or expand automated unit or instrumented tests. See `docs/agents/verification.md`.

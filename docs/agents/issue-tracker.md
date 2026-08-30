# Issue tracker: Asana

All specs and tickets for this repo live on Asana — AI-created and human-created, same board.

## Scope (hard rule)

Work **only** in:

| | Name | GID |
|---|---|---|
| Workspace | Dota 2 Spell Sound Quiz | `1203335126561734` |
| Project | Dota 2 Spell Sound Quiz | `1203335128322637` |

Do **not** create or move tickets into other workspaces or projects (including **Psp Game**).

## How to create / update

Use the Asana MCP (`create_tasks`, `update_tasks`, `get_task`, `search_tasks`, `add_comment`, etc.).

- Default new work into project gid `1203335128322637` (section **To do** unless the user says otherwise).
- **No assignee** — solo personal project; ignore other project members.
- Specs and tickets both live as Asana tasks on this board.

## Naming

Titles should be short and understandable on their own.

**AI-created** tasks must start with `[AI]` so authorship is obvious at a glance.

## Type (feature / improvement / bug)

Asana **Custom Fields are not available** on this workspace's plan. Encode type in the **task title** instead (and optionally the first line of the description):

| Type | Title marker | Branch prefix |
|---|---|---|
| New capability | `[feature]` | `feat/` |
| Enhance existing behaviour | `[improvement]` | `improve/` |
| Defect | `[bug]` | `fix/` |

Examples:

- `[AI][feature] Play button shows loading while sound fetches`
- `[AI][bug] Quiz crashes when audio decode fails`
- `[improvement] Speed up hero list scroll` (human-created; no `[AI]`)

Optional first line of notes for parsers: `Type: feature` | `Type: improvement` | `Type: bug`.

When creating a git branch for a ticket, follow `.cursor/skills/git-branch-naming/SKILL.md`.

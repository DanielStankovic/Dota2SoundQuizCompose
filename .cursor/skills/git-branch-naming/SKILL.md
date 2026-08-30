---
name: git-branch-naming
description: Name and create git branches from Asana ticket type and title. Use when starting work on a ticket, creating a feature branch, or deriving a branch name from a task marked feature, improvement, or bug.
---

# Git branch naming

Derive branch names from ticket **Type** + a short slug of the title. Create the branch only when starting implementation (or when the user asks).

## Type → prefix

| Ticket type | Prefix |
|---|---|
| feature | `feat/` |
| improvement | `improve/` |
| bug | `fix/` |

Read type from the Asana title markers `[feature]` / `[improvement]` / `[bug]`, or from a `Type: …` line in notes. If missing, ask once; do not guess silently.

## Slug

1. Strip `[AI]`, type markers, and punctuation from the title.
2. Lowercase; spaces → hyphens; keep only `[a-z0-9-]`.
3. Collapse repeated hyphens; trim ends.
4. Cap around **40** characters at a word boundary when possible.

**Form:** `<prefix><slug>`  
Examples: `feat/play-button-loading`, `fix/quiz-crash-audio-decode`, `improve/hero-list-scroll`

## Create

From an up-to-date default branch (`main` / `master` as used by the repo):

```bash
git checkout -b <prefix><slug>
```

If the name already exists locally or remotely, append a short disambiguator (`-2`, or a truncated Asana task gid).

## Do not

- Put spaces or uppercase in branch names
- Use `chore/` / `refactor/` unless the user overrides Type
- Commit or push unless the user asked

# Issue translations

One **unit** per file: `docs/story/i18n/{locale}/{slug}.md`.

English sources stay in `docs/story/*.md` (not `archive/`). Do not add `en` here.

Format and the retell/edit loop: skill `issue-catalog-translation`. Full matrix via subagents: skill `issue-catalog-wave`. Locale codes: `scripts/story-catalog/locales.py`. To build SQL from these files: `python3 scripts/story-catalog/seed_i18n.py --check` then `--locale <code>`.

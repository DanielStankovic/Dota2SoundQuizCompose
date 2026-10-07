# Story catalog seeds

English: `python3 scripts/story-catalog/seed.py` reads `docs/story/*.md` (skips `archive/`) and writes `seed.sql`.

Reading Locales: translators write `docs/story/i18n/{locale}/{slug}.md` (skill `issue-catalog-translation`). Then:

```
python3 scripts/story-catalog/seed_i18n.py --check
python3 scripts/story-catalog/seed_i18n.py --locale es
```

`--check` lists missing units. `--locale` prints upsert SQL for that locale to stdout. Markdown is the source; do not commit generated SQL.

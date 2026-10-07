# Worker prompts

Parent fills `{slug}`, `{locale}`, `{locale_note}`, `{source}`, `{output}`. Absolute repo root: `/Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose`.

Subagents have no parent history. Each prompt is the whole brief. `subagent_type`: `generalPurpose`. Do not use `explore` or `best-of-n-runner`.

## Retell

```
Read and follow /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/.cursor/skills/issue-catalog-translation/SKILL.md
and /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/.cursor/skills/issue-catalog-translation/references/file-format.md.

Unit: retell slug {slug} into locale {locale}.
Locale register: {locale_note}
English source: /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/docs/story/{source}
Output (only write): /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/docs/story/i18n/{locale}/{slug}.md

The ## Localisation notes in the English source are law. Retell title, synopsis, and every Chapter body. Honour every notes bullet in this first pass. Self-check the notes against the file before you stop.

Return: output path, Chapter count, and whether every notes bullet landed. Do not translate another locale or slug. Do not run seed_i18n.py. Do not apply SQL.
```

## Edit

```
Read and follow /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/.cursor/skills/issue-catalog-translation/SKILL.md
(edit pass only).

Unit: edit /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/docs/story/i18n/{locale}/{slug}.md
against English /Users/daniel/AndroidStudioProjects/soundquiz/Dota2SoundQuizCompose/docs/story/{source}
and that file's ## Localisation notes.
Locale: {locale}. Register: {locale_note}

You did not write this translation. Fix the file in place. Prefer sentences a child would say in this language. Every notes bullet must land. Chapter count must match English. Headings stay ## Part N.

Return exactly:
PASS <path> <chapter_count>
or
FAIL <path> <chapter_count>
then one line per notes miss or format miss.

Do not retell from scratch unless the file is empty or the wrong slug/locale. Do not start another unit. Do not run seed_i18n.py.
```

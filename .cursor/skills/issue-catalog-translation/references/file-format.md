# i18n file format

One file per unit: `docs/story/i18n/{locale}/{slug}.md`. Locale folder and filename must match the blockquote.

```markdown
# {translated title}

> locale: es
> slug: lina-golden-rolling-pin

## Synopsis

{translated synopsis, one or two sentences}

## Part 1 — {optional translated heading}

{Chapter body}

Paragraph two of the same Chapter.

## Part 2 — {optional translated heading}

{next Chapter body}
```

- `#` title and `## Synopsis` are catalog copy (`story_issue_i18n`).
- Each `## Part N` body is one Chapter (`story_chapter_i18n.body`). The heading keyword is the English word `Part`, then the number. The seeder drops the heading line, including any text after `—`.
- Bodies keep blank lines between paragraphs. No `## Part index`. No localisation notes in this file (those stay on the English source).
- Do not wrap the file in a code fence.

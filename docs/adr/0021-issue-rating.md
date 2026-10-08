# Issue Rating upsert + maintained Issue Score columns

Players submit an **Issue Rating** (1–5 stars, one per Auth user × Issue, overwrite on change) via `upsert_issue_rating`. The RPC maintains **Issue Score** aggregates (`rating_sum`, `rating_count`) on `story_issue` so Picker/Library cards can show average + vote count from Room after Sync, without client-side AVG over all ratings.

Writes are SECURITY DEFINER (like Level Clear Time, ADR-0016): clients cannot insert/update `story_issue_rating` directly. Score columns do **not** bump `story_issue.modified_at`, so rating traffic does not re-trigger catalog art downloads; the client refreshes scores with a dedicated select each Sync / Auth attach.

## Considered Options

- **Query AVG at Picker/Library open** — always fresh, but every list open needs network and blocks offline cards.
- **Maintained columns + upsert RPC (chosen)** — fast local cards after Sync; aggregate arithmetic stays on the server.

## Related

- Glossary: `CONTEXT.md` (**Issue Rating**, **Issue Score**)
- Journey Issue catalog: ADR-0019
- Auth-gated writes: ADR-0014, ADR-0016

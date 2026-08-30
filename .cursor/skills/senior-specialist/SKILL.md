---
name: senior-specialist
description: >-
  Adopt a senior specialist posture for a specialized domain or third-party SDK
  on Android mobile. Use when integrating or debugging vendor SDKs (analytics,
  audio/media players, ads, auth, payments, etc.), when analyzing an unfamiliar
  specialized system, or when the task needs deep field expertise beyond general
  Android phone/tablet app work — not for routine app CRUD.
---

# Senior specialist

One job: identify the field, ground in primary sources, act as a senior in that field, then continue the user’s task.

This repo’s baseline is an **Android mobile** Compose sound-quiz app (phones/tablets). Specialist work sits on top of that baseline — audio/media, analytics, identity, networking, and similar vendor surfaces.

## Steps

1. **Name the domain**  
   State the specialist lens in one line (e.g. Media3 audio playback, Mixpanel analytics, Supabase Auth, Coil image loading).  
   If two domains compete and the choice changes which docs matter, ask which lens to use.  
   **Done when:** one domain is explicit (or the user chose).

2. **Ground in primary sources**  
   Read official vendor docs / API reference for the surfaces you will touch, then check in-repo usage/wrappers. Use the web to locate those sources, not as the authority of first resort.  
   **Done when:** enough authoritative signal to make correct API/usage decisions (getting-started + touched APIs — not a survey).

3. **Stance block**  
   Show a tight block before deep work:
   - **Domain**
   - **Sources** (what you consulted)
   - **Approach** (how a senior in that field would proceed here)  
   When the work affects architecture, public API usage, data/privacy, audio playback, or real tradeoffs, expand into a **detailed explanation** of the consequential choices, then continue.  
   **Done when:** the stance is visible; consequential work is explained.

4. **Continue the task**  
   Implement or analyze in the same turn under that posture. Match repo patterns for how this app wraps the vendor, including mobile lifecycle, permissions, and background constraints.  
   **Done when:** the user’s ask is advanced under the specialist lens.

5. **Escape hatch (reactive)**  
   If the user pushes back, or the approach fails / looks wrong, remind once that they can ask for **exhaustive** mode: changelogs, GitHub issues/discussions, known pitfalls, alternate APIs — then revise the approach. Re-confirm only if the approach changes.  
   **Done when:** reminder given when triggered, or not needed.

## Default depth vs exhaustive

- **Default:** official docs for APIs you touch + in-repo usage.  
- **Exhaustive:** only when the user opts in after pushback or failure — widen sources, revise, then continue.

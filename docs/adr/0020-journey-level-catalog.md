# Journey catalog is 150 levels; spikes are guests

Launch Hero Journey is a **150**-level catalog — one playable level per active Issue Chapter (ADR-0019). Difficulty is a **toolkit** that unlocks slowly (Hero count, Affix families, decoy density, timers). Inside the current toolkit, levels **rollercoaster** across those axes so a hard level is a **guest**, not a new floor. Peak toolkit around level **120**; **121–150** remix at that peak (variety, not a third climb). Dire Heroes stay off the row until portraits ship.

**Familiar** roster: the catalog spell set is smaller than a Hero’s Dota kit. Mid and hard Affixes (identity, inverted submit, timer/quake, Sudden Death, stacks) only appear on Heroes the player has already heard on an earlier Journey level (prefer two prior appearances). Easy Affix teach still includes at least one familiar Hero. Each Affix is taught **solo** before it is stacked.

**Why 150:** Chapter capacity is 150, so the level count is 150. The extra length (vs the earlier 120 plan) is spent on a longer no-Affix primer, later first Affix, and **familiar** boards — not on climbing difficulty through 150. Authoring: skill `journey-level-authoring`. Spine still ADR-0015 (no XP).

**Assist retune:** After Journey Assists (ADR-0022), the launch seed densifies earlier Affix teaches and legal stacks (still solo-teach-first, rollercoaster guests). Seed: `scripts/journey-catalog/`.

**Rejected:** first Affix in the opening teens; stacking Affixes before each is taught solo; mid/hard Affixes on brand-new Heroes; monotonic floor raises after each spike; using off-screen Dire as a hidden identity load before Dire portraits.

-- Phase 3 CUTOVER — run ONLY after Play shows 2.3.1 (versionCode 19) installable.
-- Store release skipped a 2.3.0 changelog row; the staging 2.3.0 text ships as 2.3.1.
-- Do NOT combine with a staged rollout %.

-- 7.41f Classic-visible mutations
UPDATE public.sounds
SET spell_name = 'Summon Razorback',
    modified_at = now()
WHERE spell_name = 'Call of the Wild Boar'
  AND caster_id = (SELECT id FROM public.casters WHERE name = 'Beastmaster');

UPDATE public.sounds
SET spell_name = 'Summon Raptors',
    modified_at = now()
WHERE spell_name = 'Call of the Wild Hawk'
  AND caster_id = (SELECT id FROM public.casters WHERE name = 'Beastmaster');

UPDATE public.sounds
SET active = false,
    modified_at = now()
WHERE spell_name IN ('Bad Juju', 'Recall', 'Sidekick')
  AND active = true;

-- Changelog 2.3.1 (staging 2.3.0 text). Do not insert a 2.3.0 row.
INSERT INTO public.changelog (version, log, modified_at)
SELECT
  '2.3.1',
  '["Hero Journey mode added","Stories added — unlock Issues and Chapters by clearing Journey levels","New sounds added ( total sound count: 529 )","Largo sounds added"]'::jsonb,
  now()
WHERE NOT EXISTS (
  SELECT 1 FROM public.changelog WHERE version = '2.3.1'
);

-- Force update to versionCode 19 (app/build.gradle.kts). Revert forced_version to 16 if aborting.
UPDATE public.config
SET data = jsonb_set(
  COALESCE(data, '{}'::jsonb),
  '{forced_version}',
  '19'::jsonb,
  true
)
WHERE id = 1;

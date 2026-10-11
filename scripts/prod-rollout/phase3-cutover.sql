-- Phase 3 CUTOVER — run ONLY after Play shows 2.3.0 (versionCode 17) installable.
-- Order with the runbook: publish APK 100% → confirm installable → this file → done.
-- Do NOT run during Google review. Do NOT combine with staged rollout %.

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

-- Changelog 2.3.0 (clients show every synced row — never insert before publish)
INSERT INTO public.changelog (version, log, modified_at)
SELECT
  '2.3.0',
  '["Hero Journey mode added","Stories added — unlock Issues and Chapters by clearing Journey levels","New sounds added ( total sound count: 529 )","Largo sounds added"]'::jsonb,
  now()
WHERE NOT EXISTS (
  SELECT 1 FROM public.changelog WHERE version = '2.3.0'
);

-- Force update to versionCode 17 (app/build.gradle.kts). Revert forced_version to 16 if aborting.
UPDATE public.config
SET data = jsonb_set(
  COALESCE(data, '{}'::jsonb),
  '{forced_version}',
  '17'::jsonb,
  true
)
WHERE id = 1;

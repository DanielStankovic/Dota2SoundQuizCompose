-- Phase 2 (additive) catalog seed for prod.
-- Affixes (by name), Hero Journey game_mode, Largo + Largo sounds + Shadow Hex.
-- Does NOT rename Beastmaster spells or deactivate Classic spells (see phase3-cutover.sql).

-- Affix catalog upsert by natural key
INSERT INTO public.affixes (affix, description, active, data, modified_at)
VALUES
  ('Among Heroes', 'The faces you see are real — but one more hero hides among them. Mark every correct sound: those of the heroes you see, and those of the one you don’t.', true, '{}'::jsonb, now()),
  ('Blurred Vision', 'The hero’s image is obscured. Only true experts can recognize them by sound alone.', true, '{"blur": 7}'::jsonb, now()),
  ('Blurred Vision 2', 'The hero’s image is obscured. Only true experts can recognize them by sound alone.', true, '{"blur": 12}'::jsonb, now()),
  ('Echo Limit', 'You can listen to only a limited number of sounds. Choose wisely — every replay counts down your limit.', true, '{}'::jsonb, now()),
  ('Fragile Spirit', 'You begin the battle with one life less than usual. The margin for mistakes grows thin — tread carefully.', true, '{}'::jsonb, now()),
  ('Hidden Marks', 'The selected sound counter is hidden. So you must rely on memory and intuition.', true, '{}'::jsonb, now()),
  ('Mirror Mode', 'The world is reversed — pick only the wrong sounds to survive this twisted challenge.', true, '{}'::jsonb, now()),
  ('Partial Veil', 'Some heroes stand revealed; others hide behind the veil. Unmask the unknown by sound alone.', true, '{}'::jsonb, now()),
  ('Race Against Time', 'A timer is ticking! Complete the challenge before it runs out — or face defeat.', true, '{"timer": 120}'::jsonb, now()),
  ('Soundquake', 'Every few seconds the sound board quakes and reshuffles. Your marks stay on the sounds that moved — keep your bearings.', true, '{}'::jsonb, now()),
  ('Soundquake Aftershock', 'Every few seconds the board quakes harder: all marks are wiped, then the sounds reshuffle. Rebuild your selections from scratch.', true, '{}'::jsonb, now()),
  ('Soundquake Cataclysm', 'Every few seconds the board quakes: marks are wiped, sounds reshuffle, and you lose a heart. The harshest Soundquake.', true, '{}'::jsonb, now()),
  ('Soundquake Fracture', 'Every few seconds the board quakes and reshuffles while keeping your marks — but each quake also costs a heart.', true, '{}'::jsonb, now()),
  ('Sudden Death', 'Only one shot to prove your mastery. A single mistake ends your journey — instantly.', true, '{}'::jsonb, now()),
  ('The Hidden Hero', 'Every face is veiled. No portraits to guide you — trust only the sounds of the unseen heroes.', true, '{}'::jsonb, now()),
  ('Unknown Count', 'The true number of correct sounds is hidden. Only instinct can guide your choices now.', true, '{}'::jsonb, now())
ON CONFLICT (affix) DO UPDATE SET
  description = EXCLUDED.description,
  active = EXCLUDED.active,
  data = EXCLUDED.data,
  modified_at = EXCLUDED.modified_at;

-- Hero Journey game mode (additive row)
INSERT INTO public.game_mode (id, created_at, modified_at, mode, code, active)
SELECT 7, now(), now(), 'Hero Journey', '07', true
WHERE NOT EXISTS (
  SELECT 1 FROM public.game_mode WHERE code = '07' OR mode = 'Hero Journey'
);

-- Largo caster (do not copy staging id; use next free id / identity)
INSERT INTO public.casters (id, name, caster_type_id, active, modified_at)
SELECT 127, 'Largo', 1, true, now()
WHERE NOT EXISTS (SELECT 1 FROM public.casters WHERE name = 'Largo');

-- Largo sounds (is_local; APK assets already map spell names)
INSERT INTO public.sounds (
  id, spell_name, sound_file_name, sound_file_link, caster_id, active, is_local, modified_at
)
SELECT v.id, v.spell_name, v.sound_file_name, '', c.id, true, true, now()
FROM (
  VALUES
    (686::bigint, 'Amphibian Rhapsody', 'amphibian_rhapsody.mp3'),
    (687, 'Bullbelly Blitz', 'bullbelly_blitz.mp3'),
    (688, 'Catchy Lick', 'catchy_lick.mp3'),
    (689, 'Croak of Genius', 'croak_of_genius.mp3'),
    (690, 'Frogstomp', 'frogstomp.mp3')
) AS v(id, spell_name, sound_file_name)
JOIN public.casters c ON c.name = 'Largo'
WHERE NOT EXISTS (
  SELECT 1 FROM public.sounds s WHERE s.spell_name = v.spell_name AND s.caster_id = c.id
);

-- Shadow Hex (additive; Shadow Shaman had no Hex on prod)
INSERT INTO public.sounds (
  id, spell_name, sound_file_name, sound_file_link, caster_id, active, is_local, modified_at
)
SELECT 691, 'Shadow Hex', 'shadow_shaman_hex.mp3', '', c.id, true, true, now()
FROM public.casters c
WHERE c.name = 'Shadow_Shaman'
  AND NOT EXISTS (
    SELECT 1 FROM public.sounds s
    WHERE s.spell_name = 'Shadow Hex' AND s.caster_id = c.id
  );

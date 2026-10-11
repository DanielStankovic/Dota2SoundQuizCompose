-- Create the public story_art bucket on prod.
-- Run this in the prod SQL editor before copy_story_art_prod.py.
-- This does not copy image bytes. Storage files are not rows.

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types, avif_autodetection)
VALUES (
    'story_art',
    'story_art',
    true,
    5242880,
    ARRAY['image/png', 'image/jpeg'],
    false
)
ON CONFLICT (id) DO UPDATE
SET public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

DROP POLICY IF EXISTS "Public read story_art" ON storage.objects;
CREATE POLICY "Public read story_art"
ON storage.objects
FOR SELECT
TO public
USING (bucket_id = 'story_art');

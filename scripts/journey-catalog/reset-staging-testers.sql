-- Staging only. Do not run on prod.
-- After replacing the Journey catalog, testers must not keep a watermark
-- or clear-time on deleted prefix rows.

BEGIN;
UPDATE game_data SET journey_level = 0;
DELETE FROM level_clear_time;
DELETE FROM story_progress;
COMMIT;

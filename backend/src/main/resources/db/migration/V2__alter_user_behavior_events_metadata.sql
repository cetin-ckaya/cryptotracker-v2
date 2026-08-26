ALTER TABLE user_behavior_events ALTER COLUMN metadata TYPE text USING metadata::text;

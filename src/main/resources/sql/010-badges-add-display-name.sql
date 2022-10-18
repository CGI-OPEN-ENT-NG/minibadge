ALTER TABLE minibadge.badge_assigned
    ADD COLUMN display_name character varying(255) NOT NULL;

ALTER TABLE minibadge.badge
    ADD COLUMN display_name character varying(255) NOT NULL;


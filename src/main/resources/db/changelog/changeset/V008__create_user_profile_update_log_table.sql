CREATE TABLE IF NOT EXISTS notification_schema.user_profile_update_log (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES notification_schema.user_information(user_id) ON DELETE CASCADE
);

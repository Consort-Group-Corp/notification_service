CREATE TABLE IF NOT EXISTS notification_schema.notification_log (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    notification_status VARCHAR(50),
    communication VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES notification_schema.user_information(user_id) ON DELETE CASCADE
);

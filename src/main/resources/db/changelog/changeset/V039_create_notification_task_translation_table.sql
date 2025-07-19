CREATE TABLE IF NOT EXISTS notification_schema.notification_task_translation (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES notification_schema.notification_task(id) ON DELETE CASCADE,
    language VARCHAR(30) NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL
);

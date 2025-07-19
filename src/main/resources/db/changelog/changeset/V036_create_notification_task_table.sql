CREATE TABLE IF NOT EXISTS notification_schema.notification_task(
  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
  created_by_user_id UUID NOT NULL,
  creator_role VARCHAR(50) NOT NULL,
  communication VARCHAR(50) NOT NULL,
  send_at TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  is_active BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS notification_schema.user_information(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    language VARCHAR(5),
    last_name VARCHAR(120),
    first_name VARCHAR(120),
    middle_name VARCHAR(120),
    born_date DATE,
    phone_number VARCHAR(13),
    email VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version INTEGER DEFAULT 0
);
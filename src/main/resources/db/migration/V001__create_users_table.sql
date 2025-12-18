CREATE TABLE users (
    id BIGSERIAL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255) UNIQUE,
    token_expiry TIMESTAMP WITH TIME ZONE,
    password_reset_token VARCHAR(255) UNIQUE,
    password_reset_token_expiry TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

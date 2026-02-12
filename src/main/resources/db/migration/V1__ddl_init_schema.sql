CREATE SCHEMA IF NOT EXISTS tabby;

CREATE EXTENSION citext;
CREATE EXTENSION pgcrypto;

CREATE TABLE tabby.user (
    id uuid PRIMARY KEY,
    username varchar(20) NOT NULL UNIQUE,
    email citext NOT NULL UNIQUE,
    first_name text NOT NULL,
    last_name text NOT NULL
);

CREATE TABLE tabby.group (
    id uuid PRIMARY KEY,
    name varchar(20) NOT NULL,
    notes text
);

CREATE TABLE tabby.group_user (
    group_id uuid NOT NULL REFERENCES tabby.group(id),
    user_id uuid NOT NULL REFERENCES tabby.user(id),
    admin boolean NOT NULL,
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE tabby.category (
    id uuid PRIMARY KEY,
    name varchar(20) NOT NULL,
    subcategory varchar(20) NOT NULL
);

CREATE TABLE tabby.transaction (
    id uuid PRIMARY KEY,
    paid_by uuid NOT NULL REFERENCES tabby.user(id),
    cost numeric(10, 2) NOT NULL,
    description text NOT NULL,
    group_id uuid REFERENCES tabby.group(id),
    category_id uuid REFERENCES tabby.category(id),
    timestamp timestamptz NOT NULL,
    notes text
);

CREATE TABLE tabby.split (
    id uuid PRIMARY KEY,
    owed_by uuid NOT NULL REFERENCES tabby.user(id),
    transaction_id uuid NOT NULL REFERENCES tabby.transaction(id),
    amount numeric(10, 2) NOT NULL,
    settled boolean NOT NULL
);

CREATE TABLE tabby.payment (
    id uuid PRIMARY KEY,
    split_id uuid NOT NULL REFERENCES tabby.split(id),
    amount numeric(10, 2) NOT NULL,
    timestamp timestamptz NOT NULL
);

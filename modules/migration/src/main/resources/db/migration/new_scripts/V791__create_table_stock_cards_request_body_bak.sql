CREATE TABLE IF NOT EXISTS public.stock_cards_request_body_bak (
    id BIGSERIAL NOT NULL,
    facilityid INT4 NOT NULL,
    userId INT8,
    deviceInfo VARCHAR(150),
	versionCode VARCHAR(10),
	errorCode TEXT,
	request_body Text NOT NULL,
    createddate TIMESTAMPTZ NULL DEFAULT NOW(),
    CONSTRAINT stock_cards_request_body_bak_pkey PRIMARY KEY (id)
);
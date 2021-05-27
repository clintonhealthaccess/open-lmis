CREATE TABLE IF NOT EXISTS public.requisition_request_body_bak (
    id BIGSERIAL NOT NULL,
    facilityid INT4 NOT NULL,
	programCode varchar(30) NOT NULL,
	actualPeriodStartDate varchar(20) NOT NULL,
    userId INT8,
	errorInfo TEXT,
	request_body Text NOT NULL,
    createddate TIMESTAMPTZ NULL DEFAULT NOW(),
    CONSTRAINT requisition_request_body_bak_pkey PRIMARY KEY (id)
);
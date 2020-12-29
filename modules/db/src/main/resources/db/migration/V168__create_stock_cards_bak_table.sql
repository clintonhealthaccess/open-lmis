DROP TABLE IF EXISTS public.stock_cards_bak;
CREATE TABLE public.stock_cards_bak (
                                      id BIGSERIAL NOT NULL,
                                      facilityid INT4 NOT NULL,
                                      productid INT4 NOT NULL,
                                      fullydelete BOOLEAN NOT NULL,
                                      servermovements TEXT NULL,
                                      clientmovements TEXT NOT NULL,
                                      createdby INT4 NULL,
                                      createddate TIMESTAMPTZ NULL DEFAULT NOW(),
                                      CONSTRAINT stock_cards_bak_pkey PRIMARY KEY (id)
);

CREATE INDEX stock_cards_bak_facility_product_key ON public.stock_cards_bak USING btree (facilityid, productid);
CREATE INDEX stock_cards_bak_createddate ON public.stock_cards_bak USING btree (createddate);

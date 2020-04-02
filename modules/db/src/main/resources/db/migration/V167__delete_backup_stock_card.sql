CREATE OR REPLACE
            FUNCTION public.delete_stock_card(deleteFacilityId BIGINT,
                                              deleteProductId BIGINT) RETURNS VOID LANGUAGE plpgsql AS $function$
            DECLARE
BEGIN
DELETE
FROM
  cmm_entries
WHERE
    stockcardid IN (
    SELECT
      id
    FROM
      stock_cards
    WHERE
        stock_cards.facilityid = deleteFacilityId
      AND stock_cards.productid = deleteProductId);

DELETE
FROM
  stock_card_entry_key_values
WHERE
    stockcardentryid IN (
    SELECT
      id
    FROM
      stock_card_entries
    WHERE
        stockcardid IN (
        SELECT
          id
        FROM
          stock_cards
        WHERE
            stock_cards.facilityid = deleteFacilityId
          AND stock_cards.productid = deleteProductId));

DELETE
FROM
  stock_card_entry_lot_items_key_values
WHERE
    stockcardentrylotitemid IN (
    SELECT
      id
    FROM
      stock_card_entry_lot_items
    WHERE
        stockcardentryid IN (
        SELECT
          id
        FROM
          stock_card_entries
        WHERE
            stockcardid IN (
            SELECT
              id
            FROM
              stock_cards
            WHERE
                stock_cards.facilityid = deleteFacilityId
              AND stock_cards.productid = deleteProductId)));

DELETE
FROM
  stock_card_entry_lot_items
WHERE
    stockcardentryid IN (
    SELECT
      id
    FROM
      stock_card_entries
    WHERE
        stockcardid IN (
        SELECT
          id
        FROM
          stock_cards
        WHERE
            stock_cards.facilityid = deleteFacilityId
          AND stock_cards.productid = deleteProductId));

DELETE
FROM
  lots_on_hand
WHERE
    stockcardid IN (
    SELECT
      id
    FROM
      stock_cards
    WHERE
        stock_cards.facilityid = deleteFacilityId
      AND stock_cards.productid = deleteProductId);

DELETE
FROM
  stock_card_entries
WHERE
    stockcardid IN (
    SELECT
      id
    FROM
      stock_cards
    WHERE
        stock_cards.facilityid = deleteFacilityId
      AND stock_cards.productid = deleteProductId);

DELETE
FROM
  stock_cards
WHERE
    stock_cards.facilityid = deleteFacilityId
  AND stock_cards.productid = deleteProductId;

END $function$ ;

CREATE TABLE public.stock_cards_bak (
                                      id BIGSERIAL NOT NULL,
                                      facilityid INT4 NOT NULL,
                                      productid INT4 NOT NULL,
                                      servermovements TEXT NULL,
                                      clientmovements TEXT NOT NULL,
                                      createdby INT4 NULL,
                                      createddate TIMESTAMPTZ NULL DEFAULT NOW(),
                                      CONSTRAINT stock_cards_bak_pkey PRIMARY KEY (id)
);

CREATE INDEX stock_cards_bak_facility_product_key ON public.stock_cards_bak USING btree (facilityid, productid);
CREATE INDEX stock_cards_bak_createddate ON public.stock_cards_bak USING btree (createddate);

CREATE TABLE public.stock_cards_lock (
                                       facilityid INT4 NOT NULL,
                                       productid INT4 NOT NULL,
                                       actiontype VARCHAR(50) NOT NULL,
                                       createddate TIMESTAMPTZ NULL DEFAULT NOW(),
                                       CONSTRAINT stock_cards_lock_key UNIQUE (facilityid, productid)
);

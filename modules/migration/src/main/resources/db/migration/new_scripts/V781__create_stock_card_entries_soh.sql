DROP TABLE IF EXISTS stock_card_entries_soh;

CREATE TABLE IF NOT EXISTS stock_card_entries_soh (
stockcardid int4 NOT NULL,
stockcardentryid int4 NOT null,
quantity int4 NOT NULL DEFAULT 0,
occurred date NULL,
keycolumn text NOT NULL,
valuecolumn text NULL,
createddate timestamptz not null
);

CREATE INDEX idx_stockcardid ON stock_card_entries_soh USING btree (stockcardid);
CREATE INDEX idx_valuecolumn ON stock_card_entries_soh USING btree (valuecolumn);
CREATE INDEX idx_occurred ON stock_card_entries_soh USING btree (occurred);


CREATE OR REPLACE FUNCTION refresh_stock_card_entries_soh()
  RETURNS INT
AS $$
DECLARE
  lastStockCardEntryId INT;
BEGIN
  SELECT stockcardentryid
  FROM stock_card_entries_soh
  ORDER BY stock_card_entries_soh.stockcardentryid ASC
  LIMIT 1
  INTO lastStockCardEntryId;

  IF lastStockCardEntryId IS NULL
  THEN
    INSERT INTO stock_card_entries_soh(
    SELECT stockcardid, stockcardentryid, quantity, occurred, keycolumn, valuecolumn, stock_card_entries.createddate
    FROM stock_card_entries JOIN stock_card_entry_key_values ON stock_card_entries.id= stock_card_entry_key_values.stockcardentryid
    WHERE keycolumn = 'soh');
  ELSE
    INSERT INTO stock_card_entries_soh(
    SELECT stockcardid, stockcardentryid, quantity, occurred, keycolumn, valuecolumn, stock_card_entries.createddate
    FROM stock_card_entries JOIN stock_card_entry_key_values ON stock_card_entries.id= stock_card_entry_key_values.stockcardentryid
    WHERE stock_card_entries.id > lastStockCardEntryId AND keycolumn = 'soh');
  END IF;

  VACUUM stock_card_entries_soh;

  RETURN 1;
END
$$
LANGUAGE 'plpgsql';
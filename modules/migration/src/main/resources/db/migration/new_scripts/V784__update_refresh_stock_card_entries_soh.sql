CREATE OR REPLACE FUNCTION refresh_stock_card_entries_soh()
  RETURNS INT
AS $$
DECLARE
  lastStockCardEntryId INT;
BEGIN
  SELECT stockcardentryid
  FROM stock_card_entries_soh
  ORDER BY stock_card_entries_soh.stockcardentryid DESC
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

  RETURN 1;
END
$$
LANGUAGE 'plpgsql';
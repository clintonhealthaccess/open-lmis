CREATE OR REPLACE FUNCTION soh_of_day(cardid INTEGER, day DATE)
  RETURNS TEXT AS $BODY$
DECLARE
  soh TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entries_soh
  WHERE occurred <= day AND stockcardid = cardid
  ORDER BY stockcardentryid DESC
  LIMIT 1
  INTO soh;

  RETURN soh;
END
$BODY$
LANGUAGE 'plpgsql';
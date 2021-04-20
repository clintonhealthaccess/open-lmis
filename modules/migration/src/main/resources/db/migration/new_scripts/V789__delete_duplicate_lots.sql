BEGIN;

CREATE OR REPLACE FUNCTION resolve_duplicate_lot(errorlotId integer, correctLotId integer)
  RETURNS VOID AS $BODY$
DECLARE
BEGIN
update lots_on_hand set lotid = correctLotId where lotid = errorlotId;
update stock_card_entry_lot_items set lotid = correctLotId where lotid = errorlotId;
delete from lots where id = errorlotId;
END
$BODY$
LANGUAGE 'plpgsql';

SELECT resolve_duplicate_lot(1411424, 1411427);

COMMIT;
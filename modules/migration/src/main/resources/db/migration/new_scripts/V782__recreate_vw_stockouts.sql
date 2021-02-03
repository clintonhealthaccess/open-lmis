CREATE OR REPLACE FUNCTION calculate_each_month_duration(stockcard_id      INTEGER, occured_date DATE,
                                                         stockout_entry_id INTEGER)
  RETURNS SETOF MONTH_OVERLAP_OF_STOCKOUT AS
$event$
DECLARE
  firstDayOfMonth  DATE;
  lastDayOfMonthy  DATE;
  overlap_duration INT;
  resolvedDate     DATE;
  event            MONTH_OVERLAP_OF_STOCKOUT;
  months           INT;
  is_resolved      BOOLEAN;
BEGIN

  SELECT occurred
  FROM stock_card_entries_soh
  WHERE stock_card_entries_soh.stockcardid = stockcard_id AND valuecolumn != '0' AND stock_card_entries_soh.stockcardentryid > stockout_entry_id
  ORDER BY stock_card_entries_soh.stockcardentryid ASC
  LIMIT 1
  INTO resolvedDate;

  IF resolvedDate IS NULL
  THEN
    resolvedDate=current_date;
    is_resolved=FALSE;
  ELSE
    is_resolved=TRUE;
  END IF;

  SELECT months_between(occured_date, resolvedDate)
  INTO months;

  FOR m IN 0..months
  LOOP
    firstDayOfMonth := date_trunc('month', occured_date + (m - 1 || ' month') :: INTERVAL) + INTERVAL '20 days';
    lastDayOfMonthy := firstDayOfMonth + INTERVAL '1 month' - INTERVAL '1 day';

    overlap_duration := calculate_one_month_overlap(occured_date, resolvedDate, firstDayOfMonth, lastDayOfMonthy);

    RETURN NEXT (resolvedDate, firstDayOfMonth, overlap_duration, is_resolved);
  END LOOP;
END;
$event$ LANGUAGE plpgsql;

DROP MATERIALIZED VIEW IF EXISTS vw_stockouts;

CREATE MATERIALIZED VIEW vw_stockouts AS
  SELECT uuid_generate_v4() AS uuid,
    facilities.name AS facility_name,
    facilities.code AS facility_code,
    zone.name AS district_name,
    zone.code AS district_code,
    parent_zone.name AS province_name,
    parent_zone.code AS province_code,
    products.code AS drug_code,
    products.primaryname AS drug_name,
    programs.name AS program,
    sces.occurred AS stockout_date,
    (calculate_each_month_duration(stock_cards.id, sces.occurred, sces.stockcardentryid)).resolved_date AS resolved_date,
    (calculate_each_month_duration(stock_cards.id, sces.occurred, sces.stockcardentryid)).overlapped_month AS overlapped_month,
    (calculate_each_month_duration(stock_cards.id, sces.occurred, sces.stockcardentryid)).overlap_duration AS overlap_duration,
    (calculate_each_month_duration(stock_cards.id, sces.occurred, sces.stockcardentryid)).is_resolved AS is_resolved
   FROM facilities
     JOIN geographic_zones zone ON facilities.geographiczoneid = zone.id
     JOIN geographic_zones parent_zone ON zone.parentid = parent_zone.id
     JOIN stock_cards ON facilities.id = stock_cards.facilityid
     JOIN products ON stock_cards.productid = products.id
     JOIN program_products ON products.id = program_products.productid
     JOIN programs ON program_products.programid = programs.id
     JOIN stock_card_entries_soh sces ON stock_cards.id = sces.stockcardid
  WHERE sces.valuecolumn = '0'::text AND sces.quantity <> 0
  ORDER BY facilities.code, products.code, sces.occurred, (calculate_each_month_duration(stock_cards.id, sces.occurred, sces.stockcardentryid)).overlapped_month, sces.stockcardentryid
WITH NO DATA;

CREATE UNIQUE INDEX idx_vw_stockouts ON vw_stockouts (uuid);


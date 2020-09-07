CREATE MATERIALIZED VIEW vw_period_movements_before_20200521
TABLESPACE pg_default
AS SELECT uuid_in(md5(random()::text || now()::text)::cstring) AS uuid,
    cardidsinperiods.periodstart,
    cardidsinperiods.periodend,
    facilities.name AS facility_name,
    products.primaryname AS drug_name,
    facilities.code AS facility_code,
    products.code AS drug_code,
    parent_zone.name AS province_name,
    parent_zone.code AS province_code,
    gzone.name AS district_name,
    gzone.code AS district_code,
    soh_of_day(cardidsinperiods.stockcardid, cardidsinperiods.periodend::date)::integer AS soh,
    cmm_of(cardidsinperiods.stockcardid, cardidsinperiods.periodstart, cardidsinperiods.periodend) AS cmm,
    (total_quantity_and_occurrences(cardidsinperiods.stockcardid, cardidsinperiods.periodstart, cardidsinperiods.periodend)).*
   FROM ( SELECT processing_periods.startdate AS periodstart,
            processing_periods.enddate AS periodend,
            existing_card_ids_in_period(processing_periods.enddate) AS stockcardid
           FROM processing_periods
          WHERE processing_periods.startdate < '2020-05-21 00:00:00'::timestamp without time zone) cardidsinperiods
     JOIN stock_cards ON cardidsinperiods.stockcardid = stock_cards.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN products ON stock_cards.productid = products.id
     JOIN geographic_zones gzone ON facilities.geographiczoneid = gzone.id
     JOIN geographic_zones parent_zone ON gzone.parentid = parent_zone.id
WITH NO DATA;

-- View indexes:
CREATE UNIQUE INDEX idx_vw_period_movements_before_20200521 ON vw_period_movements_before_20200521 USING btree (uuid, periodstart, periodend, facility_code);


-- Create refresh functions
CREATE OR REPLACE FUNCTION refresh_period_movements_before_20200521()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_period_movements_before_20200521;
  RETURN 1;
END $$;
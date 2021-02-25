DROP MATERIALIZED VIEW IF EXISTS vw_period_soh_and_cmm_after_20200521;

CREATE materialized view vw_period_soh_and_cmm_after_20200521 AS
SELECT stock_cards.id AS stockcardid,
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
   cmm_of(cardidsinperiods.stockcardid, cardidsinperiods.periodstart, cardidsinperiods.periodend) AS cmm
FROM (SELECT processing_periods.startdate AS periodstart,
         processing_periods.enddate AS periodend,
         existing_card_ids_in_period(processing_periods.enddate) AS stockcardid
  FROM processing_periods
  WHERE processing_periods.startdate >= '2020-05-21 00:00:00'::timestamp without time zone) cardidsinperiods
     JOIN stock_cards ON cardidsinperiods.stockcardid = stock_cards.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN products ON stock_cards.productid = products.id
     JOIN geographic_zones gzone ON facilities.geographiczoneid = gzone.id
     JOIN geographic_zones parent_zone ON gzone.parentid = parent_zone.id
WITH NO DATA;

-- Create refresh functions
CREATE OR REPLACE FUNCTION refresh_period_soh_and_cmm_after_20200521()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_period_soh_and_cmm_after_20200521;
RETURN 1;
END $$;
CREATE OR REPLACE FUNCTION tracer_drugs_weekly_stock_history_after_2020_05_31()
  RETURNS SETOF stock_history AS
$BODY$
DECLARE
  first_movement_date       DATE;
  last_movement_date        DATE;
  one_friday                DATE;
  one_line                  stock_history;
  tracer_drug_stockcard_ids INTEGER ARRAY;
BEGIN

  tracer_drug_stockcard_ids=array(SELECT id AS stockcardid
                                  FROM stock_cards
                                  WHERE productid IN (SELECT id
                                                      FROM products
                                                      WHERE tracer = TRUE));


  SELECT occurred
  FROM stock_card_entries
  ORDER BY occurred DESC
  LIMIT 1
  INTO last_movement_date;

  FOR one_friday IN (SELECT *
                     FROM generate_series('2020-05-31' :: DATE, last_movement_date, '1 day')
                     WHERE EXTRACT(DOW FROM generate_series) = 5)
  LOOP
    FOR one_line IN (SELECT
                       facilities.name                             AS facility_name,
                       products.primaryname                        AS drug_name,
                       one_friday                                  AS date,
                       soh_of_day(entries.stockcardid, one_friday) AS soh,

                       facilities.code                             AS facility_code,
                       products.code                               AS drug_code,
                       parent_zone.name                            AS province_name,
                       parent_zone.code                            AS province_code,
                       ZONE.name                                   AS district_name,
                       ZONE.code                                   AS district_code

                     FROM (SELECT DISTINCT ON (stockcardid) stockcardid
                           FROM stock_card_entries
                           WHERE stockcardid = ANY (tracer_drug_stockcard_ids) AND occurred <= one_friday) entries
                       JOIN stock_cards ON entries.stockcardid = stock_cards.id
                       JOIN facilities ON stock_cards.facilityid = facilities.id
                       JOIN products ON stock_cards.productid = products.id
                       JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
                       JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id)
    LOOP
      RETURN NEXT one_line;
    END LOOP;
  END LOOP;
END
$BODY$
LANGUAGE 'plpgsql';

DROP MATERIALIZED VIEW IF EXISTS vw_weekly_tracer_soh_after_2020_05_31;

CREATE MATERIALIZED VIEW vw_weekly_tracer_soh_after_2020_05_31 AS (SELECT *
                                                  FROM tracer_drugs_weekly_stock_history_after_2020_05_31()) WITH NO DATA;

-- Permissions

ALTER TABLE public.vw_weekly_tracer_soh_after_2020_05_31 OWNER TO openlmis;
GRANT ALL ON TABLE public.vw_weekly_tracer_soh_after_2020_05_31 TO openlmis;
GRANT ALL ON TABLE public.vw_weekly_tracer_soh_after_2020_05_31 TO db_test;
GRANT SELECT ON TABLE public.vw_weekly_tracer_soh_after_2020_05_31 TO postgres;
GRANT SELECT ON TABLE public.vw_weekly_tracer_soh_after_2020_05_31 TO readonly;
GRANT SELECT ON TABLE public.vw_weekly_tracer_soh_after_2020_05_31 TO test1;

CREATE OR REPLACE FUNCTION refresh_weekly_tracer_soh_after_2020_05_31()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_weekly_tracer_soh_after_2020_05_31;
  RETURN 1;
END $$;
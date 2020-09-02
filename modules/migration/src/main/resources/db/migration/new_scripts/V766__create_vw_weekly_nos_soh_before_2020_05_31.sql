CREATE OR REPLACE FUNCTION nos_weekly_stock_history_before_2020_05_31()
  RETURNS SETOF nos_stock_history AS
$BODY$
DECLARE
  first_movement_date       DATE;
  last_movement_date        DATE;
  one_friday                DATE;
  one_line                  nos_stock_history;
  nos_stockcard_ids INTEGER ARRAY;
BEGIN

  nos_stockcard_ids=array(SELECT id AS stockcardid
                                  FROM stock_cards
                                  WHERE productid IN (SELECT id
                                                      FROM products
                                                      WHERE nos = TRUE));

  SELECT occurred
  FROM stock_card_entries
  ORDER BY occurred
  LIMIT 1
  INTO first_movement_date;

  FOR one_friday IN (SELECT *
                     FROM generate_series(first_movement_date :: DATE, '2020-05-31' :: DATE, '1 day')
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
                           ZONE.code                                   AS district_code,
                           (CASE WHEN programs_parent.code IS NULL THEN programs.name
                                ELSE programs_parent.code END)         AS area,
                           programs.name                               AS sub_area

                         FROM (SELECT DISTINCT ON (stockcardid) stockcardid
                               FROM stock_card_entries
                               WHERE stockcardid = ANY (nos_stockcard_ids) AND occurred <= one_friday) entries
                           JOIN stock_cards ON entries.stockcardid = stock_cards.id
                           JOIN facilities ON stock_cards.facilityid = facilities.id
                           JOIN products ON stock_cards.productid = products.id
                           JOIN program_products ON program_products.productid = products.id
                           JOIN programs ON programs.id = program_products.programid
                           LEFT JOIN programs AS programs_parent ON programs.parentid = programs_parent.id
                           JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
                           JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
                           AND program_products.active = TRUE)
    LOOP
      RETURN NEXT one_line;
    END LOOP;
  END LOOP;
END
$BODY$
LANGUAGE 'plpgsql';

DROP MATERIALIZED VIEW IF EXISTS vw_weekly_nos_soh_before_2020_05_31;

CREATE MATERIALIZED VIEW vw_weekly_nos_soh_before_2020_05_31 AS
(SELECT
 uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,
 *
 FROM nos_weekly_stock_history_before_2020_05_31()) WITH NO DATA;

-- View indexes:
CREATE UNIQUE INDEX idx_vw_weekly_nos_soh_before_2020_05_31 ON vw_weekly_nos_soh_before_2020_05_31 USING btree (uuid, facility_code, drug_code);


-- Permissions

GRANT ALL ON TABLE public.vw_weekly_nos_soh_before_2020_05_31 TO db_test;
GRANT SELECT ON TABLE public.vw_weekly_nos_soh_before_2020_05_31 TO postgres;


CREATE OR REPLACE FUNCTION refresh_weekly_nos_soh_before_2020_05_31()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_weekly_nos_soh_before_2020_05_31;
  RETURN 1;
END $$;

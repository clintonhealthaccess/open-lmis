DROP MATERIALIZED VIEW IF EXISTS vw_period_movements_after_20200521;

CREATE MATERIALIZED VIEW vw_period_movements_after_20200521
    TABLESPACE pg_default
AS SELECT uuid_in(md5(random()::text || now()::text)::cstring) AS uuid,
    stock_cards_soh_cmm.periodstart,
    stock_cards_soh_cmm.periodend,
    stock_cards_soh_cmm.facility_name,
    stock_cards_soh_cmm.drug_name,
    stock_cards_soh_cmm.drug_code,
    stock_cards_soh_cmm.facility_code,
    stock_cards_soh_cmm.province_name,
    stock_cards_soh_cmm.province_code,
    stock_cards_soh_cmm.district_name,
    stock_cards_soh_cmm.district_code,
    stock_cards_soh_cmm.soh,
    stock_cards_soh_cmm.cmm,
    (total_quantity_and_occurrences(stock_cards_soh_cmm.stockcardid, stock_cards_soh_cmm.periodstart::timestamp without time zone, stock_cards_soh_cmm.periodend::timestamp without time zone)).*
FROM vw_period_soh_and_cmm_after_20200521 stock_cards_soh_cmm
WITH NO DATA;

-- View indexes:
CREATE UNIQUE INDEX idx_vw_period_movements_after_20200521 ON vw_period_movements_after_20200521 USING btree (uuid, periodstart, periodend, facility_code);
DROP MATERIALIZED VIEW IF EXISTS vw_weekly_tracer_soh_after_2020_05_31;

CREATE MATERIALIZED VIEW vw_weekly_tracer_soh_after_2020_05_31 AS
(SELECT
 uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,
 *
 FROM tracer_drugs_weekly_stock_history_after_2020_05_31()) WITH NO DATA;

-- View indexes:
CREATE UNIQUE INDEX idx_vw_weekly_tracer_soh_after_2020_05_31 ON vw_weekly_tracer_soh_after_2020_05_31 USING btree (uuid, facility_code, drug_code);

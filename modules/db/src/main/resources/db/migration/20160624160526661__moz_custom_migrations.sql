CREATE TABLE moz_app_info(
  id  SERIAL PRIMARY KEY,
  facilityId INTEGER NOT NULL REFERENCES facilities (id),
  appVersion VARCHAR NOT NULL,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE moz_app_info ADD COLUMN userName VARCHAR;

CREATE TABLE kit_products_relation (
  kitCode VARCHAR(50) NOT NULL REFERENCES products(code),
  productCode VARCHAR(50) NOT NULL REFERENCES products(code),
  quantity INTEGER NOT NULL,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE products ADD COLUMN isKit BOOLEAN DEFAULT false;

DELETE FROM moz_app_info WHERE id IN
(SELECT a.id FROM moz_app_info a JOIN moz_app_info b on a.facilityid = b.facilityid WHERE a.id<b.id);

ALTER TABLE moz_app_info ADD CONSTRAINT moz_app_info_facilityId_key UNIQUE (facilityId);

ALTER TABLE programs ADD COLUMN parentId INTEGER REFERENCES programs(id);

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('EXPORTS','ADMIN','Permission to export Reports', 'right.export.report');

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_TABLET_INFO_REPORT','REPORT','Permission to View Tablet Information Reports', 'right.report.tablet.info');

UPDATE rights SET description = 'Permission to Export', displaynamekey = 'right.export' WHERE name = 'EXPORTS';

UPDATE configuration_settings SET value = '<h2><strong>Relatorio de MMIA anexado</strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos anexados, você encontrará:<ul><li>Um .pdf do MMIA</li><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do  SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados do MMIA para o SIMAM.</p>'
WHERE key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA';

UPDATE configuration_settings SET value = '<h2><strong>Dados de requisição Via Classica anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>Um .pdf da Requisição Via classica;</li><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisição Via Classica para o SIMAM.</p></div>'
WHERE key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS';

CREATE TYPE stock_out_event AS (duration INT, resolved_date DATE, is_resolved BOOLEAN);

CREATE OR REPLACE FUNCTION stockout_event(stockcard_id INTEGER, after_date DATE)
  RETURNS stock_out_event AS
$event$
DECLARE
  resolvedDate DATE;
  event        stock_out_event;
BEGIN

  SELECT occurred
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE stock_card_entries.stockcardid = stockcard_id AND
        keycolumn = 'soh' AND
        valuecolumn != '0' AND
        stock_card_entries.occurred > after_date
  ORDER BY occurred ASC
  LIMIT 1
  INTO resolvedDate;
  IF resolvedDate IS NULL
  THEN
    SELECT
      (current_date - after_date),
      resolvedDate,
      FALSE
    INTO event;
  ELSE
    SELECT
      resolvedDate - after_date,
      resolvedDate,
      TRUE
    INTO event;
  END IF;
  RETURN event;
END;
$event$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    zone.name                   AS district,
    parent_zone.name            AS province,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY facility_name, drug_name, stockout_date;

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_STOCKOUT_REPORT','REPORT','Permission to View stock out Reports', 'right.report.stockout');

DROP TYPE IF EXISTS stock_out_event CASCADE;
-- the line above will drop the type, and functions/views that use it

CREATE TYPE stock_out_event AS (duration INT, resolved_date DATE, is_resolved BOOLEAN);

CREATE OR REPLACE FUNCTION stockout_event(stockcard_id INTEGER, occured_date DATE, after_time TIMESTAMP WITH TIME ZONE)
  RETURNS stock_out_event AS
$event$
DECLARE
  resolvedDate DATE;
  event        stock_out_event;
BEGIN

  SELECT occurred
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE stock_card_entries.stockcardid = stockcard_id AND
        keycolumn = 'soh' AND valuecolumn != '0' AND
        stock_card_entry_key_values.createddate > after_time
  ORDER BY stock_card_entry_key_values.createddate ASC
  LIMIT 1
  INTO resolvedDate;

  IF resolvedDate IS NULL
  THEN
    SELECT
      (current_date - occured_date),
      resolvedDate,
      FALSE
    INTO event;
  ELSE
    SELECT
      resolvedDate - occured_date,
      resolvedDate,
      TRUE
    INTO event;
  END IF;
  RETURN event;
END;
$event$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    zone.name                   AS district,
    parent_zone.name            AS province,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.createddate)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY facility_name, drug_name, stockout_date;

CREATE TABLE sync_up_hashes(
  hash VARCHAR(128) PRIMARY KEY
);
-- we will use this table to store stock movement content hashes
-- so when the tablet tries to re-sync the same record twice due to http response IO interruption
-- we can reject the re-sync up

CREATE TABLE requisition_period(
  id SERIAL PRIMARY KEY,
  rnrId INT NOT NULL REFERENCES requisitions(id) UNIQUE,
  periodStartDate TIMESTAMP,
  periodEndDate TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

alter table "requisition_period" rename to requisition_periods;

CREATE TABLE archived_products(
  id SERIAL PRIMARY KEY,
  facilityId INT NOT NULL REFERENCES facilities(id) ON DELETE CASCADE,
  productCode VARCHAR(50) NOT NULL REFERENCES products(code) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    zone.name                   AS district,
    parent_zone.name            AS province,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.createddate)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY stockout_date;

  DROP VIEW IF EXISTS vw_stockouts;

CREATE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    facilities.code             AS facility_code,
    zone.name                   AS district_name,
    zone.code                   AS district_code,
    parent_zone.name            AS province_name,
    parent_zone.code            AS province_code,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (stockout_event(stock_cards.id, stock_card_entries.occurred, stock_card_entry_key_values.createddate)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY stockout_date;

DROP TYPE IF EXISTS stock_out_event CASCADE;
DROP TYPE IF EXISTS MONTH_OVERLAP_OF_STOCKOUT CASCADE;
-- lines above will drop the type, and functions/views that use it

CREATE TYPE MONTH_OVERLAP_OF_STOCKOUT AS (resolved_date DATE, overlapped_month DATE, overlap_duration INT, is_resolved BOOLEAN);

CREATE OR REPLACE FUNCTION months_of(INTERVAL)
  RETURNS INT AS $$
BEGIN
  RETURN extract(years FROM $1) :: INT * 12 + EXTRACT(MONTH FROM $1) :: INT;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION months_between(DATE, DATE)
  RETURNS INT AS $$
BEGIN
  RETURN abs(months_of(age(date_trunc('month', $1), date_trunc('month', $2))));
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION extract_days(DATERANGE)
  RETURNS INT AS
$$
BEGIN
  RETURN (date_trunc('day', UPPER($1)) :: DATE - date_trunc('day', LOWER($1)) :: DATE) + 1;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION last_day_of_month(DATE)
  RETURNS DATE AS
$$
BEGIN
  RETURN (to_char(($1 + INTERVAL '1 month'), 'YYYY-MM') || '-01') :: DATE - 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_one_month_overlap(occuredDate     DATE, resolvedDate DATE, firstDayOfMonth DATE,
                                                       lastDayOfMonthy DATE)
  RETURNS INT AS $$
DECLARE
  overlap_duration INT;
BEGIN

  IF occuredDate = resolvedDate
  THEN
    RETURN 0;
  END IF;

  overlap_duration := extract_days(
      DATERANGE(occuredDate, resolvedDate) * DATERANGE(firstDayOfMonth, lastDayOfMonthy));
  IF overlap_duration IS NULL
  THEN
    RETURN 1;
  ELSE
    RETURN overlap_duration;
  END IF;

END
$$ LANGUAGE plpgsql;

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
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE stock_card_entries.stockcardid = stockcard_id AND
        keycolumn = 'soh' AND valuecolumn != '0' AND
        stock_card_entry_key_values.stockcardentryid > stockout_entry_id
  ORDER BY stock_card_entry_key_values.stockcardentryid ASC
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

  FOR m IN 1..months + 1
  LOOP
    firstDayOfMonth := date_trunc('month', occured_date + (m - 1 || ' month') :: INTERVAL);
    lastDayOfMonthy:=last_day_of_month(firstDayOfMonth);

    overlap_duration := calculate_one_month_overlap(occured_date, resolvedDate, firstDayOfMonth, lastDayOfMonthy);

    RETURN NEXT (resolvedDate, firstDayOfMonth, overlap_duration, is_resolved);
  END LOOP;
END;
$event$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    facilities.code             AS facility_code,
    zone.name                   AS district_name,
    zone.code                   AS district_code,
    parent_zone.name            AS province_name,
    parent_zone.code            AS province_code,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred,
                                   stockcardentryid)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY facility_code, drug_code, stockout_date, overlapped_month, stockcardentryid;

CREATE OR REPLACE FUNCTION splitDates(datesStr TEXT)
  RETURNS SETOF DATE AS $$
DECLARE
  datesArray TEXT [];
  dateStr    TEXT;
BEGIN
  datesArray := regexp_split_to_array(datesStr, ',');
  FOREACH dateStr IN ARRAY datesArray
  LOOP
    RETURN NEXT to_date(dateStr, 'DD-MM-YYYY');
  END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW vw_expiry_dates AS
  SELECT
    DISTINCT ON (facility_code, drug_code, expiry_date)
    facilities.name         AS facility_name,
    facilities.code         AS facility_code,
    zone.name               AS district_name,
    zone.code               AS district_code,
    parent_zone.name        AS province_name,
    parent_zone.code        AS province_code,
    products.code           AS drug_code,
    products.primaryname    AS drug_name,
    splitDates(valuecolumn) AS expiry_date
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'expirationdates' AND valuecolumn != ''
  ORDER BY facility_code, drug_code;

  DROP VIEW IF EXISTS vw_expiry_dates;

CREATE OR REPLACE VIEW vw_expiry_dates AS
  SELECT
    facilities.name      AS facility_name,
    facilities.code      AS facility_code,
    zone.name            AS district_name,
    zone.code            AS district_code,
    parent_zone.name     AS province_name,
    parent_zone.code     AS province_code,
    products.code        AS drug_code,
    products.primaryname AS drug_name,
    valuecolumn          AS expiry_dates,
    (EXTRACT(EPOCH FROM occurred) * 1000) AS occurred
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'expirationdates'
  ORDER BY facility_code, drug_code, occurred, stock_card_entries.id DESC;

DROP VIEW IF EXISTS vw_stockouts;

CREATE MATERIALIZED VIEW vw_stockouts AS
  SELECT
    facilities.name             AS facility_name,
    facilities.code             AS facility_code,
    zone.name                   AS district_name,
    zone.code                   AS district_code,
    parent_zone.name            AS province_name,
    parent_zone.code            AS province_code,
    products.code               AS drug_code,
    products.primaryname        AS drug_name,
    programs.name               AS program,
    stock_card_entries.occurred AS stockout_date,
    (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred, stockcardentryid)).*
  FROM facilities
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN stock_cards ON facilities.id = stock_cards.facilityid
    JOIN products ON stock_cards.productid = products.id
    JOIN program_products ON products.id = program_products.productid
    JOIN programs ON program_products.programid = programs.id
    JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
    JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
  WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
  ORDER BY facility_code, drug_code, stockout_date, overlapped_month, stockcardentryid;

CREATE OR REPLACE FUNCTION refresh_stockouts()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_stockouts;
  RETURN 1;
END $$;

ALTER TABLE regimens ADD COLUMN isCustom BOOLEAN DEFAULT false;

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_EXPIRY_DATES_REPORT','REPORT','Permission to View expiry dates Reports', 'right.report.expiry.dates');

DROP VIEW IF EXISTS vw_regimen_district_distribution;
DROP VIEW IF EXISTS vw_regimen_summary;

-- the following line is the only line that matters, the other lines are copied from old migration
-- to delete and recreate views that depend on regimens
ALTER TABLE regimens ALTER COLUMN name TYPE VARCHAR;

CREATE OR REPLACE VIEW vw_regimen_summary AS
  SELECT
    r.programid,
    ps.id         AS scheduleid,
    pp.id         AS periodid,
    li.regimencategory,
    regimens.categoryid,
    regimens.id   AS regimenid,
    regimens.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.status,
    d.district_id AS geographiczoneid,
    d.district_name  district
  FROM regimen_line_items li
    JOIN requisitions r ON li.rnrid = r.id
    JOIN processing_periods pp ON r.periodid = pp.id
    JOIN processing_schedules ps ON ps.id = pp.scheduleid
    JOIN programs_supported pps ON r.programid = pps.programid AND r.facilityid = pps.facilityid
    JOIN regimens ON li.code :: TEXT = regimens.code :: TEXT
    JOIN facilities ON r.facilityid = facilities.id
    JOIN vw_districts d ON facilities.geographiczoneid = d.district_id;


CREATE OR REPLACE VIEW vw_regimen_district_distribution AS
  SELECT
    r.programid,
    rgps.scheduleid,
    pp.id           AS periodid,
    regimens.categoryid,
    regimens.id     AS regimenid,
    li.name         AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.facilityid,
    r.status,
    f.name          AS facilityname,
    f.code          AS facilitycode,
    f.typeid        AS facilitytypeid,
    ft.name            facilitytype,
    d.district_name AS district,
    d.district_id   AS districtid,
    d.region_id     AS regionid,
    d.region_name      region,
    d.zone_id       AS zoneid,
    d.zone_name     AS zone,
    d.parent
  FROM regimen_line_items li
    JOIN requisitions r ON li.rnrid = r.id
    JOIN facilities f ON r.facilityid = f.id
    JOIN facility_types ft ON f.typeid = ft.id
    JOIN vw_districts d ON f.geographiczoneid = d.district_id
    JOIN requisition_group_members rgm ON r.facilityid = rgm.facilityid
    JOIN programs_supported ps ON r.programid = ps.programid AND r.facilityid = ps.facilityid
    JOIN regimens ON li.code :: TEXT = regimens.code :: TEXT
    JOIN processing_periods pp ON r.periodid = pp.id
    JOIN requisition_group_program_schedules rgps
      ON rgm.requisitiongroupid = rgps.requisitiongroupid AND pp.scheduleid = rgps.scheduleid;

ALTER TABLE vw_regimen_district_distribution OWNER TO postgres;
ALTER TABLE vw_regimen_summary OWNER TO postgres;

CREATE OR REPLACE FUNCTION first_movement_date(cardid INTEGER)
  RETURNS DATE AS
$BODY$
BEGIN
  RETURN (SELECT occurred
          FROM stock_card_entries
          WHERE stockcardid = cardid
          ORDER BY occurred
          LIMIT 1);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_carry_start_dates AS
  SELECT
    facilities.name                     AS facility_name,
    facilities.code                     AS facility_code,
    ZONE.name                           AS district_name,
    ZONE.code                           AS district_code,
    parent_zone.name                    AS province_name,
    parent_zone.code                    AS province_code,
    products.code                       AS drug_code,
    products.primaryname                AS drug_name,
    facilities.golivedate               AS facility_golive_date,
    facilities.godowndate               AS facility_godown_date,
    first_movement_date(stock_cards.id) AS carry_start_date
  FROM stock_cards
    JOIN facilities ON stock_cards.facilityid = facilities.id
    JOIN products ON stock_cards.productid = products.id
    JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
    JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
  ORDER BY facility_code, carry_start_date;

CREATE OR REPLACE FUNCTION refresh_start_carry_view()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_carry_start_dates;
  RETURN 1;
END $$;

ALTER TABLE programs ADD COLUMN isSupportEmergency BOOLEAN DEFAULT false;

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_TRACER_DRUGS_REPORT','REPORT','Permission to View tracer drugs Reports', 'right.report.tracer.drugs.report');

CREATE TYPE stock_history AS ( facility_name TEXT, drug_name TEXT, date DATE, soh TEXT,
                               facility_code TEXT, drug_code TEXT, province_name TEXT, province_code TEXT, district_name TEXT, district_code TEXT);

CREATE OR REPLACE FUNCTION soh_of_day(cardid INTEGER, day DATE)
  RETURNS TEXT AS $BODY$
DECLARE
  soh TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE keycolumn = 'soh' AND occurred <= day AND stock_card_entries.stockcardid = cardid
  ORDER BY stockcardentryid DESC
  LIMIT 1
  INTO soh;

  RETURN soh;
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION tracer_drugs_weekly_stock_history()
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
  ORDER BY occurred
  LIMIT 1
  INTO first_movement_date;

  SELECT occurred
  FROM stock_card_entries
  ORDER BY occurred DESC
  LIMIT 1
  INTO last_movement_date;

  FOR one_friday IN (SELECT *
                     FROM generate_series(first_movement_date :: DATE, last_movement_date, '1 day')
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

CREATE MATERIALIZED VIEW vw_weekly_tracer_soh AS (SELECT *
                                                  FROM tracer_drugs_weekly_stock_history());

CREATE OR REPLACE FUNCTION refresh_weekly_tracer_soh()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW vw_weekly_tracer_soh;
  RETURN 1;
END $$;

CREATE OR REPLACE FUNCTION set_value(id INTEGER, key TEXT)
  RETURNS TEXT AS $BODY$
DECLARE
  value TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
  WHERE keycolumn = key AND stockcardentryid = id
  INTO value;

  RETURN value;
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.adjustmenttype AS reason,
    movement.type AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.createddate AS createddate,
    movement.quantity AS quantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirydates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
);

CREATE OR REPLACE FUNCTION set_value(id INTEGER, key TEXT)
  RETURNS TEXT AS $BODY$
DECLARE
  value TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
  WHERE keycolumn = key AND stockcardentryid = id
  INTO value;

  RETURN value;
END
$BODY$
LANGUAGE 'plpgsql';


DROP VIEW vw_stock_movements;

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.adjustmenttype AS reason,
    types.category AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.createddate AS createddate,
    movement.quantity AS quantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirationdates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
    JOIN losses_adjustments_types AS types ON types.name = movement.adjustmenttype
);

CREATE OR REPLACE FUNCTION set_value(id INTEGER, key TEXT)
  RETURNS TEXT AS $BODY$
DECLARE
  value TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
  WHERE keycolumn = key AND stockcardentryid = id
  INTO value;

  RETURN value;
END
$BODY$
LANGUAGE 'plpgsql';


DROP VIEW vw_stock_movements;

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.id AS id,
    movement.adjustmenttype AS reason,
    types.category AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.occurred AS movementdate,
    movement.quantity AS quantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirationdates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
    JOIN losses_adjustments_types AS types ON types.name = movement.adjustmenttype
);

CREATE OR REPLACE FUNCTION set_value(id INTEGER, key TEXT)
  RETURNS TEXT AS $BODY$
DECLARE
  value TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
  WHERE keycolumn = key AND stockcardentryid = id
  INTO value;

  RETURN value;
END
$BODY$
LANGUAGE 'plpgsql';


DROP VIEW vw_stock_movements;

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.id AS id,
    movement.adjustmenttype AS reason,
    types.category AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.occurred AS movementdate,
    movement.quantity AS quantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    f.code AS facilitycode,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirationdates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
    JOIN losses_adjustments_types AS types ON types.name = movement.adjustmenttype
);

DROP TABLE IF EXISTS cmm_entries;

CREATE TABLE  cmm_entries
(
  id SERIAL PRIMARY KEY,
  productCode VARCHAR(50) NOT NULL REFERENCES products(code),
  facilityId INTEGER NOT NULL REFERENCES facilities(id),
  periodBegin DATE NOT NULL,
  periodEnd DATE NOT NULL,
  cmmValue FLOAT,
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE stock_card_entries ADD COLUMN requestedQuantity INTEGER;

DROP VIEW vw_stock_movements;

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.id AS id,
    movement.adjustmenttype AS reason,
    types.category AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.occurred AS movementdate,
    movement.quantity AS quantity,
    movement.requestedquantity as requestedquantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    f.code AS facilitycode,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirationdates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
    JOIN losses_adjustments_types AS types ON types.name = movement.adjustmenttype
);

-- For each period, each drug in each facility will have N rows in this view
-- N is determined by how many different movement reasons were used
-- Each row should have information representing:
-- location(province and district), HF, Drug, Period(start, end), movement reason,
-- sum of movement quantify for this reason, times of movements for this reason, SOH, CMM

CREATE OR REPLACE FUNCTION existing_card_ids_in_period(periodEnd TIMESTAMP)
  RETURNS SETOF INTEGER AS $BODY$
BEGIN
  RETURN QUERY (SELECT DISTINCT stock_cards.id
                FROM stock_cards
                  JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
                WHERE occurred <= periodEnd);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION total_quantity_and_occurrences(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT
                  adjustmenttype,
                  count(adjustmenttype),
                  abs(sum(quantity))
                FROM stock_card_entries
                WHERE stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd
                GROUP BY adjustmenttype);
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION cmm_of(cardid INTEGER, periodStart TIMESTAMP, periodEnd_P TIMESTAMP)
  RETURNS DOUBLE PRECISION AS $$
DECLARE
  cmm DOUBLE PRECISION;
BEGIN
  cmm = (SELECT cmmvalue
         FROM cmm_entries
         WHERE facilityid = (SELECT facilities.id
                             FROM facilities
                               JOIN stock_cards ON facilities.id = stock_cards.facilityid
                             WHERE stock_cards.id = cardid)
               AND
               productcode = (SELECT code
                              FROM products
                              WHERE id = (SELECT productid
                                          FROM stock_cards
                                          WHERE id = cardid))
               AND cmm_entries.periodbegin = periodStart
               AND cmm_entries.periodend = periodEnd_P);

  IF (cmm IS NULL)
  THEN
    RETURN -1;
  ELSE
    RETURN cmm;
  END IF;

END
$$
LANGUAGE 'plpgsql';

CREATE MATERIALIZED VIEW vw_period_movements AS
  (SELECT
     periodStart,
     periodEnd,

     facilities.name                             AS facility_name,
     products.primaryname                        AS drug_name,
     facilities.code                             AS facility_code,
     products.code                               AS drug_code,
     parent_zone.name                            AS province_name,
     parent_zone.code                            AS province_code,
     ZONE.name                                   AS district_name,
     ZONE.code                                   AS district_code,

     soh_of_day(stockcardid, periodEnd :: DATE)  AS soh,
     cmm_of(stockcardid, periodStart, periodEnd) AS cmm,

     (total_quantity_and_occurrences(stockcardid, periodStart,
                                     periodEnd)).*

   FROM (SELECT
           startdate                            AS periodStart,
           enddate                              AS periodEnd,
           existing_card_ids_in_period(enddate) AS stockcardid
         FROM processing_periods) AS cardIdsInPeriods
     JOIN stock_cards ON cardIdsInPeriods.stockcardid = stock_cards.id
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN products ON stock_cards.productid = products.id
     JOIN geographic_zones AS ZONE
       ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone
       ON ZONE.parentid = parent_zone.id);


CREATE OR REPLACE FUNCTION refresh_period_movements()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_period_movements;
  RETURN 1;
END $$;

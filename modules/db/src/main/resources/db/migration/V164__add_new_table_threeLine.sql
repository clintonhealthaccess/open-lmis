DROP TABLE IF EXISTS three_lines;
CREATE TABLE therapeutic_line_items (
  id                          SERIAL PRIMARY KEY,
  rnrId                       INTEGER NOT NULL REFERENCES requisitions (id),
  code                        VARCHAR(250),
  patientsOnTreatment         INTEGER,
  comunitaryPharmacy          INTEGER NOT NULL,
  createdBy                   INTEGER,
  createdDate                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                  INTEGER,
  modifiedDate                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
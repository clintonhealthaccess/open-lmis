alter table moz_app_info add column uniqueId varchar(64);

CREATE TABLE facility_tablet_history(
  id             SERIAL PRIMARY KEY,
  facilityid INTEGER NOT NULL,
  username       varchar(64) NULL,
  androidversion varchar(64) NULL,
  deviceinfo     varchar(64) NULL,
  uniqueId varchar(64) NULL,
  createdby INTEGER NULL,
  createddate    timestamp NULL DEFAULT now(),
  modifiedby INTEGER NULL,
  modifieddate   timestamp not NULL DEFAULT now()
);

CREATE INDEX i_tablet_history_facilityid ON facility_tablet_history(facilityid);
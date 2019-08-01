ALTER TABLE regimen_line_items ADD COLUMN comunitaryPharmacy CHAR(255);
ALTER TABLE patient_quantification_line_items ADD COLUMN tableName CHAR(255);
INSERT INTO program_regimen_columns (programid,name,label,visible,datatype,modifiedby) VALUES (1,'comunitaryPharmacy','Comunitary pharmacy
',true,'regimen.reporting.dataType.numeric',1);
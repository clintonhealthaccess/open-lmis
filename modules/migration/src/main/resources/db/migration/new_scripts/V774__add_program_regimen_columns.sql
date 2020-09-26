DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
INSERT INTO public.program_regimen_columns (programid,name,"label",visible,"datatype",createdby,createddate,modifiedby,modifieddate,displayorder) VALUES
(1,'comunitaryPharmacy','Number of comunitary on pharmacy',true,'regimen.reporting.dataType.numeric',NULL,now() ,1,now() ,4);
 END IF;
END
$do$
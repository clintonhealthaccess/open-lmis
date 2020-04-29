DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO818','AZT 60+3TC 30+LPV/r (2DFC + LPV/r 40/10)',true,18,NULL,now(),NULL,now());
END IF;
END
$do$
UPDATE regimens SET displayorder = 1, active = TRUE, modifieddate = now() WHERE code = '002';
UPDATE regimens SET displayorder = 4, active = TRUE, modifieddate = now() WHERE code = '003';
UPDATE regimens SET displayorder = 8, active = TRUE, modifieddate = now() WHERE code = '006';
UPDATE regimens SET displayorder = 9, active = TRUE, modifieddate = now() WHERE code = '007';
UPDATE regimens SET displayorder = 10, active = TRUE, modifieddate = now() WHERE code = '008';

DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
-- adult
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,1,'TO82','TDF+3TC+DTG',true,2,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,1,'TO85','AZT+3TC+ATV/r',true,5,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,1,'TO86','TDF+3TC+ATV/r',true,6,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,1,'TO87','ABC+3TC+ATV/r',true,7,NULL,now(),NULL,now());





-- PAEDIATRICS
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO811','ABC+3TC+LPV/r (2DFC ped + LPV/r 100/25)',true,11,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO812','ABC+3TC+LPV/r (2DFC ped + LPV/r 80/20)',true,12,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO813','ABC+3TC+LPV/r (2DFC ped + LPV/r 40/10)',true,13,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO814','ABC+3TC+EFV (2DFC ped + EFV200)',true,14,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO815','AZT 60+3TC 30+NVP 50 (3DFC)',true,15,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO816','AZT 60+3TC 30+LPV/r (2DFC + LPV/r 100/25)',true,16,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO817','AZT 60+3TC 30+LPV/r (2DFC + LPV/r 80/20)',true,17,NULL,now(),NULL,now());
INSERT INTO public.regimens (programid,categoryid,code,"name",active,displayorder,createdby,createddate,modifiedby,modifieddate) VALUES (1,2,'TO818','AZT 60+3TC 30+LPV/r (2DFC + LPV/r 40/10)',true,18,NULL,now(),NULL,now());
END IF;
END
$do$
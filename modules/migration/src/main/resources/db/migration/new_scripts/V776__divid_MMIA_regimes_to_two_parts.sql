DO
$do$
    BEGIN

        DROP INDEX IF EXISTS i_regimens_code_programid;
        CREATE UNIQUE INDEX i_regimens_code_programId ON regimens(code, programId,name);

        -- Less Than V86
        UPDATE regimens SET versionCode=86 where versionCode is null;

    IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
            -- For Android V87 Adults
            INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom,skipped, createddate, modifieddate, versionCode)
            VALUES (1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults002'/*code*/, 'TDF+3TC+EFV'/*name*/, TRUE/*active*/, 1/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'AdultsTO082'/*code*/, 'TDF+3TC+DTG'/*name*/, TRUE/*active*/, 2/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults001'/*code*/, 'ABC+3TC+DTG'/*name*/, TRUE/*active*/, 3/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults003'/*code*/, 'AZT+3TC+DTG'/*name*/, TRUE/*active*/, 4/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'AdultsTO85'/*code*/, 'AZT+3TC+ATV/r'/*name*/, TRUE/*active*/, 5/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'AdultsTO86'/*code*/, 'TDF+3TC+ATV/r'/*name*/, TRUE/*active*/, 6/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'AdultsTO87'/*code*/, 'ABC+3TC+ATV/r'/*name*/, TRUE/*active*/, 7/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults006'/*code*/, 'AZT+3TC+LPV/r'/*name*/, TRUE/*active*/, 8/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults007'/*code*/, 'TDF+3TC+LPV/r'/*name*/, TRUE/*active*/, 9/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            ,(1 /*programid*/, 1/*1- adults, 2-Paediatrics*/, 'Adults008'/*code*/, 'ABC+3TC+LPV/r'/*name*/, TRUE/*active*/, 10/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87);
            -- For Android V87 Paediatrics
            INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom, skipped, createddate, modifieddate, versionCode)
            VALUES (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO811'/*code*/, 'ABC+3TC+LPV/r (2DFC ped + LPV/r 100/25)'/*name*/, TRUE/*active*/, 11/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO812'/*code*/, 'ABC+3TC+LPV/r (2DFC ped + LPV/r 80/20)'/*name*/, TRUE/*active*/, 12/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO813'/*code*/, 'ABC+3TC+LPV/r (2DFC ped + LPV/r 40/10)'/*name*/, TRUE/*active*/, 13/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO814'/*code*/, 'ABC+3TC+EFV (2DFC ped + EFV200)'/*name*/, TRUE/*active*/, 14/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO815'/*code*/, 'AZT 60+3TC 30+NVP 50 (3DFC)'/*name*/, TRUE/*active*/, 15/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO816'/*code*/, 'ABC+3TC+DTG (2DFC ped + DTG50)'/*name*/, TRUE/*active*/, 16/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO817'/*code*/, 'AZT 60+3TC 30+LPV/r (2DFC + LPV/r 100/25)'/*name*/, TRUE/*active*/, 17/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO818'/*code*/, 'AZT 60+3TC 30+LPV/r (2DFC + LPV/r 80/20)'/*name*/, TRUE/*active*/, 18/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87)
            , (1 /*programid*/, 2/*1- adults, 2-Paediatrics*/, 'PaediatricsTO819'/*code*/, 'AZT 60+3TC 30+LPV/r (2DFC + LPV/r 40/10)'/*name*/, TRUE/*active*/, 19/*displayorder*/, 1, 1, FALSE/*iscustom*/, FALSE/*skipped*/, NOW(), NOW(),87);
    END IF;
END
$do$
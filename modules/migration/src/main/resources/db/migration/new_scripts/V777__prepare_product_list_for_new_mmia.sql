
-- Adult
UPDATE products SET primaryname = 'TDF 300mg/3TC 300mg/DTG 50mg (TLD 90)', strength = '90 comp'  WHERE code = '08S18WI';
UPDATE products SET primaryname = 'TDF 300mg/3TC 300mg/DTG 50mg (TLD 30)', strength = '30 comp'  WHERE code = '08S18W';
UPDATE products SET primaryname = 'TDF 300mg/3TC 300mg/EFV 600/400mg', strength = '30 comp'  WHERE code = '08S18Y';
UPDATE products SET primaryname = 'Lamivudina 150mg/Zidovudina 300mg', strength = '60 comp'  WHERE code = '08S40';
UPDATE products SET primaryname = 'Tenofovir 300mg/Lamivudina 300mg', strength = '30 comp'  WHERE code = '08S18Z';
UPDATE products SET primaryname = 'Abacavir 600mg/Lamivudina 300mg', strength = '30 comp'  WHERE code = '08S01ZY';
UPDATE products SET primaryname = 'Atazanavir/Ritonavir 300mg/100mg', strength = '30 comp'  WHERE code = '08S30WZ';
UPDATE products SET primaryname = 'Dolutegravir (DTG) 50mg', strength = '30 comp'  WHERE code = '08S30ZY';
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 200mg/50mg', strength = '120 comp'  WHERE code = '08S18Z';
UPDATE products SET primaryname = 'Darunavir 600mg', strength = '60 comp'  WHERE code = '08S30Y';
UPDATE products SET primaryname = 'Ritonavir 100mg', strength = '60 comp'  WHERE code = '08S29';
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 200mg/50mg', strength = '120 comp'  WHERE code = '08S38Z';
-- Children
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 100mg/25mg', strength = '60 comp'  WHERE code = '08S39B';
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 40mg/10mg Grânulos', strength = '120 Saq'  WHERE code = '08S38Y';
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 80/20 ml Solução Oral', strength = '60 ml'  WHERE code = '08S39Y';
UPDATE products SET primaryname = 'Efavirenze (EFV) 200mg', strength = '90 comp'  WHERE code = '08S20';
UPDATE products SET primaryname = 'Abacavir 60mg/ Lamivudina 30mg', strength = '60 comp'  WHERE code = '08S01ZZ';
UPDATE products SET primaryname = 'Abacavir 120mg/ Lamivudina 60mg', strength = '60 comp'  WHERE code = '08S01ZW';
UPDATE products SET primaryname = 'Lamivudina 30mg/ Zidovudina 60mg', strength = '60 comp'  WHERE code = '08S40Z';
UPDATE products SET primaryname = 'Zidovudina 60mg/Lamivudina 30mg/Nevirapina 50mg', strength = '60 comp'  WHERE code = '08S42B';
UPDATE products SET primaryname = 'Abacavir 60mg', strength = '60 comp'  WHERE code = '08S10B';
UPDATE products SET primaryname = 'Darunavir 400mg', strength = '60 comp'  WHERE code = '08S30YX';
--Others
UPDATE products SET primaryname = 'Nevirapina 50mg/5ml Sol Oral', strength = '100 ou 240ml'  WHERE code = '08S17';
UPDATE products SET primaryname = 'Zidovudina 50mg/5ml Sol Oral', strength = '100 ou 240ml'  WHERE code = '08S23';

ALTER TABLE program_products ADD COLUMN  versionCode  VARCHAR(20);


DO
$do$
    BEGIN
        -- one product code --> multi program,
        -- one product code --> multi android version
        DROP INDEX IF EXISTS i_program_product_programid_productid;
        CREATE UNIQUE INDEX i_program_product_programid_productid ON program_products(programId, productId, versionCode);
        ALTER TABLE program_products DROP CONSTRAINT program_products_productid_programid_key;
        ALTER TABLE program_products ADD CONSTRAINT program_products_productid_programid_key UNIQUE (productid, programid,versioncode);

        -- For 86
        UPDATE program_products SET versionCode='86' where versioncode is null;

        IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
            -- Adult & Children for '87'()
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S18WI'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            FROM program_products pp JOIN products p ON pp.productid = p.id  JOIN product_categories pc ON pc.id = pp.productcategoryid  JOIN  programs p2 ON p2.id  = pp.programid
            WHERE p.code='08S18WI' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S18Y'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S18Y' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S40'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S40' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S18Z'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S18Z'  AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S01ZY'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S01ZY' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S18W'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S18W' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S30WZ'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S30WZ' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S29'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            where p.code='08S29' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S30Y'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            where p.code='08S30Y' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S30ZY'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S30ZY' AND pp.programid =1;


            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S30YX'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S30YX' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S42B'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S42B' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S40Z'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S40Z' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S01ZZ'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S01ZZ' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S20'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S20' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S39Y'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S39Y' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S38Y'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S38Y' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S39B'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S39B' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S01ZW'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S01ZW' AND pp.programid =1;


            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S23'),(SELECT id from product_categories WHERE "name"='Solution'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S23' AND pp.programid =1;
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S17'),(SELECT id from product_categories WHERE "name"='Solution'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S17' AND pp.programid =1;

            -- '08S38Z' : Adults
            -- '08S10B': Children

        END IF;
    END
$do$


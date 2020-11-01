-- Adult
UPDATE products SET primaryname = 'Lopinavir/Ritonavir 200mg/50mg', strength = '120 comp'  WHERE code = '08S39Z';
-- Children
UPDATE products SET primaryname = 'Abacavir 60mg', strength = '60 comp'  WHERE code = '08S01Z';


DO
$do$
    BEGIN
        IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
            -- Adult '87'
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S39Z'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            FROM program_products pp JOIN products p ON pp.productid = p.id  JOIN product_categories pc ON pc.id = pp.productcategoryid  JOIN  programs p2 ON p2.id  = pp.programid
            WHERE p.code='08S39Z' AND p2.code='MMIA';

            --Children for '87'
            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S01Z'),(SELECT id from product_categories WHERE "name"='Children'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            WHERE p.code='08S01Z' AND p2.code='MMIA';

        END IF;
    END
$do$
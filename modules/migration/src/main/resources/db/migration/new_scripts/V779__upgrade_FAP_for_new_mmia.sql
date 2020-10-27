ALTER TABLE facility_approved_products ADD COLUMN  versionCode  VARCHAR(20) DEFAULT NULL;

DO
$do$
    BEGIN
        -- For 86
        UPDATE facility_approved_products SET versionCode='86' where versioncode is null;

        IF EXISTS (SELECT * FROM programs WHERE id = 1) THEN
            -- Adult for '87'()

            INSERT INTO program_products(versioncode,programid,productid,productcategoryid,dosespermonth,active,currentprice,createdby,createddate,modifiedby,modifieddate,displayorder,fullsupply,isacoefficientsid)
            SELECT '87',(SELECT id from programs WHERE code='MMIA'),(SELECT id from products WHERE code = '08S29'),(SELECT id from product_categories WHERE "name"='Adult'),pp.dosespermonth,true,pp.currentprice,pp.createdby,pp.createddate,pp.modifiedby,pp.modifieddate,pp.displayorder,pp.fullsupply,pp.isacoefficientsid
            from program_products pp join products p on pp.productid = p.id  join product_categories pc on pc.id = pp.productcategoryid  join  programs p2 on p2.id  = pp.programid
            where p.code='08S29';

            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S18WI' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S18W' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S18Y' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S40' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S18Z' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S01ZY' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S30WZ' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S30ZY' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S39Z' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S30Y' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S29' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();

            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S39B' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S38Y' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S39Y' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S20' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S01ZZ' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S01ZW' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S40Z' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S42B' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S01Z' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S30YX' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();

            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S23' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();
            INSERT INTO facility_approved_products(versioncode,facilitytypeid,programproductid,maxmonthsofstock,minmonthsofstock,eop,createdby,createddate,modifiedby,modifieddate)
            SELECT '87',2,(select pp.id from program_products pp join products p2 on pp.productid = p2.id where p2.code='08S17' and pp.programid = 1 and pp.versioncode='87'),1,1,NULL,1, now(),1,now();

        END IF;
    END
$do$

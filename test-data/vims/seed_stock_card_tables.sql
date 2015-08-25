DO
$$
BEGIN

  INSERT INTO lots (productid, lotnumber, manufacturername, manufacturedate, expirationdate)
  VALUES ((SELECT id FROM products WHERE primaryname = 'BCG'),'A1','Manufacturer 1','2015-05-01 00:00:00','2016-05-01 00:00:00')
    ,((SELECT id FROM products WHERE primaryname = 'BCG'),'B1','Manufacturer 2','2015-06-01 00:00:00','2016-06-01 00:00:00')
    ,((SELECT id FROM products WHERE primaryname = 'OPV'),'A2','Manufacturer 1','2015-07-01 00:00:00','2016-07-01 00:00:00')
    ,((SELECT id FROM products WHERE primaryname = 'OPV'),'B2','Manufacturer 2','2015-08-01 00:00:00','2016-08-01 00:00:00')
  ;

  INSERT INTO stock_cards (facilityid, productid, totalquantityonhand, effectivedate, notes)
  VALUES ((SELECT id FROM facilities WHERE name = 'Arusha RVS'),(SELECT id FROM products WHERE primaryname = 'BCG'),550,'2015-08-02 00:00:00','Test stock card for BCG at Arusha RVS')
    ,((SELECT id FROM facilities WHERE name = 'Arusha RVS'),(SELECT id FROM products WHERE primaryname = 'OPV'),750,'2015-08-02 00:00:00','Test stock card for OPV at Arusha RVS')
    ,((SELECT id FROM facilities WHERE name = 'Karatu DVS'),(SELECT id FROM products WHERE primaryname = 'BCG'),110,'2015-08-03 00:00:00','Test stock card for BCG at Karatu DVS')
    ,((SELECT id FROM facilities WHERE name = 'Karatu DVS'),(SELECT id FROM products WHERE primaryname = 'OPV'),90,'2015-08-04 00:00:00','Test stock card for OPV at Karatu DVS')
  ;

  INSERT INTO lots_on_hand (stockcardid, lotid, quantityonhand, effectivedate)
  VALUES ((SELECT id FROM stock_cards WHERE notes = 'Test stock card for BCG at Arusha RVS'),(SELECT id FROM lots WHERE lotnumber = 'A1'),125,'2015-08-02 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for BCG at Arusha RVS'),(SELECT id FROM lots WHERE lotnumber = 'B1'),425,'2015-08-02 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for OPV at Arusha RVS'),(SELECT id FROM lots WHERE lotnumber = 'A2'),100,'2015-08-02 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for OPV at Arusha RVS'),(SELECT id FROM lots WHERE lotnumber = 'B2'),650,'2015-08-02 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for BCG at Karatu DVS'),(SELECT id FROM lots WHERE lotnumber = 'A1'),15,'2015-08-03 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for BCG at Karatu DVS'),(SELECT id FROM lots WHERE lotnumber = 'B1'),95,'2015-08-03 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for OPV at Karatu DVS'),(SELECT id FROM lots WHERE lotnumber = 'A2'),10,'2015-08-04 00:00:00')
    ,((SELECT id FROM stock_cards WHERE notes = 'Test stock card for OPV at Karatu DVS'),(SELECT id FROM lots WHERE lotnumber = 'B2'),80,'2015-08-04 00:00:00')
  ;

END;
$$
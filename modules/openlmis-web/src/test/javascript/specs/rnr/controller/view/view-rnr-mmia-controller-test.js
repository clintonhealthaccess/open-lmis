/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe('ViewRnrViaDetailController', function () {
  var httpBackend, scope, route, requisition, messageService, downloadPdfService, downloadSimamService;

  var submitterText = "submitterText";
  var approverText = "approverText";

  var mockedRnrItem = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {id: 1, expirationDate: null, categoryName: "Adult", productCode: "0A002"},
        {id: 2, categoryName: "Adult", productCode: "0A001"},
        {id: 3, expirationDate: "28/02/2000", categoryName: "Adult", productCode: "0B001"},
        {id: 4, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 5, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 6, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 7, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 8, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 9, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 10, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 11, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 12, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 13, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 14, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 15, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 16, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 17, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 18, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 19, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 20, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 21, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 22, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 23, expirationDate: "28/02/2000", categoryName: "Solution"},
        {id: 24, expirationDate: "28/02/2000", categoryName: "Solution"}],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014", stringYear: "2014"},
      regimenLineItems: [
        {id: 40, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 41, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 42, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 43, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 44, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 45, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 46, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 47, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 48, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 49, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 50, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 51, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 52, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 53, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 54, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 55, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 57, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 56, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 57, categoryName: "Paediatrics", patientsOnTreatment: 1}],
      patientQuantifications: [
        {id: 1, total: 1,category: 'new'},
        {id: 2, total: 1,category: 'maintenance'},
        {id: 3, total: 1,category: 'alteration'},
        {id: 4, total: 1,category: 'PPE'},
        {id: 5, total: 1,category: 'new'},
        {id: 6, total: 1,category: 'maintenance'},
        {id: 7, total: 1,category: 'alteration'},
      ],
      rnrSignatures: [{type: "SUBMITTER",text: submitterText},{type: "APPROVER",text: approverText}]
    }
  };
  var mockedRnrItemForNewMMIALayout = {
    rnr:{
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems:[
        {id:250859151, rnrId:500915, product:"Adult Product 1",productCode:"Adult code 1",productDisplayOrder:1,productPrimaryName:"Adult primary 1",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859152, rnrId:500915, product:"Adult Product 2",productCode:"Adult code 2",productDisplayOrder:1,productPrimaryName:"Adult primary 2",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859153, rnrId:500915, product:"Adult Product 3",productCode:"Adult code 3",productDisplayOrder:1,productPrimaryName:"Adult primary 3",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859154, rnrId:500915, product:"Adult Product 4",productCode:"Adult code 4",productDisplayOrder:1,productPrimaryName:"Adult primary 4",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859155, rnrId:500915, product:"Adult Product 5",productCode:"Adult code 5",productDisplayOrder:1,productPrimaryName:"Adult primary 5",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859156, rnrId:500915, product:"Adult Product 6",productCode:"Adult code 6",productDisplayOrder:1,productPrimaryName:"Adult primary 6",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859157, rnrId:500915, product:"Adult Product 7",productCode:"Adult code 7",productDisplayOrder:1,productPrimaryName:"Adult primary 7",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859158, rnrId:500915, product:"Adult Product 8",productCode:"Adult code 8",productDisplayOrder:1,productPrimaryName:"Adult primary 8",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859159, rnrId:500915, product:"Adult Product 9",productCode:"Adult code 9",productDisplayOrder:1,productPrimaryName:"Adult primary 9",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859160, rnrId:500915, product:"Adult Product 10",productCode:"Adult code 10",productDisplayOrder:1,productPrimaryName:"Adult primary 10",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859161, rnrId:500915, product:"Adult Product 11",productCode:"Adult code 11",productDisplayOrder:1,productPrimaryName:"Adult primary 11",productCategoryDisplayOrder:1,productCategory:"Adults",categoryName:"Adults"},
        {id:250859161, rnrId:500915, product:"Children Product 1",productCode:"Children code 1",productDisplayOrder:1,productPrimaryName:"Children primary 1",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859162, rnrId:500915, product:"Children Product 2",productCode:"Children code 2",productDisplayOrder:1,productPrimaryName:"Children primary 2",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859163, rnrId:500915, product:"Children Product 3",productCode:"Children code 3",productDisplayOrder:1,productPrimaryName:"Children primary 3",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859164, rnrId:500915, product:"Children Product 4",productCode:"Children code 4",productDisplayOrder:1,productPrimaryName:"Children primary 4",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859165, rnrId:500915, product:"Children Product 5",productCode:"Children code 5",productDisplayOrder:1,productPrimaryName:"Children primary 5",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859166, rnrId:500915, product:"Children Product 6",productCode:"Children code 6",productDisplayOrder:1,productPrimaryName:"Children primary 6",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859167, rnrId:500915, product:"Children Product 7",productCode:"Children code 7",productDisplayOrder:1,productPrimaryName:"Children primary 7",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859168, rnrId:500915, product:"Children Product 8",productCode:"Children code 8",productDisplayOrder:1,productPrimaryName:"Children primary 8",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859169, rnrId:500915, product:"Children Product 9",productCode:"Children code 9",productDisplayOrder:1,productPrimaryName:"Children primary 9",productCategoryDisplayOrder:1,productCategory:"Children",categoryName:"Children"},
        {id:250859170, rnrId:500915, product:"Solution Product 1",productCode:"Solution code 1",productDisplayOrder:1,productPrimaryName:"Solution primary 1",productCategoryDisplayOrder:1,productCategory:"Solution",categoryName:"Solution"},
        {id:250859171, rnrId:500915, product:"Solution Product 2",productCode:"Solution code 2",productDisplayOrder:1,productPrimaryName:"Solution primary 2",productCategoryDisplayOrder:1,productCategory:"Solution",categoryName:"Solution"},
        {id:250859172, rnrId:500915, product:"Others Product 1",productCode:"Others code 1",productDisplayOrder:1,productPrimaryName:"Others primary 1",productCategoryDisplayOrder:1,productCategory:"Others",categoryName:"Others"},
      ],
      regimenLineItems:[
        {id: 4982748,rnrId:500915,code:"001",name:"Adults 1",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982749,rnrId:500915,code:"002",name:"Adults 2",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982750,rnrId:500915,code:"003",name:"Adults 3",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982751,rnrId:500915,code:"004",name:"Adults 4",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982752,rnrId:500915,code:"005",name:"Adults 5",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982753,rnrId:500915,code:"006",name:"Adults 6",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982754,rnrId:500915,code:"007",name:"Adults 7",categoryName: "Adults", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982755,rnrId:500915,code:"008",name:"Paediatrics 1",categoryName: "Paediatrics", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982756,rnrId:500915,code:"009",name:"Paediatrics 2",categoryName: "Paediatrics", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982757,rnrId:500915,code:"010",name:"Paediatrics 3",categoryName: "Paediatrics", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982758,rnrId:500915,code:"011",name:"Paediatrics 4",categoryName: "Paediatrics", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
        {id: 4982759,rnrId:500915,code:"012",name:"Paediatrics 5",categoryName: "Paediatrics", patientsOnTreatment: 1,comunitaryPharmacy:1,regimenDisplayOrder:1},
      ],
      patientQuantifications: [{id: 969235, rnrId: 500915, category: "New", total: 12, tableName: "Type of patients in TRAV", categoryName: "New"},
        {id: 969236, rnrId: 500915,category: "Maintenance", total: 3, tableName: "Type of patients in TRAV", categoryName: "Maintenance"},
        {id: 969237, rnrId: 500915, category: "Alteration", total: 3, tableName: "Type of patients in TRAV",  categoryName: "Alteration"},
        {id: 969238, rnrId: 500915, category: "Transit",  total: 3,  tableName: "Type of patients in TRAV",categoryName: "Transit"},
        {id: 969239,rnrId: 500915, category: "Transfers", total: 3, tableName: "Type of patients in TRAV", categoryName: "Transfers"},
        {id: 969240, rnrId: 500915, category: "Month dispense", total: 1, tableName: "Months dispensed", categoryName: "Month dispense"},
        {id: 969241, rnrId: 500915, category: "3 mopnths dispense (DT)", total: 2, tableName: "Months dispensed", categoryName: "3 mopnths dispense (DT)"},
        {id: 969242, rnrId: 500915, category: "6 mopnths dispense (DS)", total: 33, tableName: "Months dispensed", categoryName: "6 mopnths dispense (DS)"},
        {id: 969243, rnrId: 500915, category: "# of therapeutic months dispensed", total: 3, tableName: "Months dispensed", categoryName: "# of therapeutic months dispensed"},
        {id: 969244, rnrId: 500915, category: "Adults", total: 4, tableName: "age range of TRAV patients", categoryName: "Adults"},
        {id: 969245, rnrId: 500915, category: "Pediatric from 0 to 4 years", total: 3, tableName: "age range of TRAV patients", categoryName: "Pediatric from 0 to 4 years"},
        {id: 969246, rnrId: 500915, category: "Pediatric from 5 to 9 years", total: 3, tableName: "age range of TRAV patients", categoryName: "Pediatric from 5 to 9 years"},
        {id: 969247, rnrId: 500915, category: "Pediatric from 10 to 14 years", total: 3, tableName: "age range of TRAV patients", categoryName: "Pediatric from 10 to 14 years"},
        {id: 969248, rnrId: 500915, category: "PPE", total: 3, tableName: "prophylaxis", categoryName: "PPE" },
        {id: 969249,rnrId: 500915,  category: "PrEP",total: 33, tableName: "prophylaxis", categoryName: "PrEP"},
        {id: 969250,   rnrId: 500915, category: "Exposed child", total: 3, tableName: "prophylaxis",  categoryName: "Exposed child" },
        { id: 969251,  rnrId: 500915,  category: "Total Nr of Patients In TARV at HF",  total: 3,  tableName: "prophylaxis", categoryName: "Total Nr of Patients In TARV at HF"}],
      therapeuticLines:[
          {id: 37, rnrId: 500915, code: "1st Line", patientsOnTreatment: 1,comunitaryPharmacy: 1},
        {id: 38, rnrId: 500915, code: "2nd Line",patientsOnTreatment: 3, comunitaryPharmacy: 1},
        { id: 39, rnrId: 500915, code: "3rd Line",patientsOnTreatment: 4, comunitaryPharmacy: 3}
      ],
      period: {stringStartDate: "21/05/2020", stringEndDate: "20/06/2020", stringYear: "2020"},
      rnrSignatures: [{type: "SUBMITTER",text: submitterText},{type: "APPROVER",text: approverText}]
    }
  };
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, _messageService_, _downloadPdfService_, _downloadSimamService_) {
    httpBackend = $httpBackend;
    scope = $rootScope.$new();
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}};
    messageService =  _messageService_;
    downloadPdfService = _downloadPdfService_;
    downloadSimamService = _downloadSimamService_;
    spyOn(downloadPdfService, "init").andReturn(function(a,b){});
    spyOn(downloadSimamService, "init").andReturn(function(a,b){});
    $controller(ViewRnrMmiaController, {$scope: scope, $route: route});
  }));


  it('should get rnr item',function(){
    initMockRequisition();

    expect(scope.rnr.fullSupplyLineItems.length).toBe(24);
    expect(scope.rnr.regimenLineItems.length).toBe(19);
    expect(scope.rnr.patientQuantifications.length).toBe(7);
  });

  it('should format year and month',function(){
    spyOn(messageService, "get").andReturn("January");
    initMockRequisition();

    expect(scope.year).toBe("2014");
    expect(scope.month).toBe("January");
  });

  it('should get the correct submitter and approver on mmia view',function(){
    initMockRequisition();

    expect(scope.submitterSignature).toEqual(submitterText);
    expect(scope.approverSignature).toEqual(approverText);
  });

  it('should format validate',function(){
    scope.rnr = mockedRnrItem.rnr;
    scope.initProduct();

    var actualResult = _.groupBy(scope.rnrLineItems, function (item) {
      return item.categoryName;
    });

    expect(actualResult.Adult[0].productCode).toEqual("0A001");
    expect(actualResult.Adult[1].productCode).toEqual("0A002");
    expect(actualResult.Adult[2].productCode).toEqual("0B001");
    expect(actualResult.Adult[1].expirationDate).toBe(null);
    expect(actualResult.Adult[0].expirationDate).toBe(undefined);
    expect(actualResult.Adult[2].expirationDate).toEqual("Feb 2000");
    expect(actualResult.Adult.length).toBe(14);
    expect(actualResult.Adult[13].productCode).toBe(undefined);
    expect(actualResult.Children.length).toBe(11);
    expect(actualResult.Children[10].productCode).toBe(undefined);
    expect(actualResult.Solution.length).toBe(3);
    expect(actualResult.Solution[2].productCode).toBe(undefined);
  });

  it('should initProduct exclude undefined category',function(){
    var mockedRnrItem = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [
          {id: 1, expirationDate: null, categoryName: "Adult", productCode: "0A002"},
          {id: 24, expirationDate: "28/02/2000", categoryName: "Solution"}],
      }
    };

    scope.rnr = mockedRnrItem.rnr;
    scope.initProduct();

    var actualResult = _.groupBy(scope.rnrLineItems, function (item) {
      return item.categoryName;
    });

    expect(actualResult.Adult.length).toBe(3);
    expect(actualResult.Children.length).toBe(1);
    expect(actualResult.Solution.length).toBe(2);
  });

  it('should initRegime exclude undefined category',function(){
    var mockedRnrItem = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        regimenLineItems: [
          {id: 40, categoryName: "Adults", patientsOnTreatment: 1}]
      }
    };
    scope.rnr = mockedRnrItem.rnr;
    scope.initRegime();
    var actualRegimens = _.groupBy(scope.regimens, function (item) {
      return item.categoryName;
    });
    expect(actualRegimens.Adults.length).toBe(3);
    expect(actualRegimens.Paediatrics.length).toBe(2);
  });

  it('should initRegime with new MMIA layout with version No.9', function () {
    initMockRequisitionForNewMMIALayout();
    expect(scope.rnr.therapeuticLines.length).toBe(3);
    expect(scope.rnr.reportType).toBe('new')
    expect(scope.therapeuticLines.length).toBe(3)
    expect(scope.therapeuticLinesTotalPatients).toBe(8)
    expect(scope.therapeuticLinesTotalComunitaryPharmacy).toBe(5)
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.patientsType'].length).toBe(5)
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.patientsType'][0]['categoryName']).toBe("New")
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.md'].length).toBe(4)
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.md'][1]['categoryName']).toBe("3 mopnths dispense (DT)")
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.TARVPatients'].length).toBe(4)
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.TARVPatients'][2]['categoryName']).toBe('Pediatric from 5 to 9 years')
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.prophylaxis'].length).toBe(4)
    expect(scope.rnr.patientQuantifications['view.rnr.mmia.patient.header.prophylaxis'][3]['categoryName']).toBe('Total Nr of Patients In TARV at HF')
  });

  it('should calculate regime total',function(){
    scope.rnr = mockedRnrItem.rnr;
    scope.initRegime();

    var actualRegimens = _.groupBy(scope.regimens, function (item) {
      return item.categoryName;
    });
    expect(scope.regimeTotalPatients).toBe(19);
    expect(actualRegimens.Adults.length).toBe(11);
    expect(actualRegimens.Paediatrics.length).toBe(12);
  });

  function initMockRequisition() {
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, mockedRnrItem);
    scope.loadMmiaDetail();
    httpBackend.flush();
  }

  function initMockRequisitionForNewMMIALayout() {
    httpBackend.expect('GET', "/requisitions/1/skipped.json").respond(200, mockedRnrItemForNewMMIALayout);
    scope.loadMmiaDetail();
    httpBackend.flush();
  }
});

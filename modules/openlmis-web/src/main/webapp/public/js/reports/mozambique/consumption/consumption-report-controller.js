function ConsumptionReportController($scope, $controller, $filter, $http, $q, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService, messageService, ReportExportExcelService) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.consumptionInPeriods = undefined;
  $scope.consumptionInPeriodsExport = undefined;

  $scope.loadReport = function () {
    if ($scope.checkDateValidRange() && validateProduct() &&
      $scope.validateProvince() && $scope.validateDistrict() && $scope.validateFacility()) {
      $scope.locationIdToCode($scope.reportParams);
      var promises = requestConsumptionDataForEachPeriod();
      $q.all(promises).then(function (consumptionsInPeriods) {
        $scope.consumptionInPeriods = _.pluck(_.pluck(consumptionsInPeriods, 'data'), 'summary');
        $scope.consumptionInPeriodsExport = _.pluck(_.pluck(consumptionsInPeriods,'data'), 'cells');
        renderConsumptionChart($scope.consumptionInPeriods);
      });
    }
  };

  $scope.exportXLSX = function () {
    var data = {
      reportHeaders: {
        drugCode: messageService.get('report.header.drug.code'),
        drugName: messageService.get('report.header.drug.name'),
        province: messageService.get('report.header.province'),
        district: messageService.get('report.header.district'),
        facility: messageService.get('report.header.facility'),
        period: messageService.get('report.header.period'),
        cmm: messageService.get('report.header.cmm'),
        consumption: messageService.get('report.header.consumption.during.period'),
        entries: messageService.get('report.header.entries.during.period'),
        soh: messageService.get('report.header.soh.at.period.end'),
        reportGeneratedFor: messageService.get('report.header.generated.for')

      },
      reportContent: []
    };

    $scope.consumptionInPeriodsExport.forEach(function (consumptionInPeriodExport) {
      consumptionInPeriodExport.forEach(function (consumptionInPeriod) {
        var consumptionReportContent = {};
        consumptionReportContent.drugCode = $scope.reportParams.productCode;
        consumptionReportContent.drugName = $scope.getDrugByCode($scope.reportParams.productCode).primaryName;
        consumptionReportContent.facility = consumptionInPeriod['facility.facility_name'];
        var facilityCode = consumptionInPeriod['facility.facility_code'];
        consumptionReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : getLocationByHfCode(facilityCode).provinceName;
        consumptionReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : getLocationByHfCode(facilityCode).districtName;
        consumptionReportContent.period = consumptionInPeriod.period;
        consumptionReportContent.cmm = consumptionInPeriod.cmm;
        consumptionReportContent.consumption = consumptionInPeriod.total_quantity;
        consumptionReportContent.entries = consumptionInPeriod.entries;
        consumptionReportContent.soh = consumptionInPeriod.soh;
        consumptionReportContent.reportGeneratedFor = DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.startTime) + ' - ' + DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.endTime);

        data.reportContent.push(consumptionReportContent);
      });
    });

    ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.historical.data.report'));
  };

  function getLocationByHfCode(facilityCode) {
    var location = {};
    var facility = _.find($scope.facilities, function(hf) { return hf.code == facilityCode; });
    var district = _.find($scope.districts, function(dst) { return dst.id == facility.geographicZoneId; });
    var province = _.find($scope.provinces, function(pro) { return pro.id == district.parentId; });
    location.districtName = district.name;
    location.provinceName = province.name;
    return location;
  }

  function renderConsumptionChart(consumptionInPeriods) {
    if (validateGeographicZones()) {
      AmCharts.makeChart("consumption-report", {
        "type": "serial",
        "theme": "light",
        "dataProvider": consumptionInPeriods,
        "graphs": [{
          "bullet": "round",
          "valueField": "soh",
          "balloonText": messageService.get("stock.movement.soh") + ": [[value]]"
        }, {
          "bullet": "round",
          "valueField": "cmm",
          "balloonText": "C.M.M.: [[value]]"
        }, {
          "bullet": "round",
          "valueField": "total_quantity",
          "balloonText": messageService.get("consumption.chart.balloon.text") + ": [[value]]"
        }, {
          "bullet": "round",
          "valueField": "entries",
          "lineColor": "#008000",
          "balloonText": messageService.get("entries.chart.balloon.text") + ": [[value]]"
        }],
        "chartScrollbar": {
          "oppositeAxis": false,
          "offset": 100
        },
        "chartCursor": {},
        "categoryField": "period",
        "categoryAxis": {
          "autoRotateCount": 5,
          "autoRotateAngle": 45
        }
      });
    } else {
      AmCharts.makeChart("consumption-report", {
        "type": "serial",
        "theme": "light",
        "dataProvider": consumptionInPeriods,
        "graphs": [{
          "bullet": "round",
          "valueField": "soh",
          "balloonText": messageService.get("stock.movement.soh") + ": [[value]]"
        }, {
          "bullet": "round",
          "valueField": "total_quantity",
          "balloonText": messageService.get("consumption.chart.balloon.text") + ": [[value]]"
        }, {
          "bullet": "round",
          "valueField": "entries",
          "lineColor": "#008000",
          "balloonText": messageService.get("entries.chart.balloon.text") + ": [[value]]"
        }],
        "chartScrollbar": {
          "oppositeAxis": false,
          "offset": 100
        },
        "chartCursor": {},
        "categoryField": "period",
        "categoryAxis": {
          "autoRotateCount": 5,
          "autoRotateAngle": 45
        }
      });
    }
  }

  function requestConsumptionDataForEachPeriod() {
    var periodsInSelectedRange = $scope.splitPeriods($scope.reportParams.startTime, $scope.reportParams.endTime);
    return _.map(periodsInSelectedRange, function (period) {
      var dataFromMV = $scope.pickMVs(period);
      var drilldownParams = ["facility"];
      var cutParams = CubesGenerateCutParamsService.generateCutsParams("periodstart",
        $filter('date')(period.periodStart, "yyyy,MM,dd"),
        $filter('date')(period.periodStart, "yyyy,MM,dd"),
        $scope.reportParams.selectedFacility,
        [{"drug.drug_code": $scope.reportParams.productCode}],
        $scope.reportParams.selectedProvince,
        $scope.reportParams.selectedDistrict
      );
      var consumpParams = cutParams.concat({
        dimension: 'reason_code', values: [
          'UNPACK_KIT',
          'DEFAULT_ISSUE',
          'PUB_PHARMACY',
          'MATERNITY',
          'GENERAL_WARD',
          'ACC_EMERGENCY',
          'MOBILE_UNIT',
          'LABORATORY',
          'UATS',
          'PNCTL',
          'PAV',
          'DENTAL_WARD',
          'ISSUE',
          'NO_MOVEMENT_IN_PERIOD'
        ]
      });
      var entryParams = cutParams.concat({
        dimension: 'reason_code', values: [
          'DISTRICT_DDM',
          'PROVINCE_DPM'
        ]
      });
      return $http
        .get(CubesGenerateUrlService.generateAggregateUrl(dataFromMV, drilldownParams, consumpParams))
        .then(function (consumptionData) {
          return $http
            .get(CubesGenerateUrlService.generateAggregateUrl(dataFromMV, drilldownParams, entryParams))
            .then(function (entryData) {
              var periodRange = DateFormatService.formatDateWithLocaleNoDay(period.periodStart) +
                  "-" +
                  DateFormatService.formatDateWithLocaleNoDay(period.periodEnd);
              consumptionData.data.summary.period = periodRange;

              consumptionData.data.summary.entries = entryData.data.summary.total_quantity;

              _.map(consumptionData.data.cells, function (consumptionCell) {
                consumptionCell.period = periodRange;
                _.map(entryData.data.cells, function (entryCell) {
                  if (consumptionCell['facility.facility_code'] == entryCell['facility.facility_code']) {
                    consumptionCell.entries = entryCell.total_quantity;
                  }
                });
              });
              return consumptionData;
            });
        });
    });
  }

  function validateProduct() {
    $scope.noProductSelected = !$scope.reportParams.productCode;
    return !$scope.noProductSelected;
  }

  function validateGeographicZones() {
    return !!parseInt($scope.reportParams.facilityId, 10);
  }
}
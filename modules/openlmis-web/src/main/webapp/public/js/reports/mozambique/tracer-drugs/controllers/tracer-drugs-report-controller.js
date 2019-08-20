function TracerDrugsReportController($scope, $controller, TracerDrugsChartService, NosDrugsChartService) {
  $controller('BaseProductReportController', {$scope: $scope});
  var SELECTION_CHECKBOX_NOT_SELECT_STYLES = 'selection-checkbox__not-select';
  var SELECTION_CHECKBOX_ALL_STYLES = 'selection-checkbox__all';
  $scope.reportLoaded = false;
  $scope.selectedDrugCode = '';
  $scope.buttonDisplay = false;
  $scope.showDrugList = false;
  $scope.selectedAll = false;
  $scope.selectedDrugAllClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
  $scope.selectedDrugNames = [];
  $scope.selectedDrugs = [];

  init();

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {
      getReportLoaded();
    }
  };

  $scope.exportXLSX = function () {
    NosDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict(), "tracerDrug", $scope.selectedDrugs);
  };

  $scope.onChangeSelectedDrug = function () {
    $scope.loadReport();
  };

  function getReportLoaded() {
    $scope.reportLoaded = true;
    NosDrugsChartService.getNosDrugItemsPromise(getSelectedProvince(), getSelectedDistrict(),
      $scope.reportParams.startTime, $scope.reportParams.endTime, $scope.selectedDrugCode, "tracerDrug")
      .$promise.then(function (result) {
      NosDrugsChartService.makeNosDrugHistogram('tracer-report', result.data);
    });
  }

  $scope.clickDrugSelection = function () {
    $scope.showDrugList = !$scope.showDrugList;
  };

  $scope.closeDrugListDialog = function () {
    $scope.showDrugList = false;
  };

  $scope.selectALL = function () {
    $scope.selectedAll = !$scope.selectedAll;
    $scope.selectedDrugs = [];
    $scope.selectedDrugNames = [];
    if ($scope.selectedAll) {
      $scope.selectedDrugAllClass = SELECTION_CHECKBOX_ALL_STYLES;
      _.forEach($scope.drugList, function(nosDrug) {
        nosDrug.isSelected = true;
        $scope.selectedDrugs.push(nosDrug['drug.drug_code']);
        $scope.selectedDrugNames.push(nosDrug['drug.drug_name']);
      });
    } else {
      $scope.selectedDrugAllClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
      _.forEach($scope.drugList, function(nosDrug) {
        nosDrug.isSelected = false;
      });
    }
  };

  $scope.selectDrug = function (nosDrug) {
    nosDrug.isSelected = !nosDrug.isSelected;
    $scope.selectedDrugs = [];
    $scope.selectedDrugNames = [];
    _.forEach($scope.drugList, function(nosDrug) {
      if (nosDrug.isSelected) {
        $scope.selectedDrugs.push(nosDrug['drug.drug_code']);
        $scope.selectedDrugNames.push(nosDrug['drug.drug_name']);
      }
    });
  };

  function init() {
    var tracerDrugListPromis = TracerDrugsChartService.getTracerDrugList();

    tracerDrugListPromis.then(function (tracerDrugListResult) {
      $scope.drugList = tracerDrugListResult.data;
      $scope.buttonDisplay = tracerDrugListResult.data.length > 0;
      $scope.selectedDrugCode = tracerDrugListResult.data[0]['drug.drug_code'];
    });
  }

  function getSelectedProvince() {
    return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
  }

  function getSelectedDistrict() {
    return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
  }
}
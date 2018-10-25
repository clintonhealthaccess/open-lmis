function ExpiredProductsReportController($scope, $controller, $filter, ExpiredProductsService, DateFormatService,
                                         ReportGroupSortAndFilterService, UnitService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedExpiredProductList = [];
  $scope.showExpiredProductsTable = false;
  $scope.expiredProductList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictExpiredProduct = false;
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    UnitService.onLoadScrollEvent('fixed-body', 'fixed-header');
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var expiredProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };
      
      $scope.isDistrictExpiredProduct = utils.isEmpty(reportParams.districtId);
      $scope.formattedExpiredProductList = [];
      
      ExpiredProductsService
        .getExpiredProductList()
        .get(_.pick(expiredProductParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        }), {}, function (expiredProductResponse) {
          $scope.showExpiredProductsTable = true;
          $scope.formattedExpiredProductList = formatExpiredProductList(expiredProductResponse.rnr_list);
          $scope.showExpiredProductsTable = expiredProductResponse.rnr_list.length;
          $scope.expiredProductList = formatExpiredProductListTime(expiredProductResponse.rnr_list);
          $scope.filterAndSort();
        });
    }
  };
  
  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;
    
    ExpiredProductsService.getDataForExport(
      reportParams.provinceId.toString(),
      reportParams.districtId.toString(),
      reportParams.facilityId.toString(),
      $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59"
    );
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.expiredProductList, $scope.filterText, "lotList", "expiryDate");
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedExpiredProductList = formatExpiredProductList($scope.filterList);
    
  };
  
  function formatExpiredProductListTime(expiredProductList) {
    return _.map(expiredProductList, function (expiredProduct) {
      expiredProduct.cmm = toFixedNumber(expiredProduct.cmm);
      expiredProduct.mos = toFixedNumber(expiredProduct.mos);
      return expiredProduct;
    });
    
  }
  
  function formatExpiredProductList(expiredProductList) {
    var formattedExpiredProductList = [];
    _.forEach(expiredProductList, function (expiredProduct) {
      _.forEach(expiredProduct.lotList, function (lot, index) {
        var formatItem = {
          provinceName: expiredProduct.provinceName,
          districtName: expiredProduct.districtName,
          facilityName: expiredProduct.facilityName,
          productCode: expiredProduct.productCode,
          productName: expiredProduct.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot
        };
        
        if (!$scope.isDistrictExpiredProduct) {
          _.assign(formatItem, {
            cmm: toFixedNumber(expiredProduct.cmm),
            mos: toFixedNumber(expiredProduct.mos),
            rowSpan: expiredProduct.lotList.length,
            isFirst: index === 0
          });
        }
  
        formattedExpiredProductList.push(formatItem);
      });
    });
    
    return formattedExpiredProductList;
  }
  
  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return null;
    }
    
    return parseFloat(originNumber.toFixed(2));
  }
}

services.factory('ExpiredProductsService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getExpiredProductList() {
    return $resource('/reports/overstock-report', {}, {});
  }
  
  function getDataForExport(provinceId, districtId, facilityId, endTime) {
    var data = {
      provinceId: provinceId,
      districtId: districtId,
      facilityId: facilityId,
      endTime: endTime,
      reportType: 'overStockProductReport'
    };
    
    ReportExportExcelService.exportAsXlsxBackend(
      _.pick(data, function (param) {
        if (!utils.isEmpty(param)) {
          return param;
        }
      }), messageService.get('report.file.over.stock.products.report'));
  }
  
  return {
    getExpiredProductList: getExpiredProductList,
    getDataForExport: getDataForExport
  };
});
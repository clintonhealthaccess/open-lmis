function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService,
                                       CubesGenerateUrlService, FeatureToggleService, LotExpiryDateService,
                                       $window, ReportExportExcelService, messageService, DateFormatService,
                                       ReportGroupSortAndFilterService, NewReportService) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.filterList = [];
  $scope.filterText = "";

  var CMM_STATUS = {
    'STOCK OUT': 'stock-out',
    'REGULAR STOCK': 'regular-stock',
    'OVER STOCK': 'over-stock',
    'LOW STOCK': 'low-stock'
  };

  $scope.$on('$viewContentLoaded', function () {
    FeatureToggleService.get({key: 'lot.expiry.dates.report'}, function (result) {
      $scope.isLotExpiryDatesToggleOn = result.key;
    });

    $scope.loadProducts();
  });

  $scope.hasSyncTimeColumn = true;

  function formatSingleProductListForSearch(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
      var formatItem = {
        facilityName: item.facilityName,
        facilityCode: item.facilityCode,
        productName: item.productName,
        stockOnHandStatus: item.stockOnHandStatus.replace('_', ' '),
        sumStockOnHand: item.sumStockOnHand,
        mos: item.mos,
        cmm: item.cmm,
        syncDate: new Date(item.lastSyncUpDate)
      };

      var lotList = _.sortBy(item.lotList, function (o) {
        return o.expiryDate;
      });

      formatItem.expiry_date = lotList[0].expiryDate;
      formatItem.soonest_expiring_loh = DateFormatService.formatDateWithLocale(lotList[0].expiryDate) + "(" + messageService.get("report.stock.on.hand.amount") + lotList[0].stockOnHandOfLot + ")";
      formatItem.lotList = lotList;
      formattedSingleProductList.push(formatItem);
    });


    return formattedSingleProductList;
  }

  function formatSingleProductList(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
        _.forEach(item.lotList, function (lot, index) {
          var formatItem = {
            facilityName: item.facilityName,
            productName: item.productName,
            stockOnHandStatus: item.stockOnHandStatus,
            facilityCode: item.facilityCode,
            expiry_date: DateFormatService.formatDateWithLocale(item.expiry_date),
            soonest_expiring_loh: item.soonest_expiring_loh,
            lotNumber: lot.lotNumber,
            stockOnHandOfLot: lot.stockOnHandOfLot,
            sumStockOnHand: item.sumStockOnHand,
            cmm: toFixedNumber(item.cmm),
            estimated_months: toFixedNumber(item.mos),
            syncDate: DateFormatService.formatDateWithTimeAndLocale(item.syncDate),
            rowSpan: item.lotList.length,
            isFirst: index === 0
          };

          formattedSingleProductList.push(formatItem);
        });
    });

    return formattedSingleProductList;
  }

  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return null;
    }

    return parseFloat(originNumber.toFixed(2));
  }

  var sortList = ['lotNumber', 'stockOnHandOfLot'];
  var ignoreSearchList = ['expiryDate', 'productName', 'facilityCode'];
  var timeFieldList = ['expiry_date', 'syncDate'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.originData, $scope.filterText, "lotList", timeFieldList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.reportData = formatSingleProductList($scope.filterList);
  };

  $scope.loadReport = function () {
    if ($scope.validateProduct() && $scope.validateProvince() && $scope.validateDistrict()) {
      var reportParams = $scope.reportParams;

      var singleProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        productCode: reportParams.productCode.toString(),
        reportType: "singleStockOnHand"
      };

      $scope.formattedOverStockList = [];

      NewReportService.get(_.pick(singleProductParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        })).$promise.then(function (singleProductResponse) {
          $scope.originData = singleProductResponse.data;
          $scope.originData = formatSingleProductListForSearch($scope.originData);
          $scope.reportData = formatSingleProductList($scope.originData);
          $scope.filterAndSort();
        });
    }
  };

  $scope.generateRedirectToExpiryDateReportURL = function (facilityCode) {
    var date = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd");

    var redirectedURL = "/public/pages/reports/mozambique/index.html#/lot-expiry-dates" + "?" +
      "facilityCode=" + facilityCode + "&" +
      "date=" + date + "&" +
      "drugCode=" + $scope.reportParams.productCode;
    return redirectedURL;
  };

  $scope.redirectToLotExpiryDateReport = function (facilityCode) {
    $window.location.href = $scope.generateRedirectToExpiryDateReportURL(facilityCode);
  };

  $scope.cmmStatusStyle = function (status) {
    return CMM_STATUS[status];
  };

  $scope.exportXLSX = function () {

    if ($scope.validateProduct() && $scope.validateProvince() && $scope.validateDistrict()) {
      var reportParams = $scope.reportParams;

      var data = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        productCode: reportParams.productCode.toString(),
        reportType: "singleStockOnHand"
      };

      ReportExportExcelService.exportAsXlsxBackend(
        utils.pickEmptyObject(data), messageService.get('report.file.single.product.soh.report'));
    }

  };
}
  angular.module('supervisory-node', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller: SupervisoryNodeListController, templateUrl: 'partials/list.html'}).
        when('/create-supervisory-node', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html',resolve:SupervisoryNodeController.resolve}).
        when('/edit/:supervisoryNodeId', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html',resolve:SupervisoryNodeController.resolve}).
        otherwise({redirectTo: '/list'});
    }]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    })
    .directive('select2Blur', function () {
      return function (scope, elm, attrs) {
        angular.element("body").on('mousedown', function (e) {
          $('.select2-dropdown-open').each(function () {
            if (!$(this).hasClass('select2-container-active')) {
              $(this).data("select2").blur();
            }
          });
        });
      };
    })
    .run(function ($rootScope, AuthorizationService) {
      $rootScope.supervisoryNodeSelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_SUPERVISORY_NODE');
    });


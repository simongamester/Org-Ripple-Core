'use strict';

angular.module('rippleDemonstrator')
  .controller('MainSearchController', function($scope, AdvancedSearch) {
    $scope.mainSearchEnabled = true;
    $scope.searchExpression = '';

    $scope.isClickToAdvancedSearch = true;

    $scope.openAdvancedSearch = AdvancedSearch.openAdvancedSearch;

    $scope.$emit('toggleHeaderSearchEnabled', false);

    $scope.hideSearch = function() {
      $scope.mainSearchEnabled = false;
      $scope.$emit('toggleHeaderSearchEnabled', true);
      $scope.$emit('populateHeaderSearch', $scope.searchExpression);
    };

    $scope.searchFunction = function() {
        if($scope.isClickToAdvancedSearch) {
          $scope.openAdvancedSearch();
        }
    };

  });


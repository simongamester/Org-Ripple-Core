'use strict';

angular.module('rippleDemonstrator')
  .controller('headerController', function ($scope, $rootScope, $window, $state, usSpinnerService, $stateParams, UserService, AdvancedSearch) {

    $rootScope.searchExpression = '';
    $scope.searchExpression = $rootScope.searchExpression;
    $scope.reportTypes = [];

    $scope.searchFocused = false;

    var redirectUrl;

    // Get current user
    UserService.findCurrentUser().then( function (response) {
       redirectUrl = response.headers().location;

      if (redirectUrl) {
        $window.location = redirectUrl;
      }
      else {
        $rootScope.currentUser = response.data;
        $scope.autoAdvancedSearch = false;

        // Direct different roles to different pages at login
        switch ($scope.currentUser.role) {
          case 'IDCR':
            $state.go('main-search');
            break;
          case 'PHR':
            $state.go('patients-summary', {
              patientId: $rootScope.currentUser.nhsNumber
            });
            break;
          default:
            $state.go('patients-summary', {
              patientId: $rootScope.currentUser.nhsNumber
            });
        }
      }
    });

    $scope.logout = function () {
      UserService.logout().then(function (response) {
        redirectUrl = response.headers().location;

        if (redirectUrl) {
          $window.location = redirectUrl;
        }
      });
    };

    $rootScope.$on('$stateChangeSuccess', function (event, toState) {
      var params = $stateParams;
      var previousState = '';
      var pageHeader = '';
      var previousPage = '';

      var mainWidth = 0;
      var detailWidth = 0;

      switch (toState.name) {
        case 'main-search':
          previousState = '';
          pageHeader = 'Welcome';
          previousPage = '';
          mainWidth = 12;
          detailWidth = 0;
          break;
      case 'patients-list':
        previousState = 'patients-charts';
        pageHeader = 'Patient Lists';
        previousPage = 'Patient Dashboard';
        mainWidth = 12;
        detailWidth = 0;
        break;
      case 'patients-charts':
        previousState = '';
        pageHeader = 'Patient Dashboard';
        previousPage = '';
        mainWidth = 12;
        detailWidth = 0;
        break;
      case 'patients-summary':
        previousState = 'patients-list';
        pageHeader = 'Patient Summary';
        previousPage = 'Patient Lists';
        mainWidth = 12;
        detailWidth = 0;
        break;
      case 'patients-lookup':
        previousState = '';
        pageHeader = 'Patients lookup';
        previousPage = '';
        mainWidth = 6;
        detailWidth = 6;
        break;
      case 'search-report':
        previousState = 'patients-charts';
        pageHeader = 'Report Search';
        previousPage = 'Patient Dashboard';
        mainWidth = 12;
        detailWidth = 0;
        break;
      case 'patients-list-full':
        previousState = 'patients-charts';
        pageHeader = 'Patients Details';
        previousPage = 'Patient Dashboard';
        mainWidth = 12;
        detailWidth = 0;
        break;
      default:
        previousState = 'patients-list';
        pageHeader = 'Patients Details';
        previousPage = 'Patient Lists';
        mainWidth = 6;
        detailWidth = 6;
        break;
      }

      if (params.queryType === 'Reports: ') {
        previousState = 'search-report';
        previousPage = 'Report Chart';
      }

      $scope.containsReportString = function () {
        return $scope.searchExpression.indexOf('rp ') === 0;
      };

      $scope.containsSettingString = function () {
        return $scope.searchExpression.lastIndexOf('st ') === 0;
      };

      $scope.containsPatientString = function () {
        return $scope.searchExpression.lastIndexOf('pt ') === 0;
      };

      $scope.containsReportTypeString = function () {
        for (var i = 0; i < $scope.reportTypes.length; i++) {
          if ($scope.searchExpression.lastIndexOf($scope.reportTypes[i]) !== -1) {
            return true;
          }
        }
        return false;
      };

      $rootScope.searchMode = false;
      $rootScope.reportMode = false;
      $rootScope.settingsMode = false;
      $rootScope.patientMode = false;
      $rootScope.reportTypeSet = false;
      $rootScope.reportTypeString = '';

      $scope.checkExpression = function () {
        if($scope.autoAdvancedSearch) {
          if($scope.searchExpression.length >= 3) {
            AdvancedSearch.openAdvancedSearch($scope.searchExpression);
          }
        }
        else if ($rootScope.searchMode) {
          if ($rootScope.reportMode && !$rootScope.reportTypeSet) {
            $scope.reportTypes = [
              'Diagnosis: ',
              'Orders: '
              ];
          }
          if ($scope.containsReportTypeString() && !$scope.patientMode) {
            $rootScope.reportTypeSet = true;
            $scope.processReportTypeMode();
          }
        } else {
          $scope.reportTypes = [];
          $rootScope.searchMode = ($scope.containsReportString() || $scope.containsSettingString() || $scope.containsPatientString());
          $rootScope.reportMode = $scope.containsReportString();
          $rootScope.settingsMode = $scope.containsSettingString();
          $rootScope.patientMode = $scope.containsPatientString();
          if ($rootScope.reportMode) {
            if ($scope.containsReportTypeString) {
              $scope.processReportTypeMode();
            }
            $scope.processReportMode();
          }
          if ($rootScope.settingsMode) {
            $scope.processSettingMode();
          }
          if ($rootScope.patientMode) {
            $scope.processPatientMode();
          }
        }
      };

      $scope.cancelSearchMode = function () {
        $rootScope.reportMode = false;
        $rootScope.searchMode = false;
        $rootScope.patientMode = false;
        $rootScope.settingsMode = false;
        $scope.searchExpression = '';
        $scope.reportTypes = '';
        $rootScope.reportTypeSet = false;
        $rootScope.reportTypeString = '';
      };

      $scope.cancelReportType = function () {
        $rootScope.reportTypeString = '';
        $rootScope.reportTypeSet = false;
      };

      $scope.searchFunction = function () {
        if($scope.autoAdvancedSearch)
        {
          AdvancedSearch.openAdvancedSearch();
        }
        if ($rootScope.reportTypeSet && $scope.searchExpression !== '') {
          var tempExpression = $rootScope.reportTypeString + ': ' + $scope.searchExpression;
          $state.go('search-report', {
            searchString: tempExpression
          });
        }
        if ($rootScope.settingsMode && $scope.searchExpression !== '') {
          $state.go('patients-list-full', {
            queryType: 'Setting: ',
            searchString: $scope.searchExpression,
            orderType: 'ASC',
            pageNumber: '1'
          });
        }
        if ($rootScope.patientMode && $scope.searchExpression !== '') {
          $state.go('patients-list-full', {
            queryType: 'Patient: ',
            searchString: $scope.searchExpression,
            orderType: 'ASC',
            pageNumber: '1'
          });
        }
      };

      $scope.processReportMode = function () {
        if ($scope.searchExpression === 'rp ') {
          $scope.searchExpression = '';
        }
      };

      $scope.processReportTypeMode = function () {
        for (var i = 0; i < $scope.reportTypes.length; i++) {
          if ($scope.searchExpression.lastIndexOf($scope.reportTypes[i]) !== -1) {
            var arr = $scope.searchExpression.split(':');
            $rootScope.reportTypeString = arr[0];
            $rootScope.reportTypeSet = true;
            $scope.searchExpression = '';
          }
        }
        $scope.reportTypes = [];
      };

      $scope.processSettingMode = function () {
        if ($scope.searchExpression === 'st ') {
          $scope.searchExpression = '';
        }
      };

      $scope.processPatientMode = function () {
        if ($scope.searchExpression === 'pt ') {
          $scope.searchExpression = '';
        }
      };

      if (typeof $stateParams.ageFrom === 'undefined') {
        previousState = 'patients-list';
        previousPage = 'Patient Lists';
      }

      $scope.pageHeader = pageHeader;
      $scope.previousState = previousState;
      $scope.previousPage = previousPage;

      $scope.mainWidth = mainWidth;
      $scope.detailWidth = detailWidth;

      $scope.searchBarEnabled = !$state.is('main-search');

      $scope.goBack = function () {
        history.back();
      };

      $scope.userContextViewExists = ('user-context' in $state.current.views);
      $scope.actionsExists = ('actions' in $state.current.views);

      $scope.go = function (patient) {
        $state.go('patients-summary', {
          patientId: patient.id
        });
      };

      if ($scope.currentUser.role === 'PHR') {
        $scope.title = 'PHR POC'
      }
      else {
        $scope.title = 'IDCR POC'
      }

      $scope.footer = 'Integrated Digital Care Record';

      $scope.goHome = function () {
        $scope.cancelSearchMode();
        if ($scope.currentUser.role === 'IDCR') {
          $state.go('patients-charts');
        }
        if ($scope.currentUser.role === 'PHR') {
          $state.go('patients-summary', {
            patientId: $scope.currentUser.nhsNumber
          });
        }
      };
    });

    $scope.openAdvancedSearch = AdvancedSearch.openAdvancedSearch;

    $scope.$on("toggleHeaderSearchEnabled", function(event, enabled) {
      $scope.searchBarEnabled = enabled;
    });

    $scope.$on("populateHeaderSearch", function(event, expression) {
      $scope.searchExpression = expression;
      $scope.searchFocused = true;
    });
    
    // Mobile Nav (New)
    $scope.currentNavTab = ''; // search, notifications or user
    
    $scope.changeNavTab = function(newTab){
      
      // Is tab already expanded?
      if( $scope.currentNavTab == newTab ){
        $scope.currentNavTab = '';
      } else {
        $scope.currentNavTab = newTab;       
      }
    }
    
    $scope.activeNavTab = function(thisTab){
      if( thisTab == $scope.currentNavTab ){
        return 'active';
      }
    }
    
  });

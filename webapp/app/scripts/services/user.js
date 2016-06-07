'use strict';

angular.module('rippleDemonstrator')
  .factory('UserService', function ($http) {

    var findCurrentUser = function () {
       return $http.get('/api/user');
    };

    return {
      findCurrentUser: findCurrentUser
    };
  });

/*
 *   Copyright 2016 Ripple OSI
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

'use strict';

angular.module('rippleDemonstrator')
  .controller('ImageModalCtrl', function ($scope, $modalInstance, $stateParams, PatientService, seriesId, series, patient, modal, Image) {

    $scope.currentUser = PatientService.getCurrentUser();
    $scope.patient = patient;
    $scope.modal = modal;
    $scope.seriesId = seriesId;
    $scope.series = series;

    $scope.ok = function () {
      $modalInstance.close();
    };

    Image.getSeriesDetails($stateParams.patientId, $scope.seriesId).then(function (result) {
      $scope.series = result.data;

      var modal = $('.modal-body').clone();
      loadSeries(modal);
    });

    function loadSeries(modal) {
      $('#wadoURL').val();

      var stacks = [];
      var currentStackIndex = 0;
      var instanceIndex = 0;

      var stack = {
        protocolName: null,                                                       // part of study???
        modality: $scope.series.modality,
        seriesDate: $scope.series.seriesDate,
        seriesTime: $scope.series.seriesTime,
        stationName: $scope.series.stationName,
        operatorsName: $scope.series.operatorsName,
        seriesNumber: $scope.series.seriesNumber,
        imageIds: [],
        imageUrls: [],
        instanceIndex: instanceIndex,
        currentImageIdIndex: 0
      };

        for (var i = 0; i < $scope.series.instanceIds.length; i++) {
          var imageUri = 'wadouri:' + '/orthanc/instances/' + $scope.series.instanceIds[i] + '/file';

          stack.imageUrls.push(imageUri);
          stack.imageIds.push(imageUri);
        }

      stacks.push(stack);

      // resize the parent div of the viewport to fit the screen
      var imageViewer = $(modal).find('.imageViewer')[0];
      var viewportWrapper = $(modal).find('.viewportWrapper')[0];
      var parentDiv = $(modal).find('.viewer')[0];
      var viewport = $(modal).find('.viewport')[0];

      viewportWrapper.style.width = (parentDiv.style.width - 10) + "px";
      viewportWrapper.style.height = (window.innerHeight - 150) + "px";

      var studyRow = $(modal).find('.studyRow')[0];
      var width = $(studyRow).width();

      $(parentDiv).width(width - 170);
      viewportWrapper.style.width = (parentDiv.style.width - 10) + "px";
      viewportWrapper.style.height = (window.innerHeight - 150) + "px";

      // image enable the dicomImage element and activate a few tools
      var element = $(modal).find('.viewport')[0];
      var parent = $(element).parent();
      var childDivs = $(parent).find('.overlay');

      var topLeft = $(childDivs[0]).find('div');
      $(topLeft[0]).text($scope.patient.name);
      $(topLeft[1]).text($scope.patient.id);

      var topRight = $(childDivs[1]).find('div');
      //$(topRight[0]).text($scope.image.studyDescription);
      //$(topRight[1]).text($scope.image.dateRecorded);

      var bottomLeft = $(childDivs[2]).find('div');
      var bottomRight = $(childDivs[3]).find('div');

      function onNewImage(e) {
        // if we are currently playing a clip then update the FPS
        var playClipToolData = cornerstoneTools.getToolState(element, 'playClip');

        if (playClipToolData !== undefined && playClipToolData.data.length > 0 && playClipToolData.data[0].intervalId !== undefined && e.detail.frameRate !== undefined) {
          $(bottomLeft[0]).text("FPS: " + Math.round(e.detail.frameRate));
        }
        else if ($(bottomLeft[0]).text().length > 0) {
          $(bottomLeft[0]).text("");
        }

        $(bottomLeft[2]).text("Image #" + (stacks[currentStackIndex].currentImageIdIndex + 1) + "/" + stacks[currentStackIndex].instanceIds.length);
      }

      element.addEventListener("CornerstoneNewImage", onNewImage, false);

      function onImageRendered(e) {
        $(bottomRight[0]).text("Zoom:" + e.detail.viewport.scale.toFixed(2));
        $(bottomRight[1]).text("WW/WL:" + Math.round(e.detail.viewport.voi.windowWidth) + "/" + Math.round(e.detail.viewport.voi.windowCenter));
      }

      element.addEventListener("CornerstoneImageRendered", onImageRendered, false);

      var imageUrl = stacks[currentStackIndex].imageUrls[0];

      // image enable the dicomImage element
      cornerstone.enable(element);

      cornerstone.registerImageLoader('cornerstoneWADOImageLoader', cornerstoneWADOImageLoader);

      cornerstone.loadAndCacheImage(imageUrl).then(function (image) {
        cornerstone.displayImage(element, image);

        if (stacks[0].frameRate !== undefined) {
          cornerstone.playClip(element, stacks[0].frameRate);
        }

        cornerstoneTools.mouseInput.enable(element);
        cornerstoneTools.mouseWheelInput.enable(element);
        cornerstoneTools.touchInput.enable(element);

        // Enable all tools we want to use with this element
        cornerstoneTools.wwwc.activate(element, 1); // ww/wc is the default tool for left mouse button
        cornerstoneTools.pan.activate(element, 2); // pan is the default tool for middle mouse button
        cornerstoneTools.zoom.activate(element, 4); // zoom is the default tool for right mouse button
        cornerstoneTools.probe.enable(element);
        cornerstoneTools.length.enable(element);
        cornerstoneTools.ellipticalRoi.enable(element);
        cornerstoneTools.rectangleRoi.enable(element);
        cornerstoneTools.wwwcTouchDrag.activate(element);
        cornerstoneTools.zoomTouchPinch.activate(element);

        // stack tools
        cornerstoneTools.addStackStateManager(element, ['playClip']);
        cornerstoneTools.addToolState(element, 'stack', stacks[0]);
        cornerstoneTools.stackScrollWheel.activate(element);
        cornerstoneTools.stackPrefetch.enable(element);

        function disableAllTools() {
          cornerstoneTools.wwwc.disable(element);
          cornerstoneTools.pan.activate(element, 2); // 2 is middle mouse button
          cornerstoneTools.zoom.activate(element, 4); // 4 is right mouse button
          cornerstoneTools.probe.deactivate(element, 1);
          cornerstoneTools.length.deactivate(element, 1);
          cornerstoneTools.ellipticalRoi.deactivate(element, 1);
          cornerstoneTools.rectangleRoi.deactivate(element, 1);
          cornerstoneTools.stackScroll.deactivate(element, 1);
          cornerstoneTools.wwwcTouchDrag.deactivate(element);
          cornerstoneTools.zoomTouchDrag.deactivate(element);
          cornerstoneTools.panTouchDrag.deactivate(element);
          cornerstoneTools.stackScrollTouchDrag.deactivate(element);
        }

        var buttons = $(imageViewer).find('button');

        // Tool button event handlers that set the new active tool
        $(buttons[0]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.wwwc.activate(element, 1);
          cornerstoneTools.wwwcTouchDrag.activate(element);
        });

        $(buttons[1]).on('click touchstart', function () {
          disableAllTools();
          var viewport = cornerstone.getViewport(element);
          viewport.invert = viewport.invert !== true;
          cornerstone.setViewport(element, viewport);
        });

        $(buttons[2]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.zoom.activate(element, 5); // 5 is right mouse button and left mouse button
          cornerstoneTools.zoomTouchDrag.activate(element);
        });

        $(buttons[3]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.pan.activate(element, 3); // 3 is middle mouse button and left mouse button
          cornerstoneTools.panTouchDrag.activate(element);
        });

        $(buttons[4]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.stackScroll.activate(element, 1);
          cornerstoneTools.stackScrollTouchDrag.activate(element);
        });

        $(buttons[5]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.length.activate(element, 1);
        });

        $(buttons[6]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.probe.activate(element, 1);
        });

        $(buttons[7]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.ellipticalRoi.activate(element, 1);
        });

        $(buttons[8]).on('click touchstart', function () {
          disableAllTools();
          cornerstoneTools.rectangleRoi.activate(element, 1);
        });

        $(buttons[9]).on('click touchstart', function () {
          var frameRate = stacks[currentStackIndex].frameRate;                              // can we use or delete the frameRate var?
          if (frameRate === undefined) {
            frameRate = 10;
          }
          cornerstoneTools.playClip(element, 31);
        });

        $(buttons[10]).on('click touchstart', function () {
          cornerstoneTools.stopClip(element);
        });

        $(buttons[0]).tooltip();
        $(buttons[1]).tooltip();
        $(buttons[2]).tooltip();
        $(buttons[3]).tooltip();
        $(buttons[4]).tooltip();
        $(buttons[5]).tooltip();
        $(buttons[6]).tooltip();
        $(buttons[7]).tooltip();
        $(buttons[8]).tooltip();
        $(buttons[9]).tooltip();

        //var seriesList = $(element).find('.thumbnails')[0];

        stacks.forEach(function (stack) {
          //var seriesEntry = '<a class="list-group-item" + ' +
          //  'oncontextmenu="return false"' +
          //  'unselectable="on"' +
          //  'onselectstart="return false;"' +
          //  'onmousedown="return false;">' +
          //  '<div class="csthumbnail"' +
          //  'oncontextmenu="return false"' +
          //  'unselectable="on"' +
          //  'onselectstart="return false;"' +
          //  'onmousedown="return false;"></div>' +
          //  "<div class='text-center small'>" + $scope.series.modality + '</div></a>';
          //
          //var seriesElement = $(viewport).appendTo(seriesList);
          //var thumbnail = $(seriesElement).find('div')[0];

          //cornerstone.enable(thumbnail);
          cornerstone.loadAndCacheImage(stacks[stack.instanceIndex].imageIds[0]).then(function (image) {
            if (stack.instanceIndex === 0) {
              $(imageViewer).addClass('active');
            }

            cornerstone.enable(imageViewer);
            cornerstone.displayImage(imageViewer, image, element);
          });

          $(imageViewer).on('click touchstart', function () {
            // make this series visible
            var activeThumbnails = $(imageViewer).find('a').each(function () {
              $(this).removeClass('active');
            });

            $(imageViewer).addClass('active');

            cornerstoneTools.stopClip(element);
            cornerstoneTools.stackScroll.disable(element);
            cornerstoneTools.stackScroll.enable(element, stacks[stack.instanceIndex], 0);

            cornerstone.loadAndCacheImage(stacks[stack.instanceIndex].imageIds[0]).then(function (image) {
              var defViewport = cornerstone.getDefaultViewport(element, image);
              currentStackIndex = stack.instanceIndex;

              cornerstone.displayImage(element, image, defViewport);
              cornerstone.fitToWindow(element);

              var stackState = cornerstoneTools.getToolState(element, 'stack');
              stackState.data[0] = stacks[stack.instanceIndex];
              stackState.data[0].currentImageIdIndex = 0;
              cornerstoneTools.stackPrefetch.enable(element);

              $(bottomLeft[1]).text("# Images: " + stacks[stack.instanceIndex].imageIds.length);
              if (stacks[stack.instanceIndex].frameRate !== undefined) {
                cornerstoneTools.playClip(element, stacks[stack.instanceIndex].frameRate);
              }
            });
          });
        });

        function resizeStudyViewer() {
          //var studyRow = $(studyRow).find('.studyRow')[0];
          var height = $(studyRow).height();
          var width = $(studyRow).width();

          $(imageViewer).height(height - 40);
          $(parentDiv).width(width - 170);

          viewportWrapper.style.width = (parentDiv.style.width - 10) + "px";
          viewportWrapper.style.height = (window.innerHeight - 150) + "px";

          cornerstone.resize(element, true);
        }

        $(window).resize(function () {
          resizeStudyViewer();
        });

        resizeStudyViewer();
      });
    }

    function resizeMain() {
      var height = $(window).height();

      $('#main').height(height - 50);
      $('#tabContent').height(height - 50 - 42);
    }

    $(window).resize(function () {
      resizeMain();
    });

    resizeMain();
  });

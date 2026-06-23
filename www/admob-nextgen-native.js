var exec = require('cordova/exec');

var PLUGIN_NAME = 'AdMobNextGenNative';

// Created by EMI INDO on Apr/30/2026

var AdMobNextGenNative = {
    show: function (options, successCallback, errorCallback) {
        exec(successCallback, errorCallback, PLUGIN_NAME, 'show', [options || {}]);
    },

    hide: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, PLUGIN_NAME, 'hide', []);
    },

    showWith: function (element, options, successCallback, errorCallback) {
        if (!element) return;

        var rect = element.getBoundingClientRect();
        var layoutParams = {
            x: rect.left + window.scrollX,
            y: rect.top + window.scrollY,
            width: rect.width,
            height: rect.height
        };

        var isCordova15Environment = false;
        if (typeof cordova !== 'undefined' && cordova.platformVersion) {
            var majorVersion = parseInt(cordova.platformVersion.split('.')[0], 10);
            if (majorVersion >= 15) {
                isCordova15Environment = true;
            }
        }

        options.isCordova15 = isCordova15Environment;

        var args = Object.assign({}, options, layoutParams);
        exec(successCallback, errorCallback, PLUGIN_NAME, 'showWith', [args]);
    }
};

module.exports = AdMobNextGenNative;
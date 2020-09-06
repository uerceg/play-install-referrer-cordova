cordova.define("cordova-play-install-referrer.PlayInstallReferrer", function(require, exports, module) {
//
//  play_install_referrer.js
//  cordova-play-install-referrer
//
//  Created by Uglješa Erceg (@ugi) on 31st July 2020.
//  Copyright (c) 2020 Uglješa Erceg. All rights reserved.
//

function callCordovaCallback(action, callback) {
    var args = Array.prototype.slice.call(arguments, 2);

    cordova.exec(callback,
        function errorHandler(err) { },
        'PlayInstallReferrer',
        action,
        args
    );
}

var PlayInstallReferrer = {
    getInstallReferrerInfo: function(callback) {
        callCordovaCallback('getInstallReferrerInfo', callback);
    },
};

module.exports = PlayInstallReferrer;

});

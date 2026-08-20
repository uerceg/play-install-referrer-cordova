//
//  play_install_referrer.js
//  cordova-play-install-referrer
//
//  Created by Uglješa Erceg (@uerceg) on 31st July 2020.
//  Copyright © 2020-Present Uglješa Erceg. All rights reserved.
//

function callCordovaCallback(action, callback) {
    var args = Array.prototype.slice.call(arguments, 2);

    // native side always delivers a JSON object and flags failures with an
    // 'errorMessage' key, so success and error both feed the same callback
    cordova.exec(callback,
        callback,
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

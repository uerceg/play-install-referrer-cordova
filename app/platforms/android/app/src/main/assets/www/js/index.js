/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    onDeviceReady: function() {
        this.receivedEvent('deviceready');
        PlayInstallReferrer.getInstallReferrerInfo(function(installReferrerInfo) {
            if (!installReferrerInfo.errorMessage) {
                console.log("install referrer = " + installReferrerInfo.installReferrer);
                console.log("referrer click timestamp seconds = " + installReferrerInfo.referrerClickTimestampSeconds);
                console.log("install begin timestamp seconds = " + installReferrerInfo.installBeginTimestampSeconds);
                console.log("referrer click timestamp server seconds = " + installReferrerInfo.referrerClickTimestampServerSeconds);
                console.log("install begin timestamp seconds = " + installReferrerInfo.installBeginTimestampServerSeconds);
                console.log("install version = " + installReferrerInfo.installVersion);
                console.log("google play instant = " + installReferrerInfo.googlePlayInstant);
            } else {
                console.log("error message: " + installReferrerInfo.errorMessage);
                console.log("error response code: " + installReferrerInfo.errorResponseCode);
            }
        });
    },

    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');
        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');
    }
};

app.initialize();
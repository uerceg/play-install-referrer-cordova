//
//  PlayInstallReferrerCordova.java
//  cordova-play-install-referrer
//
//  Created by Uglješa Erceg (@uerceg) on 31st July 2020.
//  Copyright © 2020-Present Uglješa Erceg. All rights reserved.
//

package com.uerceg.play_install_referrer;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult.Status;

import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class PlayInstallReferrerCordova extends CordovaPlugin {
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (!action.equals("getInstallReferrerInfo")) {
            return false;
        }

        // callback is getting pinged always with a JSON object
        // error is indicated if JSON contains 'errorMessage' key.
        // the callback is one shot, so guard against sending a second result - the service
        // can report a disconnect after a value has already been delivered
        final AtomicBoolean delivered = new AtomicBoolean(false);

        try {
            final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this.cordova.getActivity().getApplicationContext()).build();
            referrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        // getInstallReferrer() is a blocking IPC call and this listener is
                        // invoked on the main thread, so read the details off of it
                        cordova.getThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ReferrerDetails response = referrerClient.getInstallReferrer();

                                    // each field keeps the type the native library reports it as
                                    JSONObject installReferrerInfo = new JSONObject();
                                    installReferrerInfo.put("installReferrer", orNull(response.getInstallReferrer()));
                                    installReferrerInfo.put("referrerClickTimestampSeconds", response.getReferrerClickTimestampSeconds());
                                    installReferrerInfo.put("installBeginTimestampSeconds", response.getInstallBeginTimestampSeconds());
                                    installReferrerInfo.put("referrerClickTimestampServerSeconds", response.getReferrerClickTimestampServerSeconds());
                                    installReferrerInfo.put("installBeginTimestampServerSeconds", response.getInstallBeginTimestampServerSeconds());
                                    installReferrerInfo.put("installVersion", orNull(response.getInstallVersion()));
                                    installReferrerInfo.put("googlePlayInstant", response.getGooglePlayInstantParam());
                                    if (delivered.compareAndSet(false, true)) {
                                        callbackContext.sendPluginResult(new PluginResult(Status.OK, installReferrerInfo));
                                    }
                                } catch (Exception ex) {
                                    sendError(delivered, callbackContext, "Exception while reading install referrer info: " + ex.getMessage(), null);
                                } finally {
                                    referrerClient.endConnection();
                                }
                            }
                        });
                        return;
                    }

                    // every other code the library can report, named for readability, with the
                    // native int handed over as errorResponseCode
                    sendError(delivered, callbackContext, describe(responseCode), responseCode);
                    referrerClient.endConnection();
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // if this arrives before a value was delivered, nobody is ever going to
                    // ping the callback, so report it rather than leaving the caller waiting
                    sendError(delivered, callbackContext, "Connection to install referrer service was lost", null);
                }
            });
        } catch (Throwable ex) {
            sendError(delivered, callbackContext, "Exception while starting connection with referrer client: " + ex.getMessage(), null);
        }
        return true;
    }

    private static Object orNull(String value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static String describe(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED: return "SERVICE_DISCONNECTED";
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE: return "SERVICE_UNAVAILABLE";
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED: return "FEATURE_NOT_SUPPORTED";
            case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR: return "DEVELOPER_ERROR";
            case InstallReferrerClient.InstallReferrerResponse.PERMISSION_ERROR: return "PERMISSION_ERROR";
            default: return "Unexpected response code arrived: " + responseCode;
        }
    }

    private void sendError(AtomicBoolean delivered, CallbackContext callbackContext, String message, Integer responseCode) {
        if (!delivered.compareAndSet(false, true)) {
            return;
        }
        try {
            JSONObject error = new JSONObject();
            error.put("errorMessage", message);
            if (responseCode != null) {
                error.put("errorResponseCode", responseCode.intValue());
            }
            callbackContext.sendPluginResult(new PluginResult(Status.ERROR, error));
        } catch (JSONException ex) {
            callbackContext.sendPluginResult(new PluginResult(Status.ERROR, message));
        }
    }
}

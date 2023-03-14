/*
 * Copyright Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.splunk.android.example

import android.app.Application
import android.util.Log
import com.splunk.rum.SplunkRum
import com.splunk.rum.StandardAttributes
import io.opentelemetry.api.common.Attributes

const val LOG_TAG = "BackgrounderApp"

class BackgrounderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "********** BackgrounderApp.onCreate() is called")
        SplunkRum.builder()
            // note: for these values to be resolved, put them in your local.properties
            // file as rum.beacon.url and rum.access.token
            .setRealm(getResources().getString(R.string.rum_realm))
            .setRumAccessToken(getResources().getString(R.string.rum_access_token))
            .setApplicationName(LOG_TAG)
            .setDeploymentEnvironment("demo")
            .enableDebug()
            .setGlobalAttributes(
                Attributes.builder()
                    .put("vendor", "Splunk")
                    .put(StandardAttributes.APP_VERSION, BuildConfig.VERSION_NAME)
                    .build()
            )
            .build(this);
    }
}
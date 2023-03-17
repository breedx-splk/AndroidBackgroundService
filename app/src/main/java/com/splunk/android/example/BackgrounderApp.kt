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
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
            .build(this)

        setUpNotificationChannel()
    }

    private fun setUpNotificationChannel() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "Fetched Firebase token: $token"
            Log.d(LOG_TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        /*// Create an Intent for the activity you want to start
        val resultIntent = Intent(this, OtherRegularActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentIntent(resultPendingIntent)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }*/
    }
}

package com.splunk.android.example

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "Firebase registration token:::::::::  $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(LOG_TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(LOG_TAG, "Message data payload: ${message.data}")

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(LOG_TAG, "Message Notification Title: ${it.title}")
            Log.d(LOG_TAG, "Message Notification Body: ${it.body}")
            val attrs = Attributes.of(AttributeKey.stringKey("title"), it.title, AttributeKey.stringKey("body"), "bar")
            SplunkRum.getInstance().addRumEvent("veryCoolPush", attrs)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
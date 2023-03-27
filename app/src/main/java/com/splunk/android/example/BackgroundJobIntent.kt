package com.splunk.android.example

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService

class BackgroundJobIntent: JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, BackgroundJobIntent::class.java, 666, intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "BackgroundJobIntent created", Toast.LENGTH_SHORT).show();
        Log.w(LOG_TAG, "BackgroundJobIntent.onCreate() yay! pid = ${android.os.Process.myPid()}");
    }

    override fun onHandleWork(intent: Intent) {
        Log.w(LOG_TAG, "BackgroundJobIntent is handling work! Hooray!")
    }
}
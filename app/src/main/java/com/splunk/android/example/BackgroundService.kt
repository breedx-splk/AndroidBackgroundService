package com.splunk.android.example

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.concurrent.timerTask

class BackgroundService: Service() {

    val timer = Timer("background")

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "$$ BackgroundService.onBind() is called")
        return Binder("background binder")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "$$ BackgroundService.onStartCommand()")
        timer.schedule(timerTask {
            Log.d(LOG_TAG, "$$ timer hit")
        }, 1000, 3000)
        return super.onStartCommand(intent, flags, startId)
    }
}
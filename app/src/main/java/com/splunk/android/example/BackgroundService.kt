package com.splunk.android.example

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BackgroundService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "BackgroundService.onBind() is called")
        return Binder("no clue")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "$$ BackgroundService.onStartCommand()")
        //TODO:  start background timer here?
        return super.onStartCommand(intent, flags, startId)
    }
}
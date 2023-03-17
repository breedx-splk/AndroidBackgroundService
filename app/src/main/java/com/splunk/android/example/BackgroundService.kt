package com.splunk.android.example

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.trace.Span
import java.util.*
import kotlin.concurrent.timerTask

class BackgroundService: Service() {

    private val timer = Timer("background")
    private val splunkRum = SplunkRum.getInstance();
    private val http = HttpBuddy(splunkRum);

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "$$ BackgroundService.onBind() is called")
        return Binder("background binder")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "$$ BackgroundService.onStartCommand()")
        timer.schedule(timerTask {
            Log.d(LOG_TAG, "$$ timer hit, pid = ${android.os.Process.myPid()}")
            val workflow: Span = splunkRum!!.startWorkflow("Doing a click")
            http.makeCall("https://pmrum.o11ystore.com?user=bg&pass=inthebackground", workflow)
        }, 1000, 3000)
        return super.onStartCommand(intent, flags, startId)
    }
}
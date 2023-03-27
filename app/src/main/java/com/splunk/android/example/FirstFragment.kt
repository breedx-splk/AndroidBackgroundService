package com.splunk.android.example

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.splunk.android.example.databinding.FragmentFirstBinding
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.trace.Span


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var binding: FragmentFirstBinding? = null
    private var splunkRum: SplunkRum? = null
    var sessionId = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        splunkRum = SplunkRum.getInstance()
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.firstFragment = this

        val http = HttpBuddy(splunkRum!!)
        binding!!.buttonService.setOnClickListener {
            val intent = Intent(context, BackgroundService::class.java)
            val connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    Log.d(LOG_TAG, "Service connected!")
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    Log.d(LOG_TAG, "Service connected!")
                }
            }
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            activity?.startService(intent)
        }

        binding!!.buttonJobIntent.setOnClickListener {
            Log.d(LOG_TAG, "Starting job intent service....")
            val intent = Intent(context, BackgroundJobIntent::class.java)
            BackgroundJobIntent.enqueueWork(context!!, intent)
            Log.d(LOG_TAG, "Probably started.")
        }

        binding!!.buttonHttp.setOnClickListener {
            Log.d(LOG_TAG, "Doing a click - pid = ${android.os.Process.myPid()}")
            val workflow: Span = splunkRum!!.startWorkflow("Doing a click")
            http.makeCall("https://pmrum.o11ystore.com?user=me&pass=secret123secret", workflow)
        }

        sessionId.postValue(splunkRum!!.rumSessionId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}


package com.splunk.android.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.splunk.android.example.databinding.FragmentFirstBinding
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.trace.Span


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var splunkRum: SplunkRum? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        splunkRum = SplunkRum.getInstance()
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val http = HttpBuddy(splunkRum!!);
        binding.buttonService.setOnClickListener {
            val intent = Intent(context, BackgroundService::class.java)
//            startService(intent)

//            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.buttonHttp.setOnClickListener {
            Log.d(LOG_TAG, "Doing a click");
            val workflow: Span = splunkRum!!.startWorkflow("Doing a click")
            http.makeCall("https://pmrum.o11ystore.com?user=me&pass=secret123secret", workflow)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


package com.splunk.android.example

import android.util.Log
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import okhttp3.*
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import org.apache.http.conn.ssl.SSLSocketFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private val trustAllCerts = arrayOf<TrustManager>(
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
)

class HttpBuddy(rum: SplunkRum) {

    private val http: Call.Factory

    init {
        hackSslContext()
        http = buildOkHttpClient(rum)
    }

    fun makeCall(url: String, span: Span) {
        // make sure the span is in the current context so it can be propagated into the async call.
        span.makeCurrent().use {
            val call: Call = http.newCall(Request.Builder().url(url).get().build())
            call.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(LOG_TAG, "ERROR while making http call ${e.message}")
                        span.setStatus(
                            StatusCode.ERROR,
                            "failure to communicate"
                        )
                        span.end()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body.use {
                            Log.d(LOG_TAG, "Got HTTP response code: ${response.code}")
                            span.end()
                        }
                    }
                })
        }
    }
}


// Much like the comment above, you should NEVER do something like this in production, it
// is an extremely bad practice. This is only present because the demo endpoint uses a
// self-signed SSL cert.
fun hackSslContext() {
    try {
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun buildOkHttpClient(splunkRum: SplunkRum): Call.Factory {
    // grab the default executor service that okhttp uses, and wrap it with one that will
    // propagate the otel context.
    val builder = OkHttpClient.Builder()
    return try {
        // NOTE: This is really bad and dangerous. Don't ever do this in the real world.
        // it's only necessary because the demo endpoint uses a self-signed SSL cert.
        val sslContext = SSLContext.getInstance(SSLSocketFactory.SSL)
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        splunkRum.createRumOkHttpCallFactory(
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(AllowAllHostnameVerifier())
                .build()
        )
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        splunkRum.createRumOkHttpCallFactory(builder.build())
    } catch (e: KeyManagementException) {
        e.printStackTrace()
        splunkRum.createRumOkHttpCallFactory(builder.build())
    }
}
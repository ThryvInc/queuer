package com.thryvinc.thux.network

import android.app.AlertDialog
import android.content.Context
import com.android.volley.VolleyError

interface NetworkErrorFunctionProvider {
    fun errorFunction(): (VolleyError?) -> Unit
}

class PrintingNetworkErrorFunctionProvider: NetworkErrorFunctionProvider {
    override fun errorFunction(): (VolleyError?) -> Unit {
        return { error: VolleyError? ->
            error?.printStackTrace()
        }
    }
}

class DebugNetworkErrorFunctionProvider(val context: Context?): NetworkErrorFunctionProvider {
    override fun errorFunction(): (VolleyError?) -> Unit {
        return { error: VolleyError? ->
            val title: String
            val text: String?
            val networkResponse = error?.networkResponse
            if (networkResponse != null && networkResponse.statusCode > 299) {
                title = "Server Error ${networkResponse.statusCode}"
                text = "Message: ${error.message}"
            } else {
                title = "Error"
                text = error?.message
            }
            AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton("Ok",{ dialog, _ -> dialog.dismiss() })
                    .create()
                    .show()
        }
    }
}

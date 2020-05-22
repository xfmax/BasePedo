package com.base.basepedo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * DO NOT do anything in this Receiver!<br></br>
 *
 * Created by Mars on 12/24/15.
 */
class Receiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "receiver2 onReceive")
    }

    companion object {
        const val TAG: String = "Receiver2"
    }
}

package com.base.basepedo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * DO NOT do anything in this Service!<br></br>
 *
 *
 * Created by Mars on 12/24/15.
 */
class DaemonService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}

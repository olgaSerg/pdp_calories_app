package com.example.pdp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NetworkChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isCharging: Boolean = intent.action == Intent.ACTION_POWER_CONNECTED

        if (isCharging) {
            Toast.makeText(context, "Device is charging", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Device is not charging", Toast.LENGTH_SHORT).show()
        }
    }
}
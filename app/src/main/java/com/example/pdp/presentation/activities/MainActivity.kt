package com.example.pdp.presentation.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.example.pdp.R
import com.example.pdp.databinding.ActivityMainBinding
import com.example.pdp.presentation.base.BaseActivity
import com.example.pdp.services.ChargingMonitoringReceiver

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var receiver: ChargingMonitoringReceiver? = null

    override fun inflateBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            if (it.action == Intent.ACTION_VIEW) {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.handleDeepLink(it)
            }
        }
    }

    private fun registerReceiver() {
        receiver = ChargingMonitoringReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        receiver = null
        super.onDestroy()
    }
}
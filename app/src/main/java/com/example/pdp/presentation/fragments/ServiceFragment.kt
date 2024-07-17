package com.example.pdp.presentation.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.pdp.R
import com.example.pdp.databinding.FragmentServiceBinding
import com.example.pdp.presentation.base.BaseFragment
import com.example.pdp.services.ActivityMonitoringService

class ServiceFragment : BaseFragment<FragmentServiceBinding>(), ActivityMonitoringService.ActivityChangeCallback {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var monitoringService: ActivityMonitoringService? = null
    private var isBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ActivityMonitoringService.LocalBinder
            monitoringService = binder.getService()
            isBound = true
            binder.setCallback(this@ServiceFragment)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentServiceBinding {
        return FragmentServiceBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                bindAndStartService()
            } else {
                Toast.makeText(context, "Activity recognition permission is required for this app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            bindAndStartService()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        monitoringService = null
        stopService()
        super.onDestroyView()
    }

    override fun onActivityChanged(activityType: String) {
        Toast.makeText(context, getString(R.string.current_activity, activityType), Toast.LENGTH_SHORT).show()
    }

    private fun bindAndStartService() {
        Intent(requireActivity(), ActivityMonitoringService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
            requireActivity().startForegroundService(intent)
        }
    }

    private fun stopService() {
        Intent(requireActivity(), ActivityMonitoringService::class.java).also { intent ->
            requireActivity().stopService(intent)
        }
    }
}
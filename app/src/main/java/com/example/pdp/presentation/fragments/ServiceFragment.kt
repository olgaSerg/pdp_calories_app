package com.example.pdp.presentation.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pdp.R
import com.example.pdp.databinding.FragmentServiceBinding
import com.example.pdp.services.ActivityMonitoringService

private const val ACTIVITY = "Текущая активность:"

class ServiceFragment : Fragment(R.layout.fragment_service) {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private var monitoringService: ActivityMonitoringService? = null
    private var isBound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCheckActivity.setOnClickListener {
            checkActivityType()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ActivityMonitoringService.LocalBinder
            monitoringService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(activity, ActivityMonitoringService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            activity?.unbindService(connection)
            isBound = false
        }
    }

    private fun checkActivityType() {
        if (isBound) {
            val activityType = monitoringService?.getActivityType()
            Toast.makeText(context, "$ACTIVITY $activityType", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        monitoringService = null
        super.onDestroyView()
    }
}
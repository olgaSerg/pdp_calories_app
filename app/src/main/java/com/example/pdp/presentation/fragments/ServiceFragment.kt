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

class ServiceFragment : Fragment(R.layout.fragment_service) {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private var mService: ActivityMonitoringService? = null
    private var mBound: Boolean = false

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
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
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
        if (mBound) {
            activity?.unbindService(connection)
            mBound = false
        }
    }

    private fun checkActivityType() {
        if (mBound) {
            val activityType = mService?.getActivityType()
            Toast.makeText(context, "Текущая активность: $activityType", Toast.LENGTH_SHORT).show()
        }
    }
}
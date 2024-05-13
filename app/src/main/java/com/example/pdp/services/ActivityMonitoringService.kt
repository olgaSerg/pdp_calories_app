package com.example.pdp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import com.example.pdp.R
import kotlin.math.sqrt

class ActivityMonitoringService : Service(), SensorEventListener {

    private val binder = LocalBinder()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var activityType: String

    inner class LocalBinder : Binder() {
        fun getService(): ActivityMonitoringService = this@ActivityMonitoringService
    }

    override fun onCreate() {
        super.onCreate()
        activityType = getString(R.string.unknown_activity)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                analyzeAcceleration(it.values[0], it.values[1], it.values[2])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun analyzeAcceleration(x: Float, y: Float, z: Float) {
        val gravity = 9.81
        val magnitude = sqrt((x * x + y * y + z * z).toDouble()) - gravity

        activityType = when {
            magnitude < 2 -> getString(R.string.rest)
            magnitude < 5 -> getString(R.string.walk)
            else -> getString(R.string.run)
        }
    }

    fun getActivityType(): String {
        return activityType
    }
}
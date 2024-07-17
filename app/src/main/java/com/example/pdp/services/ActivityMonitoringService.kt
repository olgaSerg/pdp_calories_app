package com.example.pdp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.pdp.R
import kotlin.math.sqrt

private const val CHANNEL_ID = "ActivityMonitoringServiceChannel"
private const val NOTIFICATION_ID = 1
private const val SERVICE_FRAGMENT_KEY = "fragmentTag"

class ActivityMonitoringService : Service(), SensorEventListener {

    private val binder = LocalBinder()
    private val sensorManager: SensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var accelerometer: Sensor? = null
    private var activityType = ""
    private var callback: ActivityChangeCallback? = null
    private var pendingIntent: PendingIntent? = null

    inner class LocalBinder : Binder() {

        fun getService(): ActivityMonitoringService = this@ActivityMonitoringService

        fun setCallback(callback: ActivityChangeCallback) {
            this@ActivityMonitoringService.callback = callback
        }
    }

    override fun onCreate() {
        super.onCreate()
        activityType = getString(R.string.unknown_activity)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        pendingIntent = createPendingIntent()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun createNotification(): Notification {
        createNotificationChannelIfNeeded()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.activity_monitoring_service_title))
            .setContentText(getString(R.string.activity_monitoring_service_subtitle))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.activity_monitoring_service_title))
            .setContentText(getString(R.string.current_activity_content, activityType))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
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
        val magnitude = calculateMagnitude(x, y, z)

        val newActivityType = determineActivityType(magnitude)

        if (newActivityType != activityType) {
            activityType = newActivityType
            callback?.onActivityChanged(activityType)
            updateNotification()
        }
    }

    private fun createPendingIntent(): PendingIntent {
        return NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.service_fragment)
            .setArguments(Bundle().apply {
                putString(SERVICE_FRAGMENT_KEY, getString(R.string.service_fragment_value))
            })
            .createPendingIntent()
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Activity Monitoring Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun calculateMagnitude(x: Float, y: Float, z: Float): Double {
        val gravity = 9.81
        return sqrt((x * x + y * y + z * z).toDouble()) - gravity
    }

    private fun determineActivityType(magnitude: Double): String {
        return when {
            magnitude < 2 -> getString(R.string.rest)
            magnitude < 5 -> getString(R.string.walk)
            else -> getString(R.string.run)
        }
    }

    interface ActivityChangeCallback {
        fun onActivityChanged(activityType: String)
    }
}
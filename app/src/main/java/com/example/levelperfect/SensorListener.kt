package com.example.levelperfect

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class SensorListener(context: Context, private val callback: TiltCallback) : SensorEventListener {

    interface TiltCallback {
        fun onTiltChanged(isAligned: Boolean)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    init {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]  // Left-Right Tilt (Roll)
            val y = it.values[1]  // Forward-Backward Tilt (Pitch) - IGNORED

            // ✅ Check if phone is in portrait or landscape
            val isPortrait = abs(y) > abs(x) // If Y has more tilt, it's portrait mode

            // ✅ Portrait Mode (0°) → ±1.0° leeway
            // ✅ Landscape Mode (±9.8°) → Even stricter ±0.3° leeway
            val isAligned = if (isPortrait) abs(x) < 1.0 else abs(abs(x) - 9.8) < 0.3

            callback.onTiltChanged(isAligned)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}

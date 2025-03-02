package com.example.levelperfect

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class FloatingIndicatorService : Service(), SensorListener.TiltCallback {

    private lateinit var windowManager: WindowManager
    private lateinit var indicatorView: ImageView
    private lateinit var perfectTextView: TextView
    private lateinit var sensorListener: SensorListener

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Create floating indicator (Dot)
        indicatorView = ImageView(this)
        indicatorView.setImageResource(android.R.drawable.presence_online)
        indicatorView.setColorFilter(Color.RED) // Default red

        val indicatorParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        indicatorParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        indicatorParams.y = 50 // Adjust position

        // Create floating text ("Perfect")
        perfectTextView = TextView(this)
        perfectTextView.text = "Perfect"
        perfectTextView.setTextColor(Color.WHITE)
        perfectTextView.setBackgroundColor(Color.BLACK)
        perfectTextView.textSize = 18f
        perfectTextView.setPadding(20, 10, 20, 10)
        perfectTextView.visibility = TextView.INVISIBLE // Hidden by default

        val textParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        textParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        textParams.y = 100 // Below the indicator

        // Add views to window manager
        windowManager.addView(indicatorView, indicatorParams)
        windowManager.addView(perfectTextView, textParams)

        // ✅ Start sensor listener
        sensorListener = SensorListener(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // ✅ Properly remove overlay views
        if (::windowManager.isInitialized) {
            if (::indicatorView.isInitialized) windowManager.removeView(indicatorView)
            if (::perfectTextView.isInitialized) windowManager.removeView(perfectTextView)
        }

        // ✅ Stop SensorListener to free resources
        if (::sensorListener.isInitialized) {
            sensorListener.unregister()
        }
    }

    override fun onTiltChanged(isAligned: Boolean) {
        if (isAligned) {
            indicatorView.setColorFilter(Color.GREEN)
            perfectTextView.visibility = TextView.VISIBLE // Show "Perfect"
        } else {
            indicatorView.setColorFilter(Color.RED)
            perfectTextView.visibility = TextView.INVISIBLE // Hide "Perfect"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

package com.example.runpickapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class WebAppInterface(private val activity: MainActivity) {

    private val context: Context = activity

    // ... other methods

    @JavascriptInterface
    fun setDarkMode(mode: Int) {
        activity.runOnUiThread {
            AppCompatDelegate.setDefaultNightMode(when (mode) {
                0 -> AppCompatDelegate.MODE_NIGHT_NO
                1 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            })
        }
    }

    @JavascriptInterface
    fun setBrightness(brightnessLevel: Float) {
        activity.runOnUiThread {
            if (Settings.System.canWrite(context)) {
                val adjustedLevel = if (brightnessLevel > 1.0f) brightnessLevel / 100.0f else brightnessLevel
                val layoutParams = activity.window.attributes
                layoutParams.screenBrightness = adjustedLevel.coerceIn(0.0f, 1.0f)
                activity.window.attributes = layoutParams
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                activity.startActivity(intent)
            }
        }
    }

    @JavascriptInterface
    fun getDeviceBrightness(): Float {
        return try {
            val brightnessValue = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            (brightnessValue / 255.0f) * 100.0f
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            -1.0f
        }
    }

    @JavascriptInterface
    fun setTextSize(percentage: Int) {
        activity.runOnUiThread {
            activity.findViewById<WebView>(R.id.webView).settings.textZoom = percentage
        }
    }

    @JavascriptInterface
    fun openCamera() {
        activity.runOnUiThread {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                activity.startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                activity.requestCameraPermission()
            }
        }
    }

    @JavascriptInterface
    fun openAccessibilitySettings() {
        activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    @JavascriptInterface
    fun requestLocation() {
        activity.requestLocationPermission()
    }

    @JavascriptInterface
    fun setKeepScreenOn(enabled: Boolean) {
        activity.runOnUiThread {
            if (enabled) activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @JavascriptInterface
    fun performHapticFeedback() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    @JavascriptInterface
    fun setRotation(mode: String) {
        activity.requestedOrientation = when (mode.lowercase()) {
            "portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    @JavascriptInterface
    fun openBatteryOptimizationSettings() {
        activity.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
    }

    @JavascriptInterface
    fun openWifiSettings() {
        activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    @JavascriptInterface
    fun clearCache() {
        activity.runOnUiThread { activity.findViewById<WebView>(R.id.webView).clearCache(true) }
    }

    @JavascriptInterface
    fun showLocalNotification(title: String, message: String) {
        val channelId = "local_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId, "Local Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        notificationManager.notify(1, notificationBuilder.build())
    }

    @JavascriptInterface
    fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(context)
        val result = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "SUCCESS"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "NO_HARDWARE"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "HW_UNAVAILABLE"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "NONE_ENROLled"
            else -> "UNKNOWN"
        }
        activity.runOnUiThread {
            activity.findViewById<WebView>(R.id.webView).evaluateJavascript("javascript: onBiometricSupportResult('$result');", null)
        }
    }

    @JavascriptInterface
    fun requestBiometricAuth() {
        activity.runOnUiThread {
            activity.showBiometricPrompt()
        }
    }

    @JavascriptInterface
    fun sendSms(phoneNumber: String, message: String) {
        activity.runOnUiThread {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", message)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                activity.startActivity(intent)
            }
        }
    }

    @JavascriptInterface
    fun startSmsOtpListener() {
        activity.runOnUiThread {
            activity.startSmsUserConsent()
        }
    }
}

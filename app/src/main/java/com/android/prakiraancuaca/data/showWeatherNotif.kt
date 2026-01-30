package com.android.prakiraancuaca.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.prakiraancuaca.adapter.getWeatherIcon
import com.android.prakiraancuaca.model.Cuaca

fun showWeatherNotif(context: Context, cuaca: Cuaca) {

    val channelId = "cuaca_channel"

    // Android 8+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Info Cuaca",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    val icon = getWeatherIcon(cuaca.weather_desc)

    val notif = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(icon)
        .setContentTitle("Cuaca 3 Jam Lagi")
        .setContentText("${cuaca.weather_desc}, ${cuaca.t}°C")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }
    NotificationManagerCompat.from(context).notify(1001, notif)
}

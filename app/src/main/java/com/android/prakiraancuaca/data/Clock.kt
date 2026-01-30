package com.android.prakiraancuaca.data

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Looper
import android.widget.TextView
import java.util.Date
import java.util.Locale

fun Clock(tvDate: TextView, tvTime: TextView) {
    val handler = android.os.Handler(Looper.getMainLooper())

    val runnable = object : Runnable {
        override fun run() {
            val now = Date()

            val dateFormat = SimpleDateFormat(
                "EEEE, dd / MM / yyyy",
                Locale("id", "ID")
            )
            val timeFormat = SimpleDateFormat(
                "HH : mm : ss",
                Locale("id", "ID")
            )

            val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            dateFormat.timeZone = timeZone
            timeFormat.timeZone = timeZone

            tvDate.text = dateFormat.format(now)
            tvTime.text = timeFormat.format(now)

            handler.postDelayed(this, 1000)
        }
    }

    handler.post(runnable)
}

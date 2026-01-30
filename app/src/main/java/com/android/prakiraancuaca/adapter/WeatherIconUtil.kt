package com.android.prakiraancuaca.adapter

import com.android.prakiraancuaca.R

fun getWeatherIcon(desc: String): Int {
    return when {
        desc.contains("Cerah Berawan", true) ->
            R.drawable.cerah_berawan

        desc.contains("Hujan Ringan", true) ->
            R.drawable.hujan_ringan

        desc.contains("Hujan Sedang", true) ->
            R.drawable.hujan_sedang

        desc.contains("Petir", true) ->
            R.drawable.badai_petir

        desc.contains("Cerah", true) ->
            R.drawable.cerah

        desc.contains("Berawan", true) ->
            R.drawable.berawan

        else ->
            R.drawable.berawan
    }
}

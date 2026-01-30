package com.android.prakiraancuaca.model

data class BmkgResponse(
    val lokasi: Lokasi,
    val data: List<DataCuaca>
)

data class Lokasi(
    val provinsi: String,
    val kotkab: String,
    val kecamatan: String,
    val desa: String
)

data class DataCuaca(
    val cuaca: List<List<Cuaca>>
)

data class Cuaca(
    val local_datetime: String,
    val t: Int,
    val hu: Int,
    val ws: Double,
    val weather_desc: String,
    val image: String?
)

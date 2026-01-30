package com.android.prakiraancuaca

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.prakiraancuaca.adapter.ForecastAdapter
import com.android.prakiraancuaca.adapter.getWeatherIcon
import com.android.prakiraancuaca.data.Clock
import com.android.prakiraancuaca.model.BmkgResponse
import com.android.prakiraancuaca.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tvLocation: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvDesc: TextView
    private lateinit var rv: RecyclerView
    private lateinit var imgWeather: ImageView
    private lateinit var tvExtra: TextView
    private lateinit var tvKelembaban: TextView
    private lateinit var tvAngin: TextView
    private lateinit var tvDateOnly: TextView
    private lateinit var tvTimeOnly: TextView

    private val adm4UpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.android.prakiraancuaca.ADM4_UPDATED") {
                loadWeatherData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLocation = findViewById(R.id.tvLocation)
        tvTemp = findViewById(R.id.tvTemp)
        tvDesc = findViewById(R.id.tvDesc)
        rv = findViewById(R.id.rvForecast)
        imgWeather = findViewById(R.id.imgWeather)
        tvExtra = findViewById(R.id.tvExtra2)
        tvKelembaban = findViewById(R.id.tvKelembaban)
        tvAngin = findViewById(R.id.tvAngin)
        tvDateOnly = findViewById(R.id.tvDateOnly)
        tvTimeOnly = findViewById(R.id.tvTimeOnly)

        val btnPlus = findViewById<ImageView>(R.id.btnPlus)
        btnPlus.setOnClickListener {
            val intent = Intent(this, AddRegionActivity::class.java)
            startActivity(intent)
        }

        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val url = "https://cuaca.bmkg.go.id/"

        val btnMoreDetails = findViewById<TextView>(R.id.btnMoreDetails)
        btnMoreDetails.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val btnCheckInfo = findViewById<TextView>(R.id.btnCheckInfo)
        btnCheckInfo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        Clock(tvDateOnly, tvTimeOnly)

        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        loadWeatherData() // Panggil saat onCreate
    }

    override fun onResume() {
        super.onResume()
        // Register receiver saat activity aktif
        registerReceiver(adm4UpdateReceiver, IntentFilter("com.android.prakiraancuaca.ADM4_UPDATED"))
        loadWeatherData() // Pastikan data terbaru saat kembali ke MainActivity
    }

    override fun onPause() {
        super.onPause()
        // Unregister receiver saat activity tidak aktif
        unregisterReceiver(adm4UpdateReceiver)
    }

    private fun loadWeatherData() {
        val sharedPref = getSharedPreferences("WeatherAppPref", Context.MODE_PRIVATE)
        val currentAdm4 = sharedPref.getString("ADM4_CODE", "32.08.09.1002") // Default jika belum ada

        if (currentAdm4.isNullOrBlank()) {
            Toast.makeText(this, "Kode ADM4 tidak ditemukan, gunakan default.", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.api.getCuaca(currentAdm4).enqueue(object : Callback<BmkgResponse> {
            override fun onResponse(
                call: Call<BmkgResponse>,
                response: Response<BmkgResponse>
            ) {
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    return
                }

                val lokasi = body.lokasi
                val cuacaList = body.data.firstOrNull()?.cuaca?.flatten()

                val current = cuacaList?.firstOrNull()
                if (cuacaList == null || current == null) {
                    Toast.makeText(this@MainActivity, "Data cuaca tidak tersedia", Toast.LENGTH_SHORT).show()
                    return
                }

                // Menampilkan nama kecamatan
                tvLocation.text = lokasi.kecamatan

                tvTemp.text = "${current.t}"
                tvDesc.text = current.weather_desc

                rv.adapter = ForecastAdapter(cuacaList.take(8))

                imgWeather.setImageResource(
                    getWeatherIcon(current.weather_desc)
                )
                tvExtra.text = current.weather_desc
                tvKelembaban.text = "Kelembaban         ${current.hu}%"
                tvAngin.text = "Angin                     ${current.ws} m/s"
            }

            override fun onFailure(call: Call<BmkgResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal mengambil data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package com.android.prakiraancuaca

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class AddRegionActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var tvCurrentLocationDisplay: TextView
    private lateinit var tvSelectedRegion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_region)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvCurrentLocationDisplay = findViewById(R.id.tvCurrentLocationDisplay)
        tvSelectedRegion = findViewById(R.id.tvSelectedRegion)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadSavedAdm4()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Izin lokasi tidak diberikan.",
                    Toast.LENGTH_SHORT
                ).show()
                tvCurrentLocationDisplay.text = getString(R.string.activate_hint)
            }
        }

        findViewById<LinearLayout>(R.id.btnCurrentLocation).setOnClickListener {
            checkLocationPermissionsAndGetLocation()
        }

        findViewById<LinearLayout>(R.id.btnInputRegion).setOnClickListener {
            showAdm4InputDialog()
        }

        findViewById<LinearLayout>(R.id.btnCekRegion).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://kodewilayah.id/")
            startActivity(intent)
        }

        checkLocationPermissionsAndGetLocation()
    }

    private fun loadSavedAdm4() {
        val sharedPref = getSharedPreferences("WeatherAppPref", MODE_PRIVATE)
        val savedAdm4 = sharedPref.getString("ADM4_CODE", null)
        savedAdm4?.let { tvSelectedRegion.text = it }
    }

    private fun showAdm4InputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_input_region_title))

        val input = EditText(this)
        input.hint = getString(R.string.dialog_input_region_message)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.dialog_save)) { dialog, _ ->
            val newAdm4 = input.text.toString().trim()
            if (newAdm4.isNotBlank()) {
                saveAdm4(newAdm4)
                tvSelectedRegion.text = newAdm4
                Toast.makeText(this, "Kode ADM4 disimpan: $newAdm4", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Kode ADM4 tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveAdm4(adm4Code: String) {
        val sharedPref = getSharedPreferences("WeatherAppPref", MODE_PRIVATE)
        sharedPref.edit {
            putString("ADM4_CODE", adm4Code)
        }
        sendBroadcast(Intent("com.android.prakiraancuaca.ADM4_UPDATED"))
    }

    private fun checkLocationPermissionsAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        tvCurrentLocationDisplay.text = getString(R.string.searching_location)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getAddressFromLocation(location)
                } else {
                    tvCurrentLocationDisplay.text = getString(R.string.activate_hint)
                    Toast.makeText(this, "Lokasi tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                tvCurrentLocationDisplay.text = getString(R.string.activate_hint)
                Toast.makeText(this, "Gagal mendapatkan lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddRegionActivity", "Error getting location", e)
            }
    }

    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locality = address.locality ?: address.subLocality ?: address.adminArea
                tvCurrentLocationDisplay.text = locality ?: "Lokasi tidak diketahui"
            } else {
                tvCurrentLocationDisplay.text = "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            tvCurrentLocationDisplay.text = "Gagal mendapatkan alamat"
            Log.e("AddRegionActivity", "Error getting address", e)
        }
    }
}

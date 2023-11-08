package com.example.submissionawalstoryapp.ui.maps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.databinding.ActivityPickLocationBinding
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.utils.Helper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPickLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateButtonStyle(binding.btnCurrentLocation, false)
        updateButtonStyle(binding.btnMarkedLocation, false)
        setActions()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        getMyLastLocation()

        mMap.setOnMapClickListener {
            pickedPlace = it
            val markerOptions = MarkerOptions()
            markerOptions.position(it)

            markerOptions.title(Helper.getStringAddress(it, this))
            mMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 15f
            )
            mMap.animateCamera(location)
            mMap.addMarker(markerOptions)
            updateButtonStyle(binding.btnMarkedLocation, true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLagLng = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    updateButtonStyle(binding.btnCurrentLocation, true)
                    mMap.isMyLocationEnabled = true
                    showStartMarker(location)
                } else {
                    updateButtonStyle(binding.btnCurrentLocation, false)
                    CustomDialog(
                        this@PickLocationActivity,
                        getString(R.string.location_not_found),
                        R.raw.error_anim).show()

                    mMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation(), DEFAULT_ZOOM)
                    )
                    mMap.isMyLocationEnabled = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun updateButtonStyle(button: Button, isEnabled: Boolean) {
        if (isEnabled) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.xanthous))
            button.isEnabled = true
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_skin))
            button.isEnabled = false
        }
    }

    private fun setActions() {
        binding.btnCurrentLocation.setOnClickListener {
            showAlertDialog(currentLagLng)
        }

        binding.btnMarkedLocation.setOnClickListener {
            showAlertDialog(pickedPlace)
        }
    }

    private fun showAlertDialog(latlng: LatLng?) {
        val address = Helper.getStringAddress(latlng, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(resources.getString(R.string.used_location))
            .setMessage(address)
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                returnLocationResult(address, latlng)
            }
            .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun returnLocationResult(address: String, latlng: LatLng?) {
        val resultIntent = Intent()
        resultIntent.putExtra("address", address)
        resultIntent.putExtra("lat", latlng?.latitude)
        resultIntent.putExtra("lng", latlng?.longitude)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun defaultLocation() = LatLng(-34.0, 151.0)

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(getString(R.string.start_point))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, DEFAULT_ZOOM))
    }

    companion object {
        var currentLagLng: LatLng? = null
        var pickedPlace: LatLng? = null
        const val DEFAULT_ZOOM = 15.0f
    }
}
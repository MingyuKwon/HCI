package com.example.hci

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Locale

class AlarmActivateActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var map: FrameLayout
    private lateinit var locationManager: LocationManager
    private lateinit var loadingDialog: AlertDialog
    private lateinit var DistanceShowText: TextView
    private lateinit var CurrentDestinationText: TextView


    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var currentMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var polyline: Polyline? = null

    private var isInitalLocationGetted: Boolean = false

    private var _currentLatLng: LatLng? = null
    private var _currentAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alarm_activate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        map = findViewById(R.id.alarmMap)
        DistanceShowText = findViewById(R.id.DistanceShowText)
        CurrentDestinationText = findViewById(R.id.CurrentDestinationText)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.alarmMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        createLoadingDialog() // 로딩 다이얼로그 생성
        showLoadingDialog() // 초기 로딩 다이얼로그 표시


        CurrentDestinationText.text = "목적지 :\n\n${Data.DestinationLocationAddress}"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            enableMyLocation()
        }
    }

    private fun createLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false) // 사용자가 다이얼로그를 닫을 수 없도록 설정
        loadingDialog = builder.create()
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            lastKnownLocation?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                getAddressFromLatLng(currentLatLng) // 위도와 경도로부터 주소 가져오기
            }

        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                return address
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }

    private fun updateMarker(latLng: LatLng) {
        if (!isInitalLocationGetted) {
            destinationMarker = gMap.addMarker(
                MarkerOptions()
                    .position(Data.DestinationLocationLatng!!)
                    .title("Destination Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            isInitalLocationGetted = true
            hideLoadingDialog()
        }

        if (currentMarker == null) {
            currentMarker = gMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        } else {
            currentMarker?.position = latLng
        }

        val distance = calculateDistance(currentMarker!!.position, destinationMarker!!.position)

        DistanceShowText.text = "$distance m 남음"
        updatePolyline()
    }

    private fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val location1 = Location("").apply {
            latitude = latLng1.latitude
            longitude = latLng1.longitude
        }
        val location2 = Location("").apply {
            latitude = latLng2.latitude
            longitude = latLng2.longitude
        }
        return location1.distanceTo(location2)
    }

    private fun updatePolyline() {
        polyline?.remove()

        polyline = gMap.addPolyline(
            PolylineOptions()
                .add(currentMarker!!.position, destinationMarker!!.position)
                .width(7f)
                .color(ContextCompat.getColor(this, R.color.green))
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            if (!isInitalLocationGetted) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
            updateMarker(currentLatLng)
            getAddressFromLatLng(currentLatLng) // 위도와 경도로부터 주소 가져오기
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}

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
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
import java.util.Locale

class MapActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var gMap: GoogleMap
    private lateinit var map: FrameLayout
    private lateinit var locationManager: LocationManager
    private lateinit var loadingDialog: AlertDialog


    private lateinit var backButton: Button
    private lateinit var OkButton: Button

    private lateinit var searchButton: ImageButton
    private lateinit var searchText: EditText

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var currentMarker: Marker? = null
    private var locationText: TextView? = null

    private var _currentLatLng : LatLng? = null
    private var _currentAddress : String? = null

    private var isInitalLocationGetted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        map = findViewById(R.id.map)
        locationText = findViewById(R.id.settingLocationDescription)


        backButton = findViewById(R.id.GoBackButton_Map)
        OkButton = findViewById(R.id.DestinationSetPageOKButton)
        searchButton = findViewById(R.id.SearchButton)
        searchText = findViewById(R.id.SearchText)


        backButton.setOnClickListener {
            finish()
        }

        OkButton.setOnClickListener {
            finish()
            if(_currentLatLng != null && _currentAddress != null)
            {
                Data.DestinationLocationLatng = _currentLatLng
                Data.DestinationLocationAddress = _currentAddress
            }
        }

        searchButton.setOnClickListener {
            val address = searchText.text.toString()
            if (address.isNotEmpty()) {
                searchAddressAndMoveCamera(address)
            } else {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        createLoadingDialog() // 로딩 다이얼로그 생성
        showLoadingDialog() // 초기 로딩 다이얼로그 표시
    }

    private fun searchAddressAndMoveCamera(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                _currentLatLng = latLng
                _currentAddress = location.getAddressLine(0)

                updateMarker(latLng)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                locationText?.text = _currentAddress
            } else {
                searchText.text.clear()
                Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            searchText.text.clear()
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMapClickListener(this) // 맵 클릭 리스너 설정

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            enableMyLocation()
        }
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

    private fun updateMarker(latLng: LatLng) {
        if (!isInitalLocationGetted) {
            gMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)) // 마커 색상 설정
            )
            isInitalLocationGetted = true
            hideLoadingDialog()
        } else {
            currentMarker?.remove() // 이전 마커 제거
            currentMarker = gMap.addMarker(MarkerOptions().position(latLng).title("Selected Location")) // 새 마커 추가
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng) : String? {
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

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            updateMarker(currentLatLng)
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            locationManager.removeUpdates(this)
            getAddressFromLatLng(currentLatLng) // 위도와 경도로부터 주소 가져오기
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onMapClick(latLng: LatLng) {
        updateMarker(latLng)
        _currentLatLng = latLng
        _currentAddress = getAddressFromLatLng(latLng)
        locationText?.text = _currentAddress

        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}

package com.example.hci

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var showmapButton: Button
    lateinit var SettingButton: Button
    private lateinit var FavoriteSpinner: Spinner
    lateinit var DestinationShowText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FavoriteSpinner = findViewById(R.id.FavoriteSpinner)
        showmapButton = findViewById(R.id.ShowMapButton)
        SettingButton = findViewById(R.id.ShowSettingButton)
        DestinationShowText = findViewById(R.id.DestinationShowButton)

        showmapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        SettingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        SetFavoriteSpinner()

        FavoriteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != 0) { // 첫 번째 아이템을 제외
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    getCoordinatesFromAddress(selectedItem)
                    FavoriteSpinner.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onResume() {
        super.onResume()
        SetDestinationText()
    }

    private fun SetDestinationText() {
        DestinationShowText.text = Data.DestinationLocationAddress
    }

    private fun SetFavoriteSpinner() {
        val items = arrayOf("즐겨찾기", "Seoul, South Korea", "New York, USA", "Tokyo, Japan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        FavoriteSpinner.adapter = adapter
    }

    private fun getCoordinatesFromAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                Data.DestinationLocationLatng = latLng
                Data.DestinationLocationAddress = address
                SetDestinationText()
                Toast.makeText(this, "즐겨찾기 주소 설정 완료 : $address", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show()
        }
    }
}

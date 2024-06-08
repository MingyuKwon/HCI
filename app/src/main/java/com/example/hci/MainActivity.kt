package com.example.hci

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
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

    lateinit var SettingButton: Button
    lateinit var AlarmStartButton: ImageButton

    private lateinit var FavoriteSpinner: Spinner
    private lateinit var favoriteAddButton: Button
    private lateinit var favoriteDeleteButton: Button
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
        favoriteAddButton = findViewById(R.id.FavoriteAddButton)
        favoriteDeleteButton = findViewById(R.id.FavoriteDeleteButton)
        SettingButton = findViewById(R.id.ShowSettingButton)

        AlarmStartButton = findViewById(R.id.AlarmStartButton)

        DestinationShowText = findViewById(R.id.DestinationShowButton)

        DestinationShowText.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        SettingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        AlarmStartButton.setOnClickListener {

            val address = DestinationShowText.text.toString()
            if (isAddressNotEmpty()) {
                val intent = Intent(this, AlarmActivateActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "주소를 입력해야 알람 시작이 가능합니다", Toast.LENGTH_SHORT).show()
            }
        }

        SetFavoriteSpinner()

        favoriteAddButton.setOnClickListener {
            val address = DestinationShowText.text.toString()
            if (isAddressNotEmpty()) {
                addFavoriteAddress(address)
            } else {
                Toast.makeText(this, "주소를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        favoriteDeleteButton.setOnClickListener {
            val address = DestinationShowText.text.toString()
            if (isAddressNotEmpty()) {
                removeFavoriteAddress(address)
            } else {
                Toast.makeText(this, "주소를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun isAddressNotEmpty() : Boolean
    {
        val address = DestinationShowText.text.toString()
        return !(address.isEmpty() || address == "도착지를 입력하세요")
    }


    override fun onResume() {
        super.onResume()
        SetDestinationText()
    }

    private fun SetDestinationText() {
        if(Data.DestinationLocationAddress == null)
        {
            DestinationShowText.text = "도착지를 입력하세요"
        }else
        {
            DestinationShowText.text = Data.DestinationLocationAddress
        }
    }

    private fun SetFavoriteSpinner() {
        val items = mutableListOf("즐겨찾기", "120 Neungdong-ro, Gwangjin District, Seoul")
        Data.FavoritesAddress?.let {
            items.addAll(it.filterNotNull())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        FavoriteSpinner.adapter = adapter
    }

    private fun getCoordinatesFromAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && isAddressNotEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                Data.DestinationLocationLatng = latLng
                Data.DestinationLocationAddress = address
                SetDestinationText()
                Toast.makeText(this, "즐겨찾기 주소 설정 완료 : $address", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "지오코딩 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFavoriteAddress(address: String) {
        if (Data.FavoritesAddress == null) {
            Data.FavoritesAddress = arrayOfNulls(0)
        }

        if (Data.FavoritesAddress?.contains(address) == true) {
            Toast.makeText(this, "이미 즐겨찾기에 있는 주소입니다", Toast.LENGTH_SHORT).show()
            return
        }

        Data.FavoritesAddress = Data.FavoritesAddress?.plus(address)
        Toast.makeText(this, "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show()
        SetFavoriteSpinner()
    }

    private fun removeFavoriteAddress(address: String) {
        if (Data.FavoritesAddress == null || !Data.FavoritesAddress!!.contains(address)) {
            Toast.makeText(this, "즐겨찾기에서 찾을 수 없는 주소입니다", Toast.LENGTH_SHORT).show()
            return
        }

        Data.FavoritesAddress = Data.FavoritesAddress?.filterNot { it == address }?.toTypedArray()
        Toast.makeText(this, "즐겨찾기에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
        SetFavoriteSpinner()
    }
}

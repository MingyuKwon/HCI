package com.example.hci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var showmapButton : Button
    lateinit var SettingButton : Button


    lateinit var DestinationShowText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showmapButton = findViewById<Button>(R.id.ShowMapButton)
        showmapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        SettingButton = findViewById(R.id.ShowSettingButton)
        SettingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        DestinationShowText = findViewById(R.id.DestinationShowButton)

    }

    override fun onResume() {
        super.onResume()
        ResumecallFunction()
    }

    private fun ResumecallFunction() {
        DestinationShowText.text = Data.DestinationLocationAddress
    }




}
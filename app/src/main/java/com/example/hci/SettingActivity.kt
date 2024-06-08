package com.example.hci

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {

    // 지훈 작성 ~
    lateinit var SpinnerPreAlarm: Spinner
    lateinit var SpinnerBell: Spinner
    lateinit var SpinnerSound: Spinner
    lateinit var SpinnerBibration: Spinner

    private lateinit var SwitchPopup: Switch
    private lateinit var SwitchPreAlarm: Switch
    private lateinit var SwitchBell: Switch

    lateinit var ButtonToBack : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 지훈 작성~
        SpinnerPreAlarm = findViewById(R.id.SpinnerPreAlarm)
        SpinnerBell = findViewById(R.id.SpinnerBell)
        SpinnerSound = findViewById(R.id.SpinnerSound)
        SpinnerBibration = findViewById(R.id.SpinnerBibration)

        SwitchPopup = findViewById(R.id.SwitchPopup)
        SwitchPreAlarm = findViewById(R.id.SwitchPreAlarm)
        SwitchBell = findViewById(R.id.SwitchBell)

        ButtonToBack = findViewById(R.id.buttonToBack)


        setSpinner()
        setSwitch()
        ButtonToBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setSpinner() {
        val itemPreAlarm = arrayOf("사전 알림 설정", "50m 전 알림", "100m 전 알림")
        val itemBell = arrayOf("벨소리 설정", "Moonlight", "Sonata", "Ring Ding Dong")
        val itemSound = arrayOf("소리 세기", "1", "2", "3")
        val itemBibration = arrayOf("진동 세기", "1", "2", "3")

        val adapterPreAlarm = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemPreAlarm)
        val adapterBell = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemBell)
        val adapterSound = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemSound)
        val adapterBibration = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemBibration)

        SpinnerPreAlarm.adapter = adapterPreAlarm
        SpinnerBell.adapter = adapterBell
        SpinnerSound.adapter = adapterSound
        SpinnerBibration.adapter = adapterBibration

        SpinnerPreAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                if (position != 0){
                    if(position == 1){ // 50m 전 알림
                        Data.preAlarmDistance = 50
                    }
                    else if(position == 2){ // 100m 전 알림
                        Data.preAlarmDistance = 100
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerBell.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                if (position != 0){
                    if(position == 1){
                        Data.bellName = 1
                    }
                    else if(position == 2){
                        Data.bellName = 2
                    }
                    else if(position == 3){
                        Data.bellName = 3
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerSound.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                if (position != 0){
                    if(position == 1){
                        Data.bellSound = 1
                    }
                    else if(position == 2){
                        Data.bellSound = 2
                    }
                    else if(position == 3){
                        Data.bellSound = 3
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerBibration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                if (position != 0){
                    if(position == 1){
                        Data.bibrationMount = 1
                    }
                    else if(position == 2){
                        Data.bibrationMount = 2
                    }
                    else if(position == 3){
                        Data.bibrationMount = 3
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setSwitch(){

        SwitchPopup.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                Data.popupAlarmAvailable = true
            }
            else{
                Data.popupAlarmAvailable = false
            }
        }
        SwitchPreAlarm.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                Data.bAlarmAvailable = true
            }
            else{
                Data.bAlarmAvailable = false
            }
        }
        SwitchBell.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                Data.bellAlarmAvailable = true
            }
            else{
                Data.bellAlarmAvailable = false
            }
        }
    }

}
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
    private lateinit var SwitchVibration: Switch
    private lateinit var SwitchBell: Switch

    lateinit var ButtonToBack : Button

    val itemPreAlarm = arrayOf("100m", "300m", "500m", "1000m")
    val itemBell = arrayOf("Moonlight", "Sonata", "Ring Ding Dong")
    val itemSound = arrayOf("작음", "중간", "큼")
    val itemBibration = arrayOf("작음", "중간", "큼")

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
        SpinnerPreAlarm = findViewById(R.id.SpinnerBibration)
        SpinnerBell = findViewById(R.id.SpinnerSound)
        SpinnerSound = findViewById(R.id.SpinnerBell)
        SpinnerBibration = findViewById(R.id.SpinnerPreAlarm)

        SwitchPopup = findViewById(R.id.SwitchPopup)
        SwitchVibration = findViewById(R.id.SwitchVibration)
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

        val adapterPreAlarm = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemPreAlarm)
        val adapterBell = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemBell)
        val adapterSound = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemSound)
        val adapterBibration = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemBibration)

        SpinnerPreAlarm.adapter = adapterPreAlarm
        SpinnerBell.adapter = adapterBell
        SpinnerSound.adapter = adapterSound
        SpinnerBibration.adapter = adapterBibration


        if(Data.AlarmUnitDistance == 100.0f)
        {
            SpinnerPreAlarm.setSelection(0)
        }else if(Data.AlarmUnitDistance == 300.0f)
        {
            SpinnerPreAlarm.setSelection(1)
        }else if(Data.AlarmUnitDistance == 500.0f)
        {
            SpinnerPreAlarm.setSelection(2)
        }else
        {
            SpinnerPreAlarm.setSelection(3)
        }


        SpinnerPreAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                if(position == 0){
                    Data.AlarmUnitDistance = 100.0f
                }
                else if(position == 1){
                    Data.AlarmUnitDistance = 300.0f
                }else if(position == 2){
                    Data.AlarmUnitDistance = 500.0f
                }else if(position == 3){
                    Data.AlarmUnitDistance = 1000.0f
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerBell.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {

                Data.bellName = position

            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerBell.setSelection(Data.bellName)

        SpinnerSound.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {

                Data.bellSound = position

            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerSound.setSelection(Data.bellSound)

        SpinnerBibration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                Data.bibrationMount = position

            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        SpinnerBibration.setSelection(Data.bibrationMount)
    }

    private fun setSwitch(){

        SwitchPopup.isChecked = Data.popupAlarmAvailable
        SwitchPopup.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                Data.popupAlarmAvailable = true
            }
            else{
                Data.popupAlarmAvailable = false
            }
        }

        SwitchVibration.isChecked = Data.vibrationAvailable
        SwitchVibration.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                Data.vibrationAvailable = true
            }
            else{
                Data.vibrationAvailable = false
            }
        }

        SwitchBell.isChecked = Data.bellAlarmAvailable
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
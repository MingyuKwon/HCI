package com.example.hci

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng

class Data {

    companion object {
        var DestinationLocationLatng: LatLng? = null
        var DestinationLocationAddress: String? = null

        var FavoritesAddress : Array<String?>? = null

        var popupAlarmAvailable : Boolean = true
        var bellAlarmAvailable : Boolean = true
        var vibrationAvailable : Boolean = true

        var bellName : Int = 0
        var bellSound : Int = 1
        var bibrationMount : Int = 0
        var AlarmUnitDistance : Float? = 100.0f

        var bAlarmAvailable : Boolean = false
        var ClosetestAlarmDistance : Float? = null
        var AccepRadius : Float = 50.0f

        private const val PREFS_NAME = "UserSettings"
        private const val KEY_FAVORITES = "Favorites_address"


        fun saveFavoritesArray(context: Context, array: Array<String>) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val concatenatedString = array.joinToString(",")
            editor.putString(KEY_FAVORITES, concatenatedString)
            editor.apply()
        }

        fun loadFavoritesArray(context: Context) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val concatenatedString = sharedPreferences.getString(KEY_FAVORITES, null)
            FavoritesAddress = concatenatedString?.split(",")?.toTypedArray()
        }
    }
}

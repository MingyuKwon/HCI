package com.example.hci

import com.google.android.gms.maps.model.LatLng

class Data {

    companion object {
        var currentLocationLatng : LatLng? = null
        var currentLocationAddress : String? = null

        var DestinationLocationLatng : LatLng? = null
        var DestinationLocationAddress : String? = null
    }
}
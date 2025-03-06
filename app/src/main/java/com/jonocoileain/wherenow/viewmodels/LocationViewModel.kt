package com.jonocoileain.wherenow.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonocoileain.wherenow.data.GeocodingResults
import com.jonocoileain.wherenow.data.LocationData
import com.jonocoileain.wherenow.data.RetrofitClient
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {

    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResults>())
    val address : State<List<GeocodingResults>> = _address

    fun updateLocation(newLocation : LocationData){
        _location.value = newLocation
    }

    fun fetchAddress(latlng:String) {
        try {
            viewModelScope.launch {
                val result = RetrofitClient.createGeocodoingAPIService().getAddressFromCoordinates(
                    latlng,
                    "AIzaSyAZVr723d-MmCgXtaub6bYX3HvkIOQdmdY"
                )
                _address.value =result.results
                //Log.d("err1", " ${_address.value} ")
            }
        } catch (e:Exception){
            Log.d("err1", " ${e.cause} ${e.message}")
        }
    }
}
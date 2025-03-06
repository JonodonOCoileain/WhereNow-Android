package com.jonocoileain.wherenow.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jonocoileain.wherenow.data.BirdSighting
import com.jonocoileain.wherenow.data.OrnithologyAPIService
import com.jonocoileain.wherenow.data.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class BirdSightingsViewModel: ViewModel() {

    private var _birdSightings = mutableStateOf<List<BirdSighting>?>(null)
    val birdSightings: State<List<BirdSighting>?> = _birdSightings
    private val ornithologyAPIService: OrnithologyAPIService = RetrofitClient.createOrnithologyAPIService()

    fun fetchNotableBirdSightings(lat: String, lng: String) {
        ornithologyAPIService.getNotableSightingsUsingCoordinates(
            lat = lat, lng, "date", "100", "25", "4ubf1p4of0js"
        ).enqueue(object :
            Callback<List<BirdSighting>> {
            override fun onResponse(call: Call<List<BirdSighting>>, response: Response<List<BirdSighting>>) {
                if (response.isSuccessful) {
                    val observations = response.body()
                    println(observations.toString())
                    _birdSightings.value = observations
                } else {
                    Log.e("API_ERROR", "Response was not successful. Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<BirdSighting>>, t: Throwable) {
                Log.e("EBirdAPI", "Error fetching observations", t)
            }
        })
    }
}
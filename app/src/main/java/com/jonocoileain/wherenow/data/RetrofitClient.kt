package com.jonocoileain.wherenow.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val MAPS_BASE_URL = "https://maps.googleapis.com/"

    fun createGeocodoingAPIService(): GeocodingAPIService {
        val retrofit = Retrofit.Builder().baseUrl(MAPS_BASE_URL).
                           addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(GeocodingAPIService::class.java)
    }

    private const val BIRD_SIGHTINGS_BASE_URL = "https://api.ebird.org/"

    fun createOrnithologyAPIService(): OrnithologyAPIService {
        val cornell = Retrofit.Builder().baseUrl(BIRD_SIGHTINGS_BASE_URL).
        addConverterFactory(GsonConverterFactory.create()).build()

        return cornell.create(OrnithologyAPIService::class.java)
    }

}
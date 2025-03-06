package com.jonocoileain.wherenow.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.bodyAsText
import kotlin.collections.Map

interface OrnithologyAPIService {
    @GET("v2/data/obs/geo/recent/notable")
    fun getNotableSightingsUsingCoordinates(
        @Query("lat") lat : String,
        @Query("lng") lng : String,
        @Query("sort") sort: String,
        @Query("maxResults") maxResults: String,
        @Query("dist") dist: String,
        @Query("key") apiKey : String
    ): Call<List<BirdSighting>>
}

suspend fun performGetRequest(url: String, queryParams: Map<String, String>? = null, headers: Map<String, String>? = null): String {
    val client = HttpClient(CIO)

    val response: String = client.get(url) {
        queryParams?.forEach { (key, value) ->
            parameter(key, value)
        }
        headers {
            headers?.forEach { (key, value) ->
                append(key, value)
            }
        }
    }.bodyAsText()

    client.close()
    return response
}

suspend fun main() {
    val url = "https://example.com/api/data"
    val params = mapOf("param1" to "value1", "param2" to "value2")
    val headers = mapOf("X-Custom-Header" to "headerValue")

    val result = performGetRequest(url, params, headers)
    println(result)
}
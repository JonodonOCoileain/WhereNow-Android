package com.jonocoileain.wherenow.data


data class BirdSighting(
    val speciesCode: String,
    val comName: String,
    val sciName: String,
    val locId: String,
    val locName: String,
    val obsDt: String,
    val howMany: Int,
    val lat: Double,
    val lng: Double,
    val obsValid: Boolean,
    val obsReviewed: Boolean,
    val locationPrivate: Boolean,
    val subId: String,
    val userDisplayName: String,
) {
    fun description(): String {
        return comName + "\n" + sciName + "\n" + "Quantity: " + howMany + "\n" + "Seen by: " + userDisplayName + "\n" + "Seen at: " + locName + "\n"
    }
    fun descriptionWithoutLocation(): String {
        return comName + "\n" + sciName + "\n" + "Quantity: " + howMany + "\n" + "Seen by: " + userDisplayName
    }
}

data class BirdSightingsResponse(
    val result: List<BirdSighting>,
    val status: String
)
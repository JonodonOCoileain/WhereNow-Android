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
        return comName + "\n" + sciName + "\n" + "Quantity: " + howMany + "\n" + "Seen at: " + locName + "\n"
    }
    fun descriptionWithoutLocation(): String {
        val name: String
        if (userDisplayName == null) {
            name = "unknown"
        } else {
            name = userDisplayName
        }
        return comName + "\n" + sciName + "\n" + "Quantity: " + howMany + "\n" + "Seen by: " + userDisplayName
    }
    fun seenAt(): String {
        return "Seen at: " + locName + "\n"
    }
}

data class BirdSightingsResponse(
    val result: List<BirdSighting>,
    val status: String
)
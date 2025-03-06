package com.jonocoileain.wherenow.pages
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.layout.Box
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Locale
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun GetCurrentLocation() {
    val context = LocalContext.current
    var address by remember { mutableStateOf("Fetching address...") }

    // Create a FusedLocationProviderClient
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchLocationAndAddress(context, fusedLocationClient) {
                address = it ?: "Unable to fetch address"
            }
        } else {
            address = "Permission denied"
        }
    }

    // Check if permission is granted, otherwise request it
    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionChecker.PERMISSION_GRANTED -> {
                fetchLocationAndAddress(context, fusedLocationClient) {
                    address = it ?: "Unable to fetch address"
                }
            }
            else -> locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Display the address in a Composable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start=16.dp, end = 16.dp, top=64.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(text = address, style = MaterialTheme.typography.bodyLarge, color = Color.White)
    }
}

@SuppressLint("MissingPermission")
private fun fetchLocationAndAddress(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onAddressFetched: (String?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            println(location)
            // Convert location to address
            getAddressFromLocation(context, location) { address ->
                onAddressFetched(address)
            }
        } ?: onAddressFetched("Location was not found")
    }.addOnFailureListener {
        onAddressFetched("Error fetching location: ${it.message}")
    }
}

// Function to convert location to address using Geocoder
private fun getAddressFromLocation(
    context: Context,
    location: Location,
    onAddressFetched: (String?) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val latitude = location.latitude
    val longitude = location.longitude

    try {
        val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                onAddressFetched(address)
            } else {
                onAddressFetched("No address found")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onAddressFetched("Error fetching address: ${e.message}")
    }
}
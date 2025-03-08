package com.jonocoileain.wherenow.pages

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.jonocoileain.wherenow.utils.LocationUtils
import com.jonocoileain.wherenow.viewmodels.LocationViewModel
import com.jonocoileain.wherenow.MainActivity
import kotlin.time.Duration.Companion.seconds

@Composable
fun HereNowPage(modifier: Modifier = Modifier,
                locationUtils: LocationUtils,
                viewModel: LocationViewModel,
                context: Context) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1976D2)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(52.dp))
        Text(
            text = "Here Now!",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        DisplayLocation(locationUtils = locationUtils,
                viewModel = viewModel,
                context = context,
                addressResults = viewModel.address.value.firstOrNull()?.formatted_address ?: "No Address")
    }
}


@Composable
fun DisplayLocation(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context,
    addressResults: String
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val location = viewModel.location.value

    val address = location?.let {
        locationUtils.requestGeocodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult = { permissions ->
            if( permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                // Ok can access location
                locationUtils.requestLocationUpdates(viewModel=viewModel)

            } else {
                // ask for permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION )

                if(rationaleRequired) {
                    Toast.makeText(context,"This feature require location permission", Toast.LENGTH_LONG).show()
                }else{
                    // need to set the permission from setting.
                    Toast.makeText(context,"Please enable location permission from android setting",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    var ticks by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(1.seconds)
            ticks++
            if (ticks % 10 == 0) {
                if(locationUtils.hasLocationPermission(context)) {
                    // permission granted -> update the location
                    locationUtils.requestLocationUpdates(viewModel)
                }else {
                    // Request location permission
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {
                if(locationUtils.hasLocationPermission(context)) {
                    // permission granted -> update the location
                    locationUtils.requestLocationUpdates(viewModel)
                }else {
                    // Request location permission
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
            Lifecycle.State.RESUMED -> {
                if(locationUtils.hasLocationPermission(context)) {
                    // permission granted -> update the location
                    locationUtils.requestLocationUpdates(viewModel)
                }else {
                    // Request location permission
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

        if ( location != null) {
            Text( "Latitude: ${location.latitude}\nLongitude: ${location.longitude}\n\n$address",
                fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center, lineHeight = 16.sp)
            val locationLatLngValue = LatLng(location.latitude, location.longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(locationLatLngValue, 15f)
            }
            Spacer(modifier = Modifier.size(20.dp))
            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier
                        .size(width = 280.dp, height = 280.dp)
                        .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                    ,
                    cameraPositionState = cameraPositionState
                )
            }
        } else {
            Text("Location not yet available", fontSize = 20.sp, color = Color.White)
        }
    }
}
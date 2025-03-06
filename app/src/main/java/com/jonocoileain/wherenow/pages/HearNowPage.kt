package com.jonocoileain.wherenow.pages

import android.Manifest
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.jonocoileain.wherenow.utils.LocationUtils
import com.jonocoileain.wherenow.MainActivity
import com.jonocoileain.wherenow.viewmodels.BirdSightingsViewModel
import com.jonocoileain.wherenow.viewmodels.LocationViewModel
import kotlin.time.Duration.Companion.seconds
import android.net.Uri

@Composable
fun HearNowPage(modifier: Modifier = Modifier,
                locationUtils: LocationUtils,
                locationViewModel: LocationViewModel,
                birdSightingsViewModel: BirdSightingsViewModel,
                context: Context) {Int
    var ticks: Int by remember { mutableIntStateOf(0) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult = { permissions ->
            if( permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                // Ok can access location
                locationUtils.requestLocationUpdates(viewModel=locationViewModel)

            } else {
                // ask for permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
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

    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(1.seconds)
            ticks++
            if (ticks % 10 == 0 || ticks == 1) {
                if(locationUtils.hasLocationPermission(context)) {
                    // permission granted -> update the location
                    locationUtils.requestLocationUpdates(locationViewModel)
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
            if (ticks % 600 == 0 || ticks == 2) {
                birdSightingsViewModel.fetchNotableBirdSightings(
                    lat = locationViewModel.location.value?.latitude.toString(),
                    lng = locationViewModel.location.value?.longitude.toString()
                )
            }
        }
    }
    if (birdSightingsViewModel.birdSightings.value == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF1976D2)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hear Now!",
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFF1976D2))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        birdSightingsViewModel.birdSightings.value?.forEach { sighting ->
            Text(sighting.description(), fontWeight = FontWeight.SemiBold,
                color = Color.White, modifier = Modifier.clickable {
                    val locationName = sighting.locName
                    val gmmIntentUri = Uri.parse("geo:0,0?q=$locationName")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps") //Ensures that only Google Maps app handles the intent
                    startActivity(context, mapIntent, null)
                })
        }
    }
}
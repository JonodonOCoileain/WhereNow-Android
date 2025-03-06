package com.jonocoileain.wherenow

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import kotlin.collections.listOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.jonocoileain.wherenow.pages.GameNowPage
import com.jonocoileain.wherenow.pages.HereNowPage
import com.jonocoileain.wherenow.pages.HearNowPage
import com.jonocoileain.wherenow.pages.WeatherNowPage
import com.jonocoileain.wherenow.utils.LocationUtils
import com.jonocoileain.wherenow.viewmodels.BirdSightingsViewModel
import com.jonocoileain.wherenow.viewmodels.LocationViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navItemList = listOf(
        NavItem("Here Now!", Icons.Default.LocationOn,0),
        NavItem("Hear Now!",  ImageVector.vectorResource(R.drawable.ic_raven_24px), badgeCount = 0),
        //NavItem("Weather Now!", ImageVector.vectorResource(R.drawable.ic_sunny_24px),0),
        //NavItem("Game Now!", ImageVector.vectorResource(R.drawable.ic_videogame_asset_24px),0),
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem -> 
                    NavigationBarItem(
                        selected =  selectedIndex == index ,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            BadgedBox(badge = {
                                if(navItem.badgeCount>0)
                                    Badge(){
                                        Text(text = navItem.badgeCount.toString())
                                    }
                            }) {
                                Icon(imageVector = navItem.icon, contentDescription = "Icon")
                            }
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding),selectedIndex)
    }
}


@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int) {
    val locationViewModel: LocationViewModel = LocationViewModel()
    val birdSightingsViewModel: BirdSightingsViewModel = BirdSightingsViewModel()
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    when(selectedIndex){
        0-> HereNowPage(viewModel = locationViewModel,
                        context = context,
                        locationUtils = locationUtils)
        1-> HearNowPage(locationUtils = locationUtils,
            locationViewModel = locationViewModel,
            context = context,
            birdSightingsViewModel = birdSightingsViewModel)
        //2-> WeatherNowPage()
        //3-> GameNowPage()
    }
}




















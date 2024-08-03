package com.example.native202411pub.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.native202411pub.getLastLocation
import com.example.native202411pub.getLocationPermission
import com.example.native202411pub.locationUpdate
import com.example.native202411pub.logger
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var location by rememberSaveable { mutableStateOf("") }
    Column(modifier = modifier) {
        Button(onClick = {
            scope.launch {
                logger.trace("Get Permission START")
                val granted = getLocationPermission()
                logger.info("granted = {}", granted)
                logger.trace("Get Permission END")
            }
        }) {
            Text(text = "Get Permission")
        }
        Button(onClick = {
            scope.launch {
                logger.trace("Get Last Location START")
                val loc = getLastLocation()
                logger.info("last location = {}", loc)
                location = "${loc?.latitude} ${loc?.longitude}"
                logger.trace("Get Last Location END")
            }
        }) {
            Text(text = "Get Last Location")
        }
        Button(onClick = {
            scope.launch {
                logger.trace("Start Location Update START")
                val loc = locationUpdate()
                logger.info("updated location = {}", loc)
                location = "${loc?.latitude} ${loc?.longitude}"
                logger.trace("Start Location Update END")
            }
        }) {
            Text(text = "Location Update")
        }
        Text(text = location)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Native202411PubTheme {
        HomeScreen()
    }
}

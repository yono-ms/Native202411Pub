package com.example.native202411pub.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.native202411pub.MainActivity
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.database.LocationEntity
import com.example.native202411pub.database.MyDatabase
import com.example.native202411pub.extension.toBestString
import com.example.native202411pub.logger
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = MyPrefs.getPrefs(context)
    val isRequestingLocationFlow = prefs.isRequestingLocationFlow.collectAsState(initial = false)
    val dao = MyDatabase.getDatabase(context).locationDao()
    val locations = dao.getAllLocationsFlow().collectAsState(initial = listOf())
    val permissionCoarse = MainActivity.permissionCoarse.collectAsState()
    val permissionFine = MainActivity.permissionFine.collectAsState()

    Column(modifier = modifier) {
        PermissionIndicator(permissionCoarse.value, permissionFine.value)
        if (!permissionFine.value) {
            Text(text = "Home Screen need ACCESS_FINE_LOCATION")
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "OS Settings")
                Spacer(modifier = Modifier.weight(1F))
                Button(onClick = {
                    val uriString = "package:" + context.packageName
                    val uri = Uri.parse(uriString)
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }.also {
                        context.startActivity(it)
                    }
                }) {
                    Text("Open")
                }
            }
        }
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Requesting Update")
            Spacer(modifier = Modifier.weight(1F))
            Switch(checked = isRequestingLocationFlow.value, onCheckedChange = {
                scope.launch {
                    logger.info("onCheckedChange {}", it)
                    prefs.setIsRequestingLocation(it)
                }
            })
        }
        HorizontalDivider()
        LocationItems(list = locations.value)
    }
}

@Composable
private fun PermissionIndicator(
    permissionCoarse: Boolean,
    permissionFine: Boolean
) {
    Row {
        val coarseColor = if (permissionCoarse) Color.Green else Color.Red
        Column(
            modifier = Modifier
                .background(coarseColor)
                .weight(1F), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "COARSE_LOCATION")
        }
        val fineColor = if (permissionFine) Color.Green else Color.Red
        Column(
            modifier = Modifier
                .background(fineColor)
                .weight(1F), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "FINE_LOCATION")
        }
    }
}

@Composable
fun LocationItems(list: List<LocationEntity>) {
    LazyColumn {
        items(list) {
            LocationItem(locationEntity = it)
            HorizontalDivider()
        }
    }
}

@Composable
fun LocationItem(locationEntity: LocationEntity) {
    val latitude = locationEntity.latitude.toString()
    val longitude = locationEntity.longitude.toString()
    val updatedAt = Date(locationEntity.updatedAt).toBestString()
    Column {
        Row {
            Text(
                text = latitude,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1F)
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = longitude,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1F)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "updatedAt", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = updatedAt, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Native202411PubTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionIndicatorPreview() {
    Native202411PubTheme {
        PermissionIndicator(permissionCoarse = true, permissionFine = false)
    }
}

@Preview(showBackground = true)
@Composable
fun LocationItemsPreview() {
    Native202411PubTheme {
        LocationItems(
            listOf(
                LocationEntity(
                    locationId = 1,
                    latitude = 139.670158,
                    longitude = 35.491166,
                    updatedAt = 1722667764000
                ),
                LocationEntity(
                    locationId = 1,
                    latitude = 139.670158,
                    longitude = 35.491166,
                    updatedAt = 1722667765000
                ),
                LocationEntity(
                    locationId = 1,
                    latitude = 139.670158,
                    longitude = 35.491166,
                    updatedAt = 1722667766000
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationItemPreview() {
    Native202411PubTheme {
        LocationItem(
            LocationEntity(
                locationId = 1,
                latitude = 139.670158,
                longitude = 35.491166,
                updatedAt = 1722669279326
            )
        )
    }
}

package com.example.native202411pub

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.native202411pub.screen.MainAlertDialog
import com.example.native202411pub.screen.MainScreen
import com.example.native202411pub.ui.theme.Native202411PubTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var Instance: MainActivity
        internal fun shared(): MainActivity {
            return Instance
        }
    }

    private fun loggerTest() {
        logger.trace("Logger TEST")
        logger.debug("Logger TEST")
        logger.info("Logger TEST")
        logger.warn("Logger TEST")
        logger.error("Logger TEST")
    }

    private lateinit var locationPermissionContinuation: Continuation<Boolean>
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        logger.info("RequestPermission {}", granted)
        locationPermissionContinuation.resume(granted)
    }

    private suspend fun requestLocationPermission() = suspendCoroutine { continuation ->
        locationPermissionContinuation = continuation
        requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
    }

    internal suspend fun getLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            logger.info("checkSelfPermission granted")
            return true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                logger.info("shouldShowRequestPermissionRationale true")
                showAlert("OK", null, null, "App need permission")
            } else {
                logger.info("shouldShowRequestPermissionRationale false")
            }
            val result = requestLocationPermission()
            return result
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    internal suspend fun getLastLocation() = suspendCoroutine { continuation ->
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                continuation.resume(it)
            }
        } else {
            continuation.resume(null)
        }
    }

    internal suspend fun locationUpdate() = suspendCoroutine { continuation ->
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                logger.info("LocationCallback onLocationResult {}", locationResult)
                continuation.resume(locationResult.locations.lastOrNull())
                fusedLocationClient.removeLocationUpdates(this)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                logger.info("LocationCallback onLocationAvailability {}", locationAvailability)
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.Builder(10000).build()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        }
    }

    private val confirmFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val dismissFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val titleFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val textFlow: MutableStateFlow<String> = MutableStateFlow("")

    private lateinit var alertContinuation: Continuation<Boolean>
    internal suspend fun showAlert(
        confirm: String,
        dismiss: String?,
        title: String?,
        text: String
    ): Boolean = suspendCoroutine {
        alertContinuation = it
        confirmFlow.value = confirm
        dismissFlow.value = dismiss
        titleFlow.value = title
        textFlow.value = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Instance = this
        loggerTest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            Native202411PubTheme {
                MainScreen(modifier = Modifier.fillMaxSize())
                val confirm = confirmFlow.collectAsState()
                val dismiss = dismissFlow.collectAsState()
                val title = titleFlow.collectAsState()
                val text = textFlow.collectAsState()
                if (text.value.isNotEmpty()) {
                    MainAlertDialog(
                        confirm = confirm.value,
                        dismiss = dismiss.value,
                        title = title.value,
                        text = text.value
                    ) {
                        alertContinuation.resume(it)
                        textFlow.value = ""
                    }
                }
            }
        }
    }
}

//region Top Level
val logger: Logger by lazy { LoggerFactory.getLogger("Native202411Pub") }

suspend fun showDialog(
    text: String,
    confirm: String = "OK",
    dismiss: String? = null,
    title: String? = null
): Boolean {
    return MainActivity.shared().showAlert(confirm, dismiss, title, text)
}

suspend fun getLocationPermission(): Boolean {
    return MainActivity.shared().getLocationPermission()
}

suspend fun getLastLocation(): Location? {
    return MainActivity.shared().getLastLocation()
}

suspend fun locationUpdate(): Location? {
    return MainActivity.shared().locationUpdate()
}
//endregion

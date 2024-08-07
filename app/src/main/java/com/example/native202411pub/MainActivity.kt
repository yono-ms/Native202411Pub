package com.example.native202411pub

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.lifecycle.lifecycleScope
import com.example.native202411pub.database.LocationEntity
import com.example.native202411pub.database.MyDatabase
import com.example.native202411pub.screen.MainAlertDialog
import com.example.native202411pub.screen.MainScreen
import com.example.native202411pub.ui.theme.Native202411PubTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Date
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var Instance: MainActivity
        val permissionCoarse = MutableStateFlow(false)
        val permissionFine = MutableStateFlow(false)
        suspend fun dialog(
            text: String,
            confirm: String = "OK",
            dismiss: String? = null,
            title: String? = null
        ): Boolean {
            return Instance.showAlertDialog(confirm, dismiss, title, text)
        }
    }

    private fun loggerTest() {
        logger.trace("Logger TEST")
        logger.debug("Logger TEST")
        logger.info("Logger TEST")
        logger.warn("Logger TEST")
        logger.error("Logger TEST")
    }

    //region Location Permission

    private lateinit var locationPermissionContinuation: Continuation<Boolean>
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        logger.info("RequestPermission {}", permissions)
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionContinuation.resume(true)
            }

            else -> {
                locationPermissionContinuation.resume(false)
            }
        }
    }

    private suspend fun getLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            logger.info("checkSelfPermission granted")
            return true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                logger.info("shouldShowRequestPermissionRationale true")
                dialog("App need ACCESS_FINE_LOCATION")
            } else {
                logger.info("shouldShowRequestPermissionRationale false")
            }
            val result = suspendCoroutine { continuation ->
                locationPermissionContinuation = continuation
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
            return result
        }
    }
    //endregion Location Permission

    //region Access Fine Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isRequestingLocation: Boolean = false
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            logger.trace("LocationCallback onLocationResult {}", locationResult)
            lifecycleScope.launch {
                val dao = MyDatabase.getDatabase(this@MainActivity).locationDao()
                for (location in locationResult.locations) {
                    dao.insertLocation(
                        LocationEntity(
                            locationId = 0,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            updatedAt = Date().time
                        )
                    )
                }
            }
        }
    }

    private fun startRequestingLocation() {
        logger.trace("startRequestingLocation")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000
            ).build()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopRequestingLocation() {
        logger.trace("stopRequestingLocation")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun watchRequestingLocation() {
        lifecycleScope.launch {
            MyPrefs.getPrefs(this@MainActivity).isRequestingLocationFlow.collect {
                logger.info("isRequestingLocationFlow collect {}", it)
                if (it) {
                    if (!isRequestingLocation) {
                        logger.trace("start")
                        if (getLocationPermission()) {
                            startRequestingLocation()
                            isRequestingLocation = true
                        } else {
                            MyPrefs.getPrefs(this@MainActivity).setIsRequestingLocation(false)
                        }
                    } else {
                        logger.trace("already started")
                    }
                } else {
                    if (isRequestingLocation) {
                        logger.trace("stop")
                        stopRequestingLocation()
                        isRequestingLocation = false
                    } else {
                        logger.trace("already stopped")
                    }
                }
            }
        }
    }
    //endregion Access Fine Location

    //region AlertDialog
    private val confirmFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val dismissFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val titleFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val textFlow: MutableStateFlow<String> = MutableStateFlow("")

    private lateinit var alertContinuation: Continuation<Boolean>
    private suspend fun showAlertDialog(
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
    //endregion AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Instance = this
        loggerTest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        watchRequestingLocation()
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

    override fun onResume() {
        super.onResume()
        logger.trace("onResume")
        permissionCoarse.value = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        permissionFine.value = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        lifecycleScope.launch {
            if (permissionFine.value) {
                if (isRequestingLocation) {
                    startRequestingLocation()
                }
            } else {
                val prefs = MyPrefs.getPrefs(this@MainActivity)
                prefs.setIsRequestingLocation(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logger.trace("onPause")
        stopRequestingLocation()
    }
}

//region Top Level
val logger: Logger by lazy { LoggerFactory.getLogger("Native202411Pub") }
//endregion

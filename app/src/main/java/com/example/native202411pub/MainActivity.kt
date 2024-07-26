package com.example.native202411pub

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.native202411pub.screen.MainAlertDialog
import com.example.native202411pub.screen.MainScreen
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.slf4j.LoggerFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var Instance: MainActivity
        fun shared(): MainActivity {
            return Instance
        }
    }

    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    private fun loggerTest() {
        logger.trace("Logger TEST")
        logger.debug("Logger TEST")
        logger.info("Logger TEST")
        logger.warn("Logger TEST")
        logger.error("Logger TEST")
    }

    private lateinit var networkStatusFow: Flow<Boolean>
    lateinit var networkStatusStateFlow: StateFlow<Boolean>

    private fun watchNetworkStatus() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkStatusFow = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                val list = mutableListOf<Network>()
                override fun onAvailable(network: Network) {
                    logger.debug("onAvailable {}", network)
                    list.add(network)
                    trySend(true)
                }

                override fun onUnavailable() {
                    logger.debug("onUnavailable")
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    logger.debug("onLost {}", network)
                    list.remove(network)
                    if (list.isEmpty()) {
                        trySend(false)
                    }
                }
            }
            manager.registerNetworkCallback(request, callback)
            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged().flowOn(Dispatchers.IO)

        networkStatusStateFlow = networkStatusFow.stateIn(
            lifecycleScope,
            initialValue = false,
            started = SharingStarted.Eagerly
        )
    }

    private val confirmFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val dismissFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val titleFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val textFlow: MutableStateFlow<String> = MutableStateFlow("")

    private lateinit var continuation: Continuation<Boolean>
    suspend fun showAlert(
        confirm: String,
        dismiss: String?,
        title: String?,
        text: String
    ): Boolean = suspendCoroutine {
        continuation = it
        confirmFlow.value = confirm
        dismissFlow.value = dismiss
        titleFlow.value = title
        textFlow.value = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Instance = this
        loggerTest()
        watchNetworkStatus()
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
                        continuation.resume(it)
                        textFlow.value = ""
                    }
                }
            }
        }
    }
}

package com.example.native202411pub.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.native202411pub.MyNetworkStatus
import com.example.native202411pub.logger
import com.example.native202411pub.server.GitHubAPI
import com.example.native202411pub.server.alertMessage
import com.example.native202411pub.showDialog
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val isConnect =
        MyNetworkStatus.getNetworkStatus(LocalContext.current).isConnectFow.collectAsState(
            initial = false
        )
    Column(modifier = modifier) {
        Text(text = "Network ${isConnect.value}")
        Button(onClick = {
            scope.launch {
                logger.trace("Get users START")
                runCatching {
                    GitHubAPI.getUsers("yono-ms")
                }.onSuccess {
                    logger.debug(it.toString())
                }.onFailure {
                    logger.error("Error", it)
                    showDialog(text = it.alertMessage(), title = "Error")
                }
                logger.trace("Get users END")
            }
        }, enabled = isConnect.value) {
            Text(text = "Get users")
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

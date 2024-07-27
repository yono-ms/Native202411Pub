package com.example.native202411pub.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.logger
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier, onFinish: (screen: MyScreen) -> Unit) {
    val prefs = MyPrefs.getPrefs(LocalContext.current)
    LaunchedEffect(key1 = true) {
        logger.trace("SplashScreen LaunchedEffect START")
        delay(2_000)
        val isShowTutorial = prefs.getIsShowTutorial()
        if (isShowTutorial) {
            onFinish(MyScreen.TUTORIAL)
        } else {
            onFinish(MyScreen.MAIN)
        }
        logger.trace("SplashScreen LaunchedEffect END")
    }
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "loading")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Native202411PubTheme {
        SplashScreen {}
    }
}

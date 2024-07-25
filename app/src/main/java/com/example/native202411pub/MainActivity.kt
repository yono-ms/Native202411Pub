package com.example.native202411pub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.native202411pub.screen.MainScreen
import com.example.native202411pub.ui.theme.Native202411PubTheme
import org.slf4j.LoggerFactory

class MainActivity : ComponentActivity() {

    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    private fun loggerTest() {
        logger.trace("Logger TEST")
        logger.debug("Logger TEST")
        logger.info("Logger TEST")
        logger.warn("Logger TEST")
        logger.error("Logger TEST")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggerTest()
        enableEdgeToEdge()
        setContent {
            Native202411PubTheme {
                MainScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}



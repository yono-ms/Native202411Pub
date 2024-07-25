package com.example.native202411pub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Native202411PubTheme {
        Greeting("Android")
    }
}
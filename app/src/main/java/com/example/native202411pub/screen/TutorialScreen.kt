package com.example.native202411pub.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.logger
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(onFinish: () -> Unit) {
    val scope = rememberCoroutineScope()
    val prefs = MyPrefs.getPrefs(LocalContext.current)
    val isShowTutorial = prefs.isShowTutorialFlow.collectAsState(initial = true)
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Tutorial", style = MaterialTheme.typography.displayLarge)
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isShowTutorial.value, onCheckedChange = {
                    scope.launch {
                        logger.debug("onCheckedChange START $it")
                        prefs.setIsShowTutorial(it)
                        logger.debug("onCheckedChange END")
                    }
                })
                Text(text = "Show Tutorial")
            }
            Button(onClick = { onFinish() }) {
                Text(text = "Finish")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    Native202411PubTheme {
        TutorialScreen {}
    }
}

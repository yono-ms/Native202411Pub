package com.example.native202411pub.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.logger
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginEditScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val prefs = MyPrefs.getPrefs(LocalContext.current)
    var login by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = MyScreen.LOGIN_EDIT.title) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextField(
                value = login,
                onValueChange = {
                    when {
                        it.contains("\n") -> errorMessage = "contains RETURN"
                        it.length > LOGIN_MAX_LENGTH -> errorMessage =
                            "length ${it.length} ($LOGIN_MAX_LENGTH)"

                        else -> {
                            errorMessage = ""
                            login = it
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "login")
                },
                placeholder = {
                    Text(text = "login name")
                },
                isError = errorMessage.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        logger.debug("Keyboard onDone")
                    }
                ),
                singleLine = true,
                maxLines = 1
            )
            Text(text = errorMessage)
            Button(onClick = {
                scope.launch {
                    logger.debug("Button onClick Done")
                    prefs.setLogin(login)
                    onBack()
                }
            }, enabled = login.isNotEmpty()) {
                Text(text = "Done")
            }
        }
    }
}

const val LOGIN_MAX_LENGTH = 32

@Preview(showBackground = true)
@Composable
fun LoginEditScreenPreview() {
    Native202411PubTheme {
        LoginEditScreen {}
    }
}

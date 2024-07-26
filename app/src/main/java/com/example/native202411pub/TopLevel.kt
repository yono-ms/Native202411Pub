package com.example.native202411pub

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger by lazy { LoggerFactory.getLogger("Composable") }

suspend fun showDialog(
    text: String,
    confirm: String = "OK",
    dismiss: String? = null,
    title: String? = null
): Boolean {
    return MainActivity.shared().showAlert(confirm, dismiss, title, text)
}

val isConnectFlow
    get() = MainActivity.shared().networkStatusStateFlow

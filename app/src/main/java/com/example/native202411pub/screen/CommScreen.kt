package com.example.native202411pub.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.native202411pub.MyNetworkStatus
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.database.MyDatabase
import com.example.native202411pub.extension.fromIsoToDate
import com.example.native202411pub.extension.toBestString
import com.example.native202411pub.logger
import com.example.native202411pub.screen.dialog.EditDialog
import com.example.native202411pub.server.GitHubAPI
import com.example.native202411pub.server.GitHubRepos
import com.example.native202411pub.server.GitHubUsers
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun CommScreen(
    modifier: Modifier = Modifier,
    onHistory: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val prefs = MyPrefs.getPrefs(LocalContext.current)
    val login = prefs.loginFlow.collectAsState(initial = "")
    val dao = MyDatabase.getDatabase(LocalContext.current).gitHubDao()
    val repos = dao.getReposFlow(login.value).collectAsState(initial = listOf())
    val isConnect =
        MyNetworkStatus.getNetworkStatus(LocalContext.current).isConnectFow.collectAsState(
            initial = false
        )
    var messsage by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    val updateRepos: () -> Unit = {
        scope.launch {
            runCatching {
                logger.trace("updateRepos START")
                messsage = "loading"
                dao.getRepos(login.value).forEach {
                    logger.debug("deleteRepos {}", it)
                    dao.deleteRepos(it)
                }
                val users: GitHubUsers = GitHubAPI.getUsers(login.value)
                dao.insertUsers(users)
                val list: List<GitHubRepos> = GitHubAPI.getRepos(users.reposUrl)
                list.forEach {
                    it.userId = users.userId
                    logger.debug("insertRepos {}", it)
                    dao.insertRepos(it)
                }
                messsage = "success"
            }.onFailure {
                messsage = it.localizedMessage ?: "error"
            }.also {
                logger.trace("updateRepos END")
            }
        }
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onHistory) {
                Icon(imageVector = Icons.Filled.AccountBox, contentDescription = "")
            }
            Box(
                modifier = Modifier
                    .weight(1F)
                    .background(Color.LightGray)
                    .clickable {
                        logger.trace("onClick")
                        isVisible = true
                    }
            ) {
                Text(
                    text = login.value,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = updateRepos, enabled = login.value.isNotEmpty() && isConnect.value) {
                Text(text = "update")
            }
        }
        Text(text = "Network ${isConnect.value}")
        Text(text = messsage)
        HorizontalDivider()
        ReposItems(list = repos.value)
        if (isVisible) {
            EditDialog(onDismiss = { isVisible = false }) {
                scope.launch {
                    prefs.setLogin(it)
                    isVisible = false
                }
            }
        }
    }
}

@Composable
fun ReposItems(list: List<GitHubRepos>) {
    LazyColumn {
        items(list) {
            val date = it.updatedAt.fromIsoToDate()
            ReposItem(name = it.name, updatedAt = date.toBestString())
            HorizontalDivider()
        }
    }
}

@Composable
fun ReposItem(name: String, updatedAt: String) {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "updatedAt", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = updatedAt, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommScreenPreview() {
    Native202411PubTheme {
        CommScreen(modifier = Modifier, onHistory = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ReposItemsPreview() {
    Native202411PubTheme {
        ReposItems(
            listOf(
                GitHubRepos(repoId = 1, name = "Repository Name 1", updatedAt = ""),
                GitHubRepos(repoId = 2, name = "Repository Name 2", updatedAt = ""),
                GitHubRepos(repoId = 3, name = "Repository Name 3", updatedAt = ""),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReposItemPreview() {
    Native202411PubTheme {
        ReposItem("Repository Name", Date().toBestString())
    }
}

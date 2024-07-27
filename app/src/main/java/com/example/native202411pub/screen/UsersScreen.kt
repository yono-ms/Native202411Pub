package com.example.native202411pub.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.native202411pub.MyPrefs
import com.example.native202411pub.database.MyDatabase
import com.example.native202411pub.extension.fromIsoToDate
import com.example.native202411pub.extension.toBestString
import com.example.native202411pub.logger
import com.example.native202411pub.server.GitHubUsers
import com.example.native202411pub.ui.theme.Native202411PubTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(onBack: () -> Unit) {
    val dao = MyDatabase.getDatabase(LocalContext.current).gitHubDao()
    val users = dao.getAllUsersFlow().collectAsState(initial = listOf())
    val prefs = MyPrefs.getPrefs(LocalContext.current)
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = MyScreen.USERS.title) },
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
            Text(text = "Recent login")
            HorizontalDivider()
            UsersItems(
                list = users.value,
                onClick = {
                    scope.launch {
                        prefs.setLogin(it)
                        onBack()
                    }
                },
                onDelete = { gitHubUsers ->
                    scope.launch {
                        dao.getRepos(gitHubUsers.login).forEach {
                            logger.debug("deleteRepos {}", it)
                            dao.deleteRepos(it)
                        }
                        dao.deleteUsers(gitHubUsers)
                    }
                }
            )
        }
    }
}

@Composable
fun UsersItems(
    list: List<GitHubUsers>,
    onClick: (String) -> Unit,
    onDelete: (GitHubUsers) -> Unit
) {
    LazyColumn {
        items(items = list, key = { item -> item.userId }) {
            SwipeBox(
                item = it,
                onClick = {
                    onClick(it.login)
                },
                onDelete = {
                    onDelete(it)
                }
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBox(item: GitHubUsers, onClick: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> MaterialTheme.colorScheme.background
                }, label = "SwipeToDismissBox ColorAnimation"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            )
        }) {
        UsersItem(
            modifier = Modifier.clickable {
                logger.debug("UsersItem onClick ${item.login}")
                onClick()
            },
            item = item
        )
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> onDelete()
            else -> logger.trace("ignore {}", dismissState.currentValue)
        }
    }
}

@Composable
fun UsersItem(modifier: Modifier = Modifier, item: GitHubUsers) {
    val date = item.updatedAt.fromIsoToDate()
    val dateText = date.toBestString()
    Column(modifier = modifier) {
        Text(text = item.login, style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "updatedAt", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = dateText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    Native202411PubTheme {
        UsersScreen {}
    }
}

@Preview(showBackground = true)
@Composable
fun UsersItemsPreview() {
    Native202411PubTheme {
        UsersItems(
            listOf(
                GitHubUsers(
                    userId = 1,
                    login = "Login Name 1",
                    publicRepos = 1,
                    reposUrl = "",
                    updatedAt = ""
                ),
                GitHubUsers(
                    userId = 2,
                    login = "Login Name 2",
                    publicRepos = 2,
                    reposUrl = "",
                    updatedAt = ""
                ),
                GitHubUsers(
                    userId = 3,
                    login = "Login Name 3",
                    publicRepos = 3,
                    reposUrl = "",
                    updatedAt = ""
                ),
            ),
            onClick = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UsersItemPreview() {
    Native202411PubTheme {
        UsersItem(
            item = GitHubUsers(
                userId = 1,
                login = "Login Name 1",
                publicRepos = 1,
                reposUrl = "",
                updatedAt = ""
            )
        )
    }
}

package com.example.native202411pub.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.native202411pub.ui.theme.Native202411PubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var mainScreen by rememberSaveable { mutableStateOf(MyScreen.COMM) }
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MyScreen.SPLASH.name,
        modifier = modifier
    ) {
        composable(MyScreen.SPLASH.name) {
            SplashScreen(modifier = modifier) {
                navController.navigate(it.name) {
                    popUpTo(MyScreen.SPLASH.name) {
                        inclusive = true
                    }
                }
            }
        }
        composable(MyScreen.TUTORIAL.name) {
            TutorialScreen {
                navController.navigate(MyScreen.MAIN.name) {
                    popUpTo(MyScreen.TUTORIAL.name) {
                        inclusive = true
                    }
                }
            }
        }
        composable(MyScreen.MAIN.name) {
            Scaffold(topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text(text = mainScreen.title) },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(MyScreen.SETTINGS.name)
                        }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }, modifier = modifier) { innerPadding ->
//                HomeScreen(
//                    modifier = Modifier.padding(innerPadding)
//                )
                CommScreen(
                    modifier = Modifier.padding(innerPadding),
                    onHistory = {
                        navController.navigate(MyScreen.USERS.name)
                    },
                    onEdit = {
                        navController.navigate(MyScreen.LOGIN_EDIT.name)
                    }
                )
            }
        }
        composable(MyScreen.LOGIN_EDIT.name) {
            LoginEditScreen {
                navController.navigateUp()
            }
        }
        composable(MyScreen.USERS.name) {
            UsersScreen {
                navController.navigateUp()
            }
        }
        composable(MyScreen.SETTINGS.name) {
            SettingsScreen {
                navController.navigateUp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Native202411PubTheme {
        MainScreen()
    }
}

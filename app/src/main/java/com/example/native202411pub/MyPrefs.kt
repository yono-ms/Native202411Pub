package com.example.native202411pub

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyPrefs private constructor(context: Context, private val scope: CoroutineScope) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val IS_SHOW_TUTORIAL = booleanPreferencesKey("is_show_tutorial")
        private val IS_REQUESTING_LOCATION = booleanPreferencesKey("is_requesting_location")
        private val LOGIN = stringPreferencesKey("login")

        @Volatile
        private var Instance: MyPrefs? = null

        fun getPrefs(context: Context): MyPrefs {
            return Instance ?: synchronized(this) {
                val scope = if (context is MainActivity) {
                    logger.trace("context is MainActivity")
                    context.lifecycleScope
                } else {
                    logger.warn("context is not MainActivity")
                    CoroutineScope(Job() + Dispatchers.Main)
                }
                MyPrefs(context, scope).also { Instance = it }
            }
        }
    }

    val isShowTutorialFlow: Flow<Boolean> = dataStore.data.map {
        it[IS_SHOW_TUTORIAL] ?: true
    }

    suspend fun getIsShowTutorial() = suspendCoroutine { continuation ->
        scope.launch {
            isShowTutorialFlow.collect {
                continuation.resume(it)
                cancel()
            }
        }
    }

    suspend fun setIsShowTutorial(isShow: Boolean) {
        dataStore.edit {
            it[IS_SHOW_TUTORIAL] = isShow
        }
    }

    val isRequestingLocationFlow: Flow<Boolean> = dataStore.data.map {
        it[IS_REQUESTING_LOCATION] ?: false
    }

    suspend fun setIsRequestingLocation(isRequesting: Boolean) {
        dataStore.edit {
            it[IS_REQUESTING_LOCATION] = isRequesting
        }
    }

    val loginFlow: Flow<String> = dataStore.data.map {
        it[LOGIN] ?: ""
    }

    suspend fun setLogin(login: String) {
        dataStore.edit {
            it[LOGIN] = login
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

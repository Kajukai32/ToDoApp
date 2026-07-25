package com.arturojas32.todoapp.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DATA_STORE_NAME = "todo_app_data_store"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

@Singleton
class DataStoreManager @Inject constructor(@param:ApplicationContext private val context: Context) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun saveThemeModeKey(darkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = darkMode
        }
    }

    fun getUserId(): Flow<String> {
        return context.dataStore.data.map { preferences ->

            preferences[USER_ID] ?: ""
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }

    fun getDarkModePref(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->

            preferences[DARK_MODE_KEY] ?: false
        }
    }

}

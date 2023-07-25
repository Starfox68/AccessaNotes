package com.shaphr.accessanotes.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val fontFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LARGE_FONT] ?: false
    }

    val colourFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[COLOUR_BLIND] ?: false
    }

    suspend fun onFontChange(isLarge: Boolean) {
        context.dataStore.edit { settings ->
            settings[LARGE_FONT] = isLarge
        }
    }

    suspend fun onColourChange(isColourBlind: Boolean) {
        context.dataStore.edit { settings ->
            settings[COLOUR_BLIND] = isColourBlind
        }
    }

    companion object {
        val LARGE_FONT = booleanPreferencesKey("font")
        val COLOUR_BLIND = booleanPreferencesKey("colour")
    }
}

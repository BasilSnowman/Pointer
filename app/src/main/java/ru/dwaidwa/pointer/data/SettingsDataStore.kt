package ru.dwaidwa.pointer.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ru.dwaidwa.pointer.ui.theme.MyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferenceKeys {
    val SELECTED_THEME = stringPreferencesKey("selected_theme")
}

class SettingsDataStore(private val context: Context) {

    suspend fun saveSelectedTheme(theme: MyAppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SELECTED_THEME] = theme.name
        }
    }

    suspend fun loadSelectedTheme(): MyAppTheme {
        return context.dataStore.data
            .map { preferences ->
                val themeName = preferences[PreferenceKeys.SELECTED_THEME] ?: MyAppTheme.SYSTEM.name
                try {
                    MyAppTheme.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    MyAppTheme.SYSTEM // Значение по умолчанию при ошибке
                }
            }.first()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val DATE_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimestampsKey(date: java.time.LocalDate): androidx.datastore.preferences.core.Preferences.Key<Set<String>> {
        val dateStr = date.format(DATE_FORMATTER)
        return stringSetPreferencesKey("click_timestamps_$dateStr")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadTodayClickTimestamps(): List<String> {
        val today = java.time.LocalDate.now()
        val key = getTimestampsKey(today)
        val preferences = context.dataStore.data.first()
        return (preferences[key] ?: emptySet()).toList().sorted()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadTodayClickCount(): Int {
        return loadTodayClickTimestamps().size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addTodayClickTimestamp() {
        val today = java.time.LocalDate.now()
        val key = getTimestampsKey(today)
        val newTimestamp = java.time.Instant.now().toString()

        context.dataStore.edit { preferences ->
            val currentSet = preferences[key] ?: emptySet()
            val updatedSet = currentSet + newTimestamp
            preferences[key] = updatedSet
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun resetTodayClickTimestamps() {
        val today = java.time.LocalDate.now()
        val key = getTimestampsKey(today)
        context.dataStore.edit { preferences ->
            preferences[key] = emptySet()
        }
    }
}
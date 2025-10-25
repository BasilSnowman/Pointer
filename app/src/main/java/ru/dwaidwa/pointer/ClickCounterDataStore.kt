package ru.dwaidwa.pointer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "click_counter_store")
@RequiresApi(Build.VERSION_CODES.O)
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")

@RequiresApi(Build.VERSION_CODES.O)
private fun getCounterKey(date: LocalDate): Preferences.Key<Int> {
    val dateStr = date.format(DATE_FORMATTER)
    return intPreferencesKey("click_count_$dateStr")
}

annotation class intPreferencesKey

@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.loadTodayClickCount(): Int {
    val today = LocalDate.now()
    val key = getCounterKey(today)
    val preferences = dataStore.data.first()
    return preferences[key] ?: 0
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.saveTodayClickCount(count: Int) {
    val today = LocalDate.now()
    val key = getCounterKey(today)
    dataStore.edit { preferences ->
        preferences[key] = count
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.resetTodayClickCount() {
    val today = LocalDate.now()
    val key = getCounterKey(today)
    dataStore.edit { preferences ->
        preferences[key] = 0
    }
}
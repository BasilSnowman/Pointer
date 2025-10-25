package ru.dwaidwa.pointer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.time.Instant // Для метки времени

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "click_counter_store")
@RequiresApi(Build.VERSION_CODES.O)
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")

// Ключ для хранения Set строк меток времени (Instant.toString())
@RequiresApi(Build.VERSION_CODES.O)
private fun getTimestampsKey(date: LocalDate): Preferences.Key<Set<String>> {
    val dateStr = date.format(DATE_FORMATTER)
    return stringSetPreferencesKey("click_timestamps_$dateStr")
}

annotation class stringSetPreferencesKey

/**
 * Загружает список меток времени нажатий для текущего дня.
 * Возвращает пустой список, если для сегодняшней даты метки ещё не были установлены.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.loadTodayClickTimestamps(): List<String> {
    val today = LocalDate.now()
    val key = getTimestampsKey(today)
    val preferences = dataStore.data.first()
    return (preferences[key] ?: emptySet()).toList().sorted() // Возвращаем отсортированный список
}

/**
 * Загружает количество нажатий (размер списка меток времени) для текущего дня.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.loadTodayClickCount(): Int {
    return loadTodayClickTimestamps().size
}

/**
 * Добавляет новую метку времени (сейчас) к списку для текущего дня.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.addTodayClickTimestamp() {
    val today = LocalDate.now()
    val key = getTimestampsKey(today)
    val newTimestamp = Instant.now().toString() // Преобразуем текущее время в строку

    dataStore.edit { preferences ->
        val currentSet = preferences[key] ?: emptySet()
        val updatedSet = currentSet + newTimestamp
        preferences[key] = updatedSet
    }
}

/**
 * Сбрасывает (очищает) список меток времени нажатий для текущего дня.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.resetTodayClickTimestamps() {
    val today = LocalDate.now()
    val key = getTimestampsKey(today)
    dataStore.edit { preferences ->
        preferences[key] = emptySet() // Устанавливаем пустой Set
    }
}
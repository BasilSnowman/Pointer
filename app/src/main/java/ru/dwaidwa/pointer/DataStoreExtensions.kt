package ru.dwaidwa.pointer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// Определяем DataStore для приложения
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "click_counter_store")

// Ключ для хранения счётчика
private val CLICK_COUNT_KEY = intPreferencesKey("click_count")

// Ключ для хранения даты (например, в формате YYYY-MM-DD)
private val COUNT_DATE_KEY = intPreferencesKey("count_date")

/**
 * Загружает счётчик и дату из DataStore.
 * Возвращает пару (count, date) или (0, 0), если данные отсутствуют или дата устарела.
 * Для упрощения используем Int для даты в формате YYYYMMDD.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.loadClickCount(): Pair<Int, Int> {
    val preferences = dataStore.data.first()
    val savedCount = preferences[CLICK_COUNT_KEY] ?: 0
    val savedDate = preferences[COUNT_DATE_KEY] ?: 0

    val currentDate = getCurrentDateAsInt() // Получаем текущую дату как Int

    // Если дата в хранилище не совпадает с сегодняшней, сбрасываем счётчик
    return if (savedDate == currentDate) {
        savedCount to currentDate
    } else {
        0 to currentDate // Возвращаем 0, если дата не совпадает
    }
}

/**
 * Сохраняет счётчик и текущую дату в DataStore.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun Context.saveClickCount(count: Int) {
    val currentDate = getCurrentDateAsInt()
    dataStore.edit { preferences ->
        preferences[CLICK_COUNT_KEY] = count
        preferences[COUNT_DATE_KEY] = currentDate
    }
}

/**
 * Вспомогательная функция для получения текущей даты в формате YYYYMMDD как Int.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun getCurrentDateAsInt(): Int {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd") // Формат: 20251025
    return today.format(formatter).toInt()
}
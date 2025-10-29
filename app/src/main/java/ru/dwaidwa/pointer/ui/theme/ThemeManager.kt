package ru.dwaidwa.pointer.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import ru.dwaidwa.pointer.data.SettingsDataStore
import kotlinx.coroutines.launch

val LocalAppTheme = staticCompositionLocalOf<MyAppTheme> { MyAppTheme.SYSTEM }
val LocalThemeSetter = staticCompositionLocalOf<(MyAppTheme) -> Unit> { {} }

@Composable
fun AppThemeWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }

    val initialTheme by produceState(initialValue = MyAppTheme.SYSTEM, producer = {
        value = settingsDataStore.loadSelectedTheme()
    })

    // Текущая тема как изменяемое состояние
    var currentTheme by remember { mutableStateOf(initialTheme) }

    // LaunchedEffect, который срабатывает при изменении currentTheme
    LaunchedEffect(currentTheme) {
        // Вызываем асинхронное сохранение темы
        settingsDataStore.saveSelectedTheme(currentTheme)
    }

    // Функция для изменения темы, которая просто обновляет состояние
    val setTheme: (MyAppTheme) -> Unit = { newTheme ->
        currentTheme = newTheme // Это изменение запустит LaunchedEffect выше
    }

    CompositionLocalProvider(
        LocalAppTheme provides currentTheme,
        LocalThemeSetter provides setTheme,
        content = content
    )
}
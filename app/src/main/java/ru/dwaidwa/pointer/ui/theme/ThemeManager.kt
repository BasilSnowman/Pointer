package ru.dwaidwa.pointer.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import ru.dwaidwa.pointer.data.SettingsDataStore
import kotlinx.coroutines.launch

// Убедимся, что значения по умолчанию установлены
val LocalAppTheme = staticCompositionLocalOf { MyAppTheme.SYSTEM }
val LocalThemeSetter = staticCompositionLocalOf<(MyAppTheme) -> Unit> { {} }

@Composable
fun AppThemeWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }

    val initialTheme by produceState(initialValue = MyAppTheme.SYSTEM, producer = {
        value = settingsDataStore.loadSelectedTheme()
    })

    var currentTheme by remember { mutableStateOf(initialTheme) }

    LaunchedEffect(currentTheme) {
        settingsDataStore.saveSelectedTheme(currentTheme)
    }

    val setTheme: (MyAppTheme) -> Unit = { newTheme ->
        currentTheme = newTheme
    }

    CompositionLocalProvider(
        LocalAppTheme provides currentTheme,
        LocalThemeSetter provides setTheme,
        content = content
    )
}
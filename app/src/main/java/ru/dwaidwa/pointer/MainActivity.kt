package ru.dwaidwa.pointer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.mutableStateOf // Для создания изменяемого состояния
import androidx.compose.runtime.remember // Для запоминания значения между пересборками
import androidx.compose.runtime.getValue // Для чтения значения из State
import androidx.compose.runtime.setValue // Для обновления значения State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ru.dwaidwa.pointer.ui.theme.PointerTheme // Замените на вашу тему
import kotlinx.coroutines.launch // Импортируем для launch в coroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.dwaidwa.pointer.ui.SettingsScreen
import ru.dwaidwa.pointer.data.SettingsDataStore
import ru.dwaidwa.pointer.ui.theme.AppThemeWrapper

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Используем AppThemeWrapper как корень для управления темой
            AppThemeWrapper {
                // Теперь LocalAppTheme и LocalThemeSetter доступны
                PointerTheme { // MyApplicationTheme теперь использует LocalAppTheme
                    Surface(color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()
                        val settingsDataStore = SettingsDataStore(LocalContext.current)

                        NavHost(
                            navController = navController,
                            startDestination = "main"
                        ) {
                            composable("main") { backStackEntry ->
                                val initialTimestampsState = produceState(initialValue = emptyList<String>()) {
                                    value = settingsDataStore.loadTodayClickTimestamps()
                                }

                                val initialTimestamps = initialTimestampsState.value

                                MyEmptyAppScreen(
                                    initialTimestamps = initialTimestamps,
                                    onSettingsClick = { navController.navigate("settings") }
                                )
                            }
                            composable("settings") { backStackEntry ->
                                SettingsScreen(
                                    onResetClick = {
                                        settingsDataStore.resetTodayClickTimestamps()
                                    },
                                    onBackClick = { navController.popBackStack() },
                                    loadTimestamps = { settingsDataStore.loadTodayClickTimestamps() }
                                    // Функции темы теперь доступны через LocalThemeSetter
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyEmptyAppScreen(initialTimestamps: List<String>, onSettingsClick: () -> Unit) {
    var timestamps by remember { mutableStateOf(initialTimestamps) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle // Получаем lifecycle

    // Используем DisposableEffect для подписки на события lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // При возврате на экран (RESUME) перезагружаем список из DataStore
                scope.launch {
                    val updatedList = context.loadTodayClickTimestamps()
                    timestamps = updatedList
                }
            }
            // ON_PAUSE, ON_STOP и т.д. не требуют специальной обработки здесь
        }

        // Добавляем observer
        lifecycle.addObserver(observer)

        // Возвращаем объект DisposableEffect, который удаляет observer при выходе из Composition
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Обновляем счётчик при изменении списка меток
    val currentCount = timestamps.size

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нажатий сегодня: $currentCount",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    // Добавляем новую метку времени
                    context.addTodayClickTimestamp()
                    // Локально обновляем список (можно оставить для быстрого UI-ответа)
                    val updatedList = context.loadTodayClickTimestamps()
                    timestamps = updatedList
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Нажми меня!")
        }

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Настройки")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PointerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MyEmptyAppScreen(initialTimestamps = listOf("2025-10-25T10:00:00Z", "2025-10-25T10:01:00Z"), onSettingsClick = {})
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    PointerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(
                onResetClick = {},
                onBackClick = {},
                loadTimestamps = { emptyList() },
            )
        }
    }
}
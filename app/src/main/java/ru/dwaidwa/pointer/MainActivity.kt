package ru.dwaidwa.pointer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import ru.dwaidwa.pointer.ui.theme.PointerTheme // Замените на вашу тему
import kotlinx.coroutines.launch // Импортируем для launch в coroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.dwaidwa.pointer.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") { backStackEntry ->
                            val initialCountState = produceState(initialValue = -1) {
                                value = (applicationContext as MyApplication).loadTodayClickCount()
                            }

                            val initialCount = initialCountState.value
                            if (initialCount != -1) {
                                MyEmptyAppScreen(
                                    initialCount = initialCount,
                                    onSettingsClick = { navController.navigate("settings") }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        composable("settings") { backStackEntry ->
                            SettingsScreen(
                                onResetClick = {
                                    (applicationContext as MyApplication).resetTodayClickCount()
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyEmptyAppScreen(initialCount: Int, onSettingsClick: () -> Unit) {
    var counter by remember { mutableStateOf(initialCount) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(counter) {
        scope.launch {
            context.saveTodayClickCount(counter)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нажатий сегодня: $counter",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { counter++ },
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
            MyEmptyAppScreen(initialCount = 0, onSettingsClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    PointerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(
                onResetClick = {},
                onBackClick = {}
            )
        }
    }
}
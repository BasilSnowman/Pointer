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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import ru.dwaidwa.pointer.ui.theme.PointerTheme // Замените на вашу тему
import kotlinx.coroutines.launch // Импортируем для launch в coroutineScope

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Загружаем начальное значение при первом запуске Composable
                    val initialCountAndDate = produceState(initialValue = Pair(0, 0)) {
                        value = (applicationContext as MyApplication).loadClickCount()
                    }

                    // Ждем загрузки данных
                    val (initialCount, _) = initialCountAndDate.value

                    // Передаем начальное значение в экран
                    MyEmptyAppScreen(initialCount = initialCount)
                }
            }
        }
    }
}

// Обновленная Composable функция
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyEmptyAppScreen(initialCount: Int) {
    var counter by remember { mutableStateOf(initialCount) }
    val context = LocalContext.current // Получаем контекст для сохранения
    val scope = rememberCoroutineScope() // CoroutineScope для запуска suspend функций

    // Сохраняем счётчик при его изменении
    LaunchedEffect(counter) {
    // Запускаем асинхронное сохранение
        scope.launch {
            context.saveClickCount(counter)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Количество нажатий сегодня: $counter",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                counter++
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Нажми меня!")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PointerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MyEmptyAppScreen(initialCount = 5) // Пример с предустановленным значением
        }
    }
}
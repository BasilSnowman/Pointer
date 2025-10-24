package ru.dwaidwa.pointer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf // Для создания изменяемого состояния
import androidx.compose.runtime.remember // Для запоминания значения между пересборками
import androidx.compose.runtime.getValue // Для чтения значения из State
import androidx.compose.runtime.setValue // Для обновления значения State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import ru.dwaidwa.pointer.ui.theme.PointerTheme // Замените на вашу тему

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MyEmptyAppScreen()
                }
            }
        }
    }
}

// Модифицированная Composable функция
@Composable
fun MyEmptyAppScreen() {
    // Создаём состояние для счётчика
    var counter by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Отображаем текущее значение счётчика
        Text(
            text = "Количество нажатий: $counter",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                // Увеличиваем счётчик на 1 при нажатии
                counter++
                // Compose автоматически обновит UI
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Нажми меня!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PointerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MyEmptyAppScreen()
        }
    }
}
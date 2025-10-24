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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    Column(
        modifier = Modifier.fillMaxSize(), // Занимает всё доступное пространство
        horizontalAlignment = Alignment.CenterHorizontally, // Центрирует содержимое по горизонтали
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center // Размещает элементы по центру вертикально
    ) {
        Button(
            onClick = { /* TODO Обработать нажатие на кнопку */ },
            modifier = Modifier
                .padding(16.dp) // Добавляем отступы
        ) {
            Text(text = "Нажми меня!") // Текст на кнопке
        }
        // Можно добавить Spacer для дополнительного расстояния, если нужно
        Spacer(modifier = Modifier.height(16.dp))
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
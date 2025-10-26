package ru.dwaidwa.pointer.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onResetClick: suspend () -> Unit,
    onBackClick: () -> Unit,
    loadTimestamps: suspend () -> List<String> // Принимаем функцию загрузки
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isResetting by remember { mutableStateOf(false) }

    // Загружаем метки времени
    val timestampsState = produceState(initialValue = emptyList<String>(), producer = {
        value = loadTimestamps()
    })

    val timestamps = timestampsState.value
    var isRefreshing by remember { mutableStateOf(false) } // Для обновления списка после сброса

    // Обновляем список, если isRefreshing изменился на true
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            timestampsState.let { loadTimestamps() }
            isRefreshing = false // Сбрасываем флаг
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад"
            )
        }

        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопка сброса
        Button(
            onClick = {
                isResetting = true
                scope.launch(Dispatchers.IO) {
                    onResetClick()
                    isResetting = false
                    isRefreshing = true // Запрашиваем обновление списка
                }
            },
            enabled = !isResetting,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            if (isResetting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Сбросить счётчик за сегодня")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Метки времени нажатий сегодня:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Отображаем список меток времени
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Занимает оставшееся место
        ) {
            items(timestamps) { timestampStr ->
                val instant = try { Instant.parse(timestampStr) } catch (e: Exception) { null }
                val formattedTime = instant?.atZone(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) ?: "Invalid Time"
                Text(
                    text = formattedTime,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                )
            }
            if (timestamps.isEmpty()) {
                item {
                    Text(
                        text = "Нет нажатий за сегодня.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
package ru.dwaidwa.pointer.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.dwaidwa.pointer.data.SettingsDataStore
import ru.dwaidwa.pointer.ui.theme.LocalAppTheme
import ru.dwaidwa.pointer.ui.theme.LocalThemeSetter
import ru.dwaidwa.pointer.ui.theme.MyAppTheme
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
    navController: NavController,
    loadTimestamps: suspend () -> List<String>
) {
    val context = LocalContext.current // LocalContext должен быть доступен
    val settingsDataStore = SettingsDataStore(context) // Создаём DataStore
    val scope = rememberCoroutineScope()

    // Получаем значения из CompositionLocal
    val currentTheme = LocalAppTheme.current
    val setTheme = LocalThemeSetter.current // Это функция

    var isResetting by remember { mutableStateOf(false) }

    var timestampsState by remember { mutableStateOf(emptyList<String>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        timestampsState = loadTimestamps()
        isLoading = false
    }

    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        timestampsState = loadTimestamps()
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Назад"
            )
        }

        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Тема оформления:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )

        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { setTheme(MyAppTheme.LIGHT) }, // Вызов setTheme из LocalThemeSetter
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentTheme == MyAppTheme.LIGHT) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(text = "Светлая")
            }
            Button(
                onClick = { setTheme(MyAppTheme.DARK) }, // Вызов setTheme из LocalThemeSetter
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentTheme == MyAppTheme.DARK) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(text = "Тёмная")
            }
            Button(
                onClick = { setTheme(MyAppTheme.SYSTEM) }, // Вызов setTheme из LocalThemeSetter
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentTheme == MyAppTheme.SYSTEM) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(text = "Системная")
            }
        }

        Button(
            onClick = {
                if (!isResetting) {
                    isResetting = true
                    scope.launch(Dispatchers.IO) {
                        onResetClick()
                        refreshTrigger++
                        isResetting = false
                    }
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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).wrapContentSize(Alignment.Center)) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(timestampsState) { timestampStr ->
                    val instant = try { Instant.parse(timestampStr) } catch (e: Exception) { null }
                    val formattedTime = instant?.atZone(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) ?: "Invalid Time"
                    Text(
                        text = formattedTime,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                    )
                }
                if (timestampsState.isEmpty()) {
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
}

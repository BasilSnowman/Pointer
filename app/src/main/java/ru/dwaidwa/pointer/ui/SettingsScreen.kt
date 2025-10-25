package ru.dwaidwa.pointer.ui

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onResetClick: suspend () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isResetting by remember { mutableStateOf(false) }

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
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                isResetting = true
                scope.launch(Dispatchers.IO) {
                    onResetClick()
                    isResetting = false
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
    }
}
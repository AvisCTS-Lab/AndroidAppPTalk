package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.dialog.ConfirmDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ChatUiMessage(
    val text: String,
    val time: LocalTime,
    val fromUser: Boolean
)

@Composable
fun DeviceChatDetailScreen(
    navController: NavController,
    date: LocalDate = LocalDate.of(2025, 12, 23)
) {
    var showConfirm by remember { mutableStateOf(false) }

    ConfirmDialog(
        show = showConfirm,
        title = "Xác nhận",
        message = "Bạn có chắc chắn muốn xóa lịch sử trò chuyện này?",
        onDismiss = { showConfirm = false },
        onConfirm = { /* TODO: delete chat history */ }
    )

    Column(Modifier.fillMaxSize()) {
        BaseTopAppBar(title = "Lịch sử trò chuyện") { navController.popBackStack() }

        // Date header row + actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* TODO: mark helpful */ }) {
                Icon(Icons.Filled.SaveAlt, contentDescription = "Save", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {
                showConfirm = true
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            val messages = remember(date) {
                listOf(
                    ChatUiMessage("Câu hỏi của người dùng...", LocalTime.of(12, 30), true),
                    ChatUiMessage("Câu trả lời của PTalk...", LocalTime.of(12, 37), false),
                    ChatUiMessage("Câu hỏi của người dùng...", LocalTime.of(18, 0), true),
                    ChatUiMessage("Câu trả lời của PTalk...", LocalTime.of(18, 7), false)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(messages) { msg ->
                    MessageBubbleRow(message = msg)
                }
            }
        }
    }
}

@Composable
private fun MessageBubbleRow(message: ChatUiMessage) {
    val timeText = message.time.format(DateTimeFormatter.ofPattern("HH:mm"))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.Start else Arrangement.End
    ) {
        Column(horizontalAlignment = if (message.fromUser) Alignment.Start else Alignment.End) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
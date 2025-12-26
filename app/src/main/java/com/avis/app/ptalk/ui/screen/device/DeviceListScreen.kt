package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private enum class DeviceStatus { Online, Offline, Sleep }

@Composable
fun DeviceListScreen(
    navController: NavController
) {
    var expanded by remember { mutableStateOf(true) }

    val mockDevices = listOf(
        MockDevice("Tên thiết bị", DeviceStatus.Online, 23),
        MockDevice("Tên thiết bị", DeviceStatus.Online, 23),
        MockDevice("Tên thiết bị", DeviceStatus.Offline, 23),
        MockDevice("Tên thiết bị", DeviceStatus.Sleep, 23),
        MockDevice("Tên thiết bị", DeviceStatus.Online, 23),
        MockDevice("Tên thiết bị", DeviceStatus.Online, 23),
    )

    val deviceCount = mockDevices.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(width = 1.5.dp, color = MaterialTheme.colorScheme.onPrimaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Dot(color = Color(0xFF2ECC71))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$deviceCount Thiết bị",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private data class MockDevice(
    val name: String,
    val status: DeviceStatus,
    val lastSeenMinutes: Int
)

@Composable
private fun DeviceCard(device: MockDevice) {

}

@Composable
private fun Dot(color: Color, size: Int = 8) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
            .height(size.dp)
            .fillMaxWidth(0.0f)
    )
}
package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
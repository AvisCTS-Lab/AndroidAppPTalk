package com.avis.app.ptalk.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.domain.define.DeviceConnectionStatus
import com.avis.app.ptalk.domain.model.Device
import com.avis.app.ptalk.domain.model.DeviceState
import com.avis.app.ptalk.ui.component.StatusDot

@Composable
fun DeviceCard(device: Device, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFCECECE))
                    .fillMaxWidth(0.6f)
                    .aspectRatio(0.85f)
            ) { /* image placeholder */ }

            Spacer(Modifier.height(8.dp))
            Text(device.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Row(verticalAlignment = Alignment.CenterVertically) {
//                val statusColor = when (device.status) {
//                    DeviceConnectionStatus.ONLINE -> Color(0xFF2ECC71)
//                    DeviceConnectionStatus.OFFLINE -> Color.Red
//                    DeviceConnectionStatus.SLEEP -> Color(0xFF999999)
//                }
                val statusColor = Color(0xFF2ECC71)
                StatusDot(color = statusColor)
                Spacer(Modifier.padding(4.dp))
                Text(
//                    when (device.status) {
//                        DeviceConnectionStatus.ONLINE -> "Online"
//                        DeviceConnectionStatus.OFFLINE -> "Offline"
//                        DeviceConnectionStatus.SLEEP -> "Sleep"
//                    },
                    "Online",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Pin thiết bị",
                    modifier = Modifier.size(16.dp),
                )
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Tín hiệu thiết bị",
                    modifier = Modifier.size(16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTimeFilled,
                        contentDescription = "Thời gian online",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "${device.lastSeenMinutes}min",
//                        style = MaterialTheme.typography.bodySmall,
//                    )
                }

            }
        }
    }
}
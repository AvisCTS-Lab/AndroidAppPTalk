package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.domain.define.DeviceConnectionStatus
import com.avis.app.ptalk.domain.model.DeviceState
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.card.DeviceCard

val mockDeviceState = DeviceState("Tên thiết bị", DeviceConnectionStatus.ONLINE, 23)

@Composable
fun RealTimeControlScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        ) {
        BaseTopAppBar(
            title = "Điều khiển Real-time",
            onBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.padding(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                DeviceCard(mockDeviceState)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            // Volume section
            Text("Âm lượng", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "Volume")
                Slider(
                    value = 0.5f,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Toggles
            LabeledSwitchRow(
                label = "Hiển thị màn hình",
                checked = true,
                onCheckedChange = { }
            )
            LabeledSwitchRow(
                label = "Khóa thiết bị",
                checked = false,
                onCheckedChange = { }
            )
            // Destructive action button
            Button(
                onClick = { /* UI-only: restart action */ },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE3E3),   // soft red background
                    contentColor = Color(0xFFEB5757)      // red text
                )
            ) {
                Text("Restart thiết bị", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun LabeledSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IncompleteCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar

val MockDevicesDiscovery = listOf(
    DeviceDiscovery("Thiết bị 1", "00:00:00:00:00:01", -50),
    DeviceDiscovery("Thiết bị 2", "00:00:00:00:00:02", -10),
    DeviceDiscovery("Thiết bị 3", "00:00:00:00:00:03", -20),
    DeviceDiscovery("Thiết bị 4", "00:00:00:00:00:04", -50),
)

val MockConnectedDevices = listOf(
    DeviceDiscovery("Thiết bị 5", "00:00:00:00:00:01", -50),

)

@Composable
fun AddDeviceScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BaseTopAppBar(
            title = "Thêm thiết bị",
            onBack = {
                navController.popBackStack()
            }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F4E1),
                contentColor = MaterialTheme.colorScheme.scrim
            ),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Bluetooth",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Bật Bluetooth để tìm thiết bị",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Connected device section
        Text(
            "Thiết bị đã kết nối",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MockConnectedDevices.size) { index ->
                DeviceDiscoverCard(device = MockConnectedDevices[index], isConnected = true)
            }
        }

        // Device discovered section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Các thiết bị xung quanh",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.IncompleteCircle, contentDescription = "Settings",
                modifier = Modifier.size(20.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            items(MockDevicesDiscovery.size) { index ->
                DeviceDiscoverCard(device = MockDevicesDiscovery[index])
            }
        }
    }
}

data class DeviceDiscovery(
    val name: String,
    val address: String,
    val rssi: Int
)

@Composable
private fun DeviceDiscoverCard(
    device: DeviceDiscovery,
    onClick: () -> Unit = {},
    isConnected: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(22.dp)
        ) {
       Row (
           verticalAlignment = Alignment.CenterVertically,
           modifier = Modifier.padding(16.dp)
       ) {
           Text(device.name,
               color = if (isConnected) Color(0xFF2ECC71) else MaterialTheme.colorScheme.onSurface,
               style = MaterialTheme.typography.bodyLarge,
               fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
               modifier = Modifier.weight(1f))
           Icon(
               imageVector = Icons.Filled.Info,
               contentDescription = "Info"
           )
       }
    }
}
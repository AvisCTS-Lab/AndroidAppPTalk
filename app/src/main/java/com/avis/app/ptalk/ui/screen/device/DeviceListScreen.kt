package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.StatusDot
import com.avis.app.ptalk.ui.viewmodel.DeviceConnectionStatus
import com.avis.app.ptalk.ui.viewmodel.DeviceState
import com.avis.app.ptalk.ui.viewmodel.VMDeviceList

@Composable
fun DeviceListScreen(
    navController: NavController,
    vm: VMDeviceList = viewModel()
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        vm.loadDeviceList()
    }

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
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(width = 1.5.dp, color = MaterialTheme.colorScheme.onPrimaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
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
                    StatusDot(color = Color(0xFF2ECC71), size = 8)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${vm.getDeviceCount()} Thiết bị",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { vm.toggleListExpanded() }
            ) {
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = "Expand more",
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Thiết bị đã liên kết",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton (
                onClick = {
                    navController.navigate(Route.ADD_DEVICE)
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Thêm thiết bị")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.devices.size) { index ->
                DeviceCard(device = uiState.devices[index])
            }
        }
    }
}

@Composable
private fun DeviceCard(device: DeviceState, onClick: () -> Unit = {}) {
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
                val statusColor = when (device.status) {
                    DeviceConnectionStatus.ONLINE -> Color(0xFF2ECC71)
                    DeviceConnectionStatus.OFFLINE -> Color.Red
                    DeviceConnectionStatus.SLEEP -> Color(0xFF999999)
                }
                StatusDot(color = statusColor)
                Spacer(Modifier.padding(4.dp))
                Text(
                    when (device.status) {
                        DeviceConnectionStatus.ONLINE -> "Online"
                        DeviceConnectionStatus.OFFLINE -> "Offline"
                        DeviceConnectionStatus.SLEEP -> "Sleep"
                    },
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
                    Text(
                        text = "${device.lastSeenMinutes}min",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

            }
        }
    }
}
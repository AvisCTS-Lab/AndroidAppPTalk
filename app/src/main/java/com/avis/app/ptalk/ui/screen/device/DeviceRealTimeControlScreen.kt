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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.card.DeviceCard
import com.avis.app.ptalk.ui.viewmodel.VMRealTimeControl
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun RealTimeControlScreen(
    navController: NavController,
    shareVmDevice: ShareVMDevice,
    vm: VMRealTimeControl = hiltViewModel(),
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        vm.getDeviceControlState(shareVmDevice.device.value!!.macAddress)
    }

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
                DeviceCard(shareVmDevice.device.value!!)
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
                    value = uiState.volume,
                    onValueChange = { vm.setVolume(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Toggles
            LabeledSwitchRow(
                label = "Hiển thị màn hình",
                checked = uiState.showScreen,
                onCheckedChange = { vm.toggleShowScreen() }
            )
            LabeledSwitchRow(
                label = "Khóa thiết bị",
                checked = uiState.lockDevice,
                onCheckedChange = { vm.toggleLockDevice() }
            )
            // Destructive action button
            Button(
                onClick = { vm.restartDevice() },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
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
package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.card.DetailDeviceCard
import com.avis.app.ptalk.ui.viewmodel.VMRealTimeControl
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun RealTimeControlScreen(
    navController: NavController,
    shareVmDevice: ShareVMDevice,
    vm: VMRealTimeControl = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val isConnected by vm.isConnected.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showRenameDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    var showOtaDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shareVmDevice.device.value?.let {
            // Use deviceId (from BLE UUID 0xFF0A) instead of macAddress for WebSocket
            // Check for both null and empty string
            val targetId = if (!it.deviceId.isNullOrBlank()) it.deviceId else it.macAddress
            vm.initDevice(targetId)
        }
    }

    // Show error/success messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
    }

    // Dialogs
    if (showRenameDialog) {
        RenameDeviceDialog(
            currentName = uiState.deviceName,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                vm.setDeviceName(newName)
                showRenameDialog = false
            }
        )
    }

    if (showRestartDialog) {
        ConfirmActionDialog(
            title = "Khởi động lại thiết bị",
            message = "Thiết bị sẽ khởi động lại và mất kết nối tạm thời. Tiếp tục?",
            onDismiss = { showRestartDialog = false },
            onConfirm = {
                vm.restartDevice()
                showRestartDialog = false
            }
        )
    }

    if (showOtaDialog) {
        ConfirmActionDialog(
            title = "Cập nhật Firmware",
            message = "Kiểm tra và cập nhật phiên bản firmware mới nhất cho thiết bị?",
            onDismiss = { showOtaDialog = false },
            onConfirm = {
                vm.requestOtaUpdate()
                showOtaDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),

            ) {
            BaseTopAppBar(
                title = "Điều khiển Real-time",
                onBack = { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.padding(8.dp))

            // Device card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                shareVmDevice.device.value?.let {
                    Row(modifier = Modifier.fillMaxWidth(0.5f)) {
                        DetailDeviceCard(it)
                    }
                }

                // Connection status
                ConnectionStatusBadge(isConnected = isConnected)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                // Volume section
                ControlSliderSection(
                    title = "Âm lượng",
                    value = uiState.volume,
                    onValueChange = { vm.setVolume(it) },
                    onValueChangeFinished = { vm.applyVolume() },
                    leadingIcon = Icons.AutoMirrored.Filled.VolumeDown,
                    trailingIcon = Icons.AutoMirrored.Filled.VolumeUp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Brightness section
                ControlSliderSection(
                    title = "Độ sáng màn hình",
                    value = uiState.brightness,
                    onValueChange = { vm.setBrightness(it) },
                    onValueChangeFinished = { vm.applyBrightness() },
                    leadingIcon = Icons.Default.Brightness6,
                    trailingIcon = Icons.Default.Brightness6
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Text(
                    text = "Hành động",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ActionButtonsRow(
                    onRestart = { showRestartDialog = true },
                    onOta = { showOtaDialog = true },
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ConnectionStatusBadge(isConnected: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (isConnected) Icons.Default.Check else Icons.Default.Error,
            contentDescription = null,
            tint = if (isConnected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (isConnected) "Đã kết nối" else "Chưa kết nối",
            style = MaterialTheme.typography.bodySmall,
            color = if (isConnected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        }
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ControlSliderSection(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(24.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            Icon(trailingIcon, contentDescription = null, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onRestart: () -> Unit,
    onOta: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onRestart,
            enabled = !isLoading,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Icon(
                Icons.Default.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text("Khởi động lại", modifier = Modifier.padding(start = 4.dp))
        }

        OutlinedButton(
            onClick = onOta,
            enabled = !isLoading,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Icon(
                Icons.Default.SystemUpdate,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text("Cập nhật OTA", modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
private fun RenameDeviceDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi tên thiết bị") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Tên mới") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newName) },
                enabled = newName.isNotBlank()
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun ConfirmActionDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.textButtonColors()
                }
            ) {
                Text(if (isDestructive) "Xác nhận" else "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
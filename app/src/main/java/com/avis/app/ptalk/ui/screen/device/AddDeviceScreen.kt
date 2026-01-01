package com.avis.app.ptalk.ui.screen.device

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.dialog.EnterDeviceConfigDialog
import com.avis.app.ptalk.ui.component.dialog.ErrorDialog
import com.avis.app.ptalk.ui.component.dialog.LoadingDialog
import com.avis.app.ptalk.ui.component.dialog.SuccessDialog
import com.avis.app.ptalk.ui.viewmodel.VMAddDevice

@Composable
fun AddDeviceScreen(navController: NavController, vm: VMAddDevice = hiltViewModel()) {
    val uiState by vm.ui.collectAsState()

    var permissionsGranted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showWifiDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        permissionsGranted = granted
    }

    fun requestBlePermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(perms)
    }

    // Start scan on launch
    LaunchedEffect(Unit) { requestBlePermissions() }

    LoadingDialog(
        show = isLoading,
        message = "Đang kết nối với thiết bị",
        onDismiss = { isLoading = false }
    )

    EnterDeviceConfigDialog(
        show = showWifiDialog,
        onDismiss = {
            vm.disconnectDevice()
            showWifiDialog = false
        },
        onSubmit = { ssid, pass ->
            isLoading = true
            showWifiDialog = false
            vm.connectWifi(ssid, pass) {
                isLoading = false
                showWifiDialog = false
                showSuccess = true
            }
        }
    )

    SuccessDialog(
        show = showSuccess,
        title = "Thành công",
        message = "Thiết lập thiết bị thành công",
        confirmText = "Hoàn thành",
        onDismiss = {
            vm.disconnectDevice()
            showSuccess = false
            showWifiDialog = false
        }
    )

    ErrorDialog(
        show = showError,
        title = "Lỗi",
        message = "Không thể kết nối với thiết bị",
        onDismiss = {
            vm.disconnectDevice()
            showError = false
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        BaseTopAppBar(
            title = "Thêm thiết bị",
            onBack = { navController.popBackStack() }
        )

        if (!permissionsGranted) {
            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F4E1),
                    contentColor = MaterialTheme.colorScheme.scrim
                ),
                shape = RoundedCornerShape(20.dp),
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
                        text = "Bật Bluetooth và cấp quyền để tìm thiết bị",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))
        Text(
            "Thiết bị tìm thấy",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Discovered devices list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.devices.isEmpty() && !uiState.scanning) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            "Không có thiết bị nào. Nhấn “Quét thiết bị” để bắt đầu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(uiState.devices, key = { it.address }) { dev ->
                    DeviceRow(
                        device = dev,
                        onClick = {
                            isLoading = true
                            vm.stopScan()
                            vm.connectDevice(dev.address) {
                                showWifiDialog = true
                                isLoading = false
                            }
                        }
                    )
                }
            }
        }

        // Scan controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (uiState.scanning) {
                Button(onClick = { vm.stopScan() }) {
                    Text("Dừng quét")
                }
            } else {
                Button(onClick = {
                    if (!permissionsGranted) requestBlePermissions()
                    else vm.startScan()
                }) {
                    Icon(Icons.AutoMirrored.Filled.BluetoothSearching, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Quét thiết bị")
                }
            }
        }

        if (uiState.error != null) {
            Text(
                text = "Lỗi: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun DeviceRow(
    device: ScannedDevice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = device.name ?: "(Không tên)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "RSSI: ${device.rssi}",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End
                )
            }
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
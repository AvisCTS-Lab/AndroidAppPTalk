package com.avis.app.ptalk.ui.screen.device

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.domain.control.WifiNetwork
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.dialog.BaseDialog
import com.avis.app.ptalk.ui.component.dialog.ErrorDialog
import com.avis.app.ptalk.ui.component.dialog.LoadingDialog
import com.avis.app.ptalk.ui.component.dialog.SuccessDialog
import com.avis.app.ptalk.ui.custom.DialogPosition
import com.avis.app.ptalk.ui.viewmodel.VMAddDevice

@Composable
fun AddDeviceScreen(navController: NavController, vm: VMAddDevice = hiltViewModel()) {
    val uiState by vm.ui.collectAsState()

    var permissionsGranted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showWifiDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        permissionsGranted = granted
    }

    fun requestBlePermissions() {
        val perms =
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
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
        wifiNetworks = uiState.wifiNetworks,
        loadingWifiList = uiState.loadingWifiList,
        deviceId = uiState.deviceId,
        onRefreshWifi = { vm.refreshWifiList() },
        onDismiss = {
            vm.disconnectDevice()
            showWifiDialog = false
        },
        onSubmit = { ssid, pass, volume, brightness ->
            isLoading = true
            showWifiDialog = false
            vm.configDeviceOnConnect(
                ssid,
                pass,
                volume,
                brightness,
                object : VMAddDevice.DeviceConfigCallback {
                    override fun onConfigSaved() {
                        isLoading = false
                        showSuccess = true
                    }

                    override fun onConfigError(error: String) {
                        isLoading = false
                        showError = true
                        errorMessage = error
                    }
                })
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
            navController.popBackStack()
        },
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

@Composable
private fun EnterDeviceConfigDialog(
    show: Boolean,
    wifiNetworks: List<WifiNetwork> = emptyList(),
    loadingWifiList: Boolean = false,
    deviceId: String = "",
    position: DialogPosition = DialogPosition.BOTTOM,
    onRefreshWifi: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onSubmit: (String, String, Float, Float) -> Unit = { _, _, _, _ -> }
) {
    if (!show) return

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var wifiSsid by remember { mutableStateOf("") }
    var wifiPass by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(0.5f) }
    var brightness by remember { mutableStateOf(0.5f) }
    var showWifiDropdown by remember { mutableStateOf(false) }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    BaseDialog(
        onDismiss = onDismiss,
        position = position,
        modifier = Modifier.padding(8.dp),
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // let content control width
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title + Close
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Thiết lập tùy chỉnh thiết bị",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Device ID (if available)
                if (deviceId.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Device ID: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = deviceId,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Wifi Ssid with dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tên WiFi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (loadingWifiList) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onRefreshWifi, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh WiFi",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = wifiSsid,
                        onValueChange = { wifiSsid = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        placeholder = { Text(if (wifiNetworks.isEmpty()) "Nhập tên WiFi" else "Chọn hoặc nhập tên WiFi") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Wifi,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (wifiNetworks.isNotEmpty()) {
                                IconButton(onClick = { showWifiDropdown = !showWifiDropdown }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Show WiFi list"
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        shape = RoundedCornerShape(22.dp),
                    )

                    DropdownMenu(
                        expanded = showWifiDropdown,
                        onDismissRequest = { showWifiDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .heightIn(max = 200.dp)
                    ) {
                        wifiNetworks.forEach { network ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = network.ssid,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.SignalWifi4Bar,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = when {
                                                    network.rssi >= -50 -> Color(0xFF4CAF50)
                                                    network.rssi >= -70 -> Color(0xFFFFC107)
                                                    else -> Color(0xFFF44336)
                                                }
                                            )
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text(
                                                text = "${network.rssi}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    wifiSsid = network.ssid
                                    showWifiDropdown = false
                                }
                            )
                        }
                    }
                }

                // Wifi pass
                Text(
                    text = "Mật khẩu WiFi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                OutlinedTextField(
                    value = wifiPass,
                    onValueChange = { wifiPass = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập mật khẩu") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Show password"
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                )

                Text("Âm lượng", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "Volume")
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Text("Độ sáng màn hình", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.BrightnessMedium, contentDescription = "Volume")
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            "Hủy",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSubmit(wifiSsid, wifiPass, volume, brightness)
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Lưu tùy chỉnh")
                    }
                }
            }
        }
    }
}
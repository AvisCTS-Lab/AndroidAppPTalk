package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.FunctionCard
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.dialog.ConfirmDialog
import com.avis.app.ptalk.ui.viewmodel.VMDeviceSetting
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun DeviceSettingScreen(
    navController: NavController,
    shareVmDevice: ShareVMDevice,
    vm: VMDeviceSetting = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shareVmDevice.device.value?.let {
            // Use deviceId (from BLE UUID 0xFF0A) instead of macAddress for WebSocket
            // Check for both null and empty string
            val targetId = if (!it.deviceId.isNullOrBlank()) it.deviceId else it.macAddress
            vm.initDevice(targetId, it.macAddress)
        }
    }

    // Navigate back when device is unlinked
    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            vm.clearNavigateBack()
            shareVmDevice.clearDevice()
            navController.popBackStack(Route.DEVICE, inclusive = false)
        }
    }

    // Show messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
    }

    // Delete confirm dialog
    ConfirmDialog(
        show = showDeleteConfirm,
        title = "Hủy liên kết thiết bị",
        message = "Thiết bị sẽ được khôi phục cài đặt gốc và xóa khỏi ứng dụng. Bạn sẽ cần cấu hình lại thiết bị sau này. Tiếp tục?",
        onDismiss = { showDeleteConfirm = false },
        onConfirm = {
            vm.unlinkDevice()
            showDeleteConfirm = false
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {


        Column(
            Modifier.fillMaxWidth()


        ) {
            BaseTopAppBar(
                title = "Cài đặt thiết bị",
                onBack = { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.padding(12.dp))

            // Connection status
            if (!uiState.isConnected) {
                Text(
                    text = "⚠️ Chưa kết nối đến server",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FunctionCard(
                    icon = Icons.Filled.DeleteForever,
                    label = "Hủy liên kết thiết bị",
                    onClick = { showDeleteConfirm = true }
                )
            }
        }

        // Loading indicator
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
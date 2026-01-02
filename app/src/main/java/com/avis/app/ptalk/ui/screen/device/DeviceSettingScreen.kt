package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.ui.component.FunctionCard
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun DeviceSettingScreen(
    navController: NavController,
    shareVmDevice: ShareVMDevice
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        BaseTopAppBar(title = "Cài đặt thiết bị") { navController.popBackStack() }
        Spacer(modifier = Modifier.padding(12.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12 .dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FunctionCard(
                icon = Icons.Filled.SystemUpdate,
                label = "Firmware update (OTA)",
                onClick = { /* TODO: navigate to OTA */ }
            )
            FunctionCard(
                icon = Icons.Filled.RestartAlt,
                label = "Reset thiết bị",
                onClick = { /* TODO: send reset command */ }
            )
            FunctionCard(
                icon = Icons.Filled.SettingsBackupRestore,
                label = "Khôi phục cài đặt gốc",
                onClick = { /* TODO: factory reset */ }
            )
            FunctionCard(
                icon = Icons.Filled.DeleteForever,
                label = "Hủy liên kết thiết bị",
                onClick = { /* TODO: unlink device from local db/cloud */ }
            )
        }
    }
}
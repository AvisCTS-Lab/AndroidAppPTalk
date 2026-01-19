package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.component.appbar.MainNavigationBar
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun DeviceInfoScreen(
    navController: NavController,
    shareVmDevice: ShareVMDevice
) {
    val device = shareVmDevice.device.value

    Column(Modifier.fillMaxSize()) {
        BaseTopAppBar(
            title = "Thông tin thiết bị",
            onBack = { navController.popBackStack() }
        )
        Spacer(Modifier.padding(8.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeviceInfoCard(
                title = "Tên thiết bị",
                value = device?.name ?: "—"
            )
            DeviceInfoCard(
                title = "Địa chỉ MAC",
                value = device?.macAddress ?: "—"
            )
            DeviceInfoCard(
                title = "Phiên bản ứng dụng (FW/App)",
                value = device?.appVersion ?: "—"
            )
            DeviceInfoCard(
                title = "Thông tin build",
                value = device?.buildInfo ?: "—"
            )

            Spacer(Modifier.height(8.dp))
        }

        MainNavigationBar(navController = navController)
    }
}

@Composable
private fun DeviceInfoCard(
    title: String,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
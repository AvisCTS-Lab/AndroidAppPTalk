package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.FunctionCard
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

@Composable
fun DeviceDetailScreen(navController: NavController, shareVMDevice: ShareVMDevice) {
    val device = if (shareVMDevice.device.value != null) {
        shareVMDevice.device.value!!.name
    } else {
        "PTalk Device"
    }

    Column(modifier = Modifier.fillMaxWidth(),) {
        BaseTopAppBar(
            title = device,
            onBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.padding(12.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FunctionCard(Icons.Filled.Radio, "Điều khiển real-time", onClick = {
                navController.navigate(Route.REALTIME_CONTROL)
            })
            FunctionCard(Icons.Filled.MoreHoriz, "Lịch sử trò chuyện", onClick = {
                navController.navigate(Route.DEVICE_CHATLOG)
            })
            FunctionCard(Icons.Filled.Info, "Thông tin thiết bị", onClick = {
                navController.navigate(Route.DEVICE_INFO)
            })
            FunctionCard(Icons.Filled.Settings, "Cài đặt thiết bị", onClick = {
                navController.navigate(Route.DEVICE_SETTING)
            })
        }
    }
}
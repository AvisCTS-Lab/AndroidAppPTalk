package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar

@Composable
fun DeviceDetailScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),

    ) {
        BaseTopAppBar(
            title = "Tên thiết bị",
            onBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.padding(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FunctionCard(Icons.Outlined.Radio, "Điều khiển real-time", onClick = {
                navController.navigate(Route.REALTIME_CONTROL)
            })
            FunctionCard(Icons.Outlined.MoreHoriz, "Lịch sử trò chuyện")
            FunctionCard(Icons.Outlined.MoreHoriz, "Thông tin thiết bị")
            FunctionCard(Icons.Outlined.MoreHoriz, "Cài đặt thiết bị")
        }
    }
}

@Composable
private fun FunctionCard(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier
            )
        }


    }
}
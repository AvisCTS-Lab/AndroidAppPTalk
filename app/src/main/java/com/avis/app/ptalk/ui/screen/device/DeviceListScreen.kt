package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.dialog.BaseDialog
import com.avis.app.ptalk.ui.custom.DialogPosition
import com.avis.app.ptalk.ui.viewmodel.VMDeviceList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    navController: NavController,
    vm: VMDeviceList = hiltViewModel(),
) {
    val ui = vm.uiState.collectAsStateWithLifecycle().value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("PTalk Assistant", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = @Composable {
                    IconButton(onClick = { navController.navigate(Route.DEVICE_ADDDEVICE) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add device"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {

            }
        }
    }
}
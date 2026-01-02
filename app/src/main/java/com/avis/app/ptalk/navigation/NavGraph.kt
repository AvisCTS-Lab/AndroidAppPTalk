package com.avis.app.ptalk.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.avis.app.ptalk.ui.screen.auth.LoginScreen
import com.avis.app.ptalk.ui.screen.auth.SignupScreen
import com.avis.app.ptalk.ui.screen.device.AddDeviceScreen
import com.avis.app.ptalk.ui.screen.device.DeviceChatDetailScreen
import com.avis.app.ptalk.ui.screen.device.DeviceChatLogScreen
import com.avis.app.ptalk.ui.screen.device.DeviceDetailScreen
import com.avis.app.ptalk.ui.screen.device.DeviceInfoScreen
import com.avis.app.ptalk.ui.screen.device.DeviceListScreen
import com.avis.app.ptalk.ui.screen.device.DeviceSettingScreen
import com.avis.app.ptalk.ui.screen.device.RealTimeControlScreen
import com.avis.app.ptalk.ui.viewmodel.share.ShareVMDevice

const val ANIM_DURATION = 300

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val shareVMDevice = viewModel<ShareVMDevice>()


    NavHost(
        navController = navController,
        startDestination = Route.LOGIN,
        modifier = modifier,
        // Slide animation
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIM_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIM_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIM_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIM_DURATION)
            )
        }
    ) {
        composable(Route.LOGIN) { LoginScreen(navController) }
        composable(Route.SIGNUP) { SignupScreen(navController) }
        composable(Route.DEVICE) { DeviceListScreen(navController, shareVMDevice) }
        composable(Route.DEVICE_ADDDEVICE) { AddDeviceScreen(navController) }
        composable(Route.DEVICE_DETAIL) { DeviceDetailScreen(navController, shareVMDevice) }
        composable(Route.REALTIME_CONTROL) { RealTimeControlScreen(navController, shareVMDevice) }
        composable(Route.DEVICE_CHATLOG) { DeviceChatLogScreen(navController) }
        composable(Route.DEVICE_CHATDETAIL) { DeviceChatDetailScreen(navController) }
        composable(Route.DEVICE_INFO) { DeviceInfoScreen(navController, shareVMDevice) }
        composable(Route.DEVICE_SETTING) { DeviceSettingScreen(navController, shareVMDevice) }
    }
}
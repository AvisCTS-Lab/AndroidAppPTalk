package com.avis.app.ptalk.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avis.app.ptalk.ui.screen.auth.ForgotPasswordScreen
import com.avis.app.ptalk.ui.screen.auth.LoginScreen
import com.avis.app.ptalk.ui.screen.auth.OtpVerifyScreen
import com.avis.app.ptalk.ui.screen.auth.ResetPasswordScreen
import com.avis.app.ptalk.ui.screen.auth.SignUpScreen
import com.avis.app.ptalk.ui.screen.device.AddDeviceScreen
import com.avis.app.ptalk.ui.screen.device.DeviceChatDetailScreen
import com.avis.app.ptalk.ui.screen.device.DeviceChatLogScreen
import com.avis.app.ptalk.ui.screen.device.DeviceDetailScreen
import com.avis.app.ptalk.ui.screen.device.DeviceInfoScreen
import com.avis.app.ptalk.ui.screen.device.DeviceListScreen
import com.avis.app.ptalk.ui.screen.device.DeviceSettingScreen
import com.avis.app.ptalk.ui.screen.device.RealTimeControlScreen
import com.avis.app.ptalk.ui.screen.setting.ServerSettingsScreen
import com.avis.app.ptalk.ui.screen.common.UnderDevelopmentScreen
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
        composable(Route.SIGNUP) { SignUpScreen(navController) }
        composable(Route.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }
        composable(Route.VERIFY_OTP + "?phone={phone}", arguments = listOf(navArgument("phone") {
            type = NavType.StringType;
            defaultValue = "";
        })) { OtpVerifyScreen(navController) }
        composable(
            Route.RESET_PASSWORD + "?phone={phone}&otp={otp}", arguments = listOf(
            navArgument("phone") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("otp") {
                type = NavType.StringType
                defaultValue = ""
            }
        )) { ResetPasswordScreen(navController) }
        composable(Route.DEVICE) { DeviceListScreen(navController) }
        composable(Route.DEVICE_ADDDEVICE) { AddDeviceScreen(navController) }
        composable(Route.DEVICE_DETAIL) { DeviceDetailScreen(navController, shareVMDevice) }
        composable(Route.REALTIME_CONTROL) { RealTimeControlScreen(navController, shareVMDevice) }
        composable(Route.DEVICE_CHATLOG) { DeviceChatLogScreen(navController) }
        composable(Route.DEVICE_CHATDETAIL) { DeviceChatDetailScreen(navController) }
        composable(Route.DEVICE_INFO) { DeviceInfoScreen(navController, shareVMDevice) }
        composable(Route.DEVICE_SETTING) { DeviceSettingScreen(navController, shareVMDevice) }
        composable(Route.SERVER_SETTINGS) { ServerSettingsScreen(navController) }

        // Under development screens
        composable(Route.BAN_KEYWORD) { UnderDevelopmentScreen(navController, "Chặn từ khóa") }
        composable(Route.ANALYTICS) { UnderDevelopmentScreen(navController, "Thống kê") }
        composable(Route.SETTING) { UnderDevelopmentScreen(navController, "Cài đặt") }
    }
}

fun String.baseRoute(): String =
    this.substringBefore("?")     // strip query part e.g. otp?phone={phone} -> otp
        .substringBefore("/{")    // strip path args e.g. device/{id} -> device

fun NavDestination.baseRouteOrNull(): String? =
    this.route?.baseRoute()
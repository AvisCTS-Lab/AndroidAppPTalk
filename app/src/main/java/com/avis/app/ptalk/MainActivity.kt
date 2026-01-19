package com.avis.app.ptalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.avis.app.ptalk.navigation.AppNavGraph
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.appbar.MainNavigationBar
import com.avis.app.ptalk.ui.theme.AndroidPTalkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import org.thingai.android.module.meo.MeoSdk
import org.thingai.base.log.ILog
import org.thingai.meo.common.callback.RequestCallback

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ILog.logLevel = ILog.DEBUG
        ILog.ENABLE_LOGGING = true

        MeoSdk.init(this.applicationContext)

        // Install the platform splash screen and keep it until auth check completes
        val splashScreen = installSplashScreen()
        var keepOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepOnScreen }

        // Flow to hold authentication result; null = unknown/loading
        val authStateFlow = MutableStateFlow<Boolean?>(null)

        // Kick off connect; update authStateFlow and release splash when done
        MeoSdk.connect(object : RequestCallback<Boolean> {
            override fun onSuccess(p0: Boolean?, p1: String?) {
                runOnUiThread {
                    authStateFlow.value = p0 ?: false
                    keepOnScreen = false
                }
            }

            override fun onFailure(p0: Int, p1: String?) {
                runOnUiThread {
                    authStateFlow.value = false
                    keepOnScreen = false
                }
            }
        })

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            // collect auth state into Compose
            val authState by authStateFlow.collectAsStateWithLifecycle()

            // When authState becomes known, navigate accordingly once
            LaunchedEffect(authState) {
                if (authState == true) {
                    ILog.d("MainActivity", "Authenticated")
                    navController.navigate(Route.DEVICE) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = false }
                    }
                } else if (authState == false) {
                    ILog.d("MainActivity", "Not authenticated")
                    navController.navigate(Route.LOGIN) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = false }
                    }
                }
            }

            AndroidPTalkTheme(
                content = {
                    Scaffold(
                        bottomBar = {
                            MainNavigationBar(navController = navController)
                        },
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        }
                    ) { innerPadding ->
                        AppNavGraph(navController = navController, modifier = Modifier.padding(paddingValues = innerPadding))
                    }
                }
            )
        }
    }
}
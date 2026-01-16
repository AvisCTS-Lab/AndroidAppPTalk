package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.config.WSConfig
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMServerSetting @Inject constructor(
    private val serverConfig: WSConfig,
    private val webSocket: DeviceControlWebSocket
) : ViewModel() {

    data class ServerSettingsUiState(
        val serverHost: String = WSConfig.DEFAULT_HOST,
        val serverPort: Int = WSConfig.DEFAULT_PORT,
        val fullServerUrl: String = "",
        val isConnected: Boolean = false,
        val isLoading: Boolean = false,
        val message: String? = null
    )

    private val _uiState = MutableStateFlow(ServerSettingsUiState())
    val uiState: StateFlow<ServerSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        observeConnection()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                serverHost = serverConfig.getServerHost(),
                serverPort = serverConfig.getServerPort(),
                fullServerUrl = serverConfig.getServerUrl()
            )
        }
    }

    private fun observeConnection() {
        viewModelScope.launch {
            webSocket.connectionState.collect { state ->
                _uiState.update {
                    it.copy(isConnected = state == DeviceControlWebSocket.ConnectionState.CONNECTED)
                }
            }
        }
    }

    fun saveSettings(host: String, port: Int) {
        if (host.isBlank()) {
            _uiState.update { it.copy(message = "Địa chỉ IP không được để trống") }
            return
        }

        if (port <= 0 || port > 65535) {
            _uiState.update { it.copy(message = "Port không hợp lệ (1-65535)") }
            return
        }

        viewModelScope.launch {
            serverConfig.setServerHost(host)
            serverConfig.setServerPort(port)

            _uiState.update {
                it.copy(
                    serverHost = host,
                    serverPort = port,
                    fullServerUrl = serverConfig.getServerUrl(),
                    message = "Đã lưu cài đặt. Đang kết nối lại..."
                )
            }

            // Reconnect with new settings
            webSocket.disconnect()
            delay(500)
            webSocket.connect(serverConfig.getServerUrl())
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                webSocket.disconnect()
                delay(300)
                webSocket.connect(serverConfig.getServerUrl())

                // Wait a bit for connection
                delay(2000)

                val connected =
                    webSocket.connectionState.value == DeviceControlWebSocket.ConnectionState.CONNECTED
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = if (connected) "✅ Kết nối thành công!" else "❌ Không thể kết nối đến server"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "❌ Lỗi: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetToDefaults() {
        serverConfig.resetToDefaults()
        loadSettings()
        _uiState.update { it.copy(message = "Đã đặt lại về mặc định") }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
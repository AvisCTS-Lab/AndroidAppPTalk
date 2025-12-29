package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.avis.app.ptalk.domain.model.DeviceChatLog
import com.avis.app.ptalk.domain.util.DateTimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VMDeviceChatLog: ViewModel() {
    data class DeviceChatLogUiState(
        val query: String = "",
        val chatLogs: List<DeviceChatLog> = emptyList(),

        val loadingChatLogs: Boolean = false
    )

    private val _uiState = MutableStateFlow(DeviceChatLogUiState())
    val uiState: StateFlow<DeviceChatLogUiState> = _uiState.asStateFlow()

    fun handleQueryChange() {
        _uiState.update { it.copy(query = it.query) }
    }

    fun loadChatLog() {
        val mockChatLogs = listOf(
            DeviceChatLog("4", 1767016800000),
            DeviceChatLog("3", 1766880000000),
            DeviceChatLog("2", 1764537600000),
            DeviceChatLog("1", 1764528000000)
        )
        _uiState.update { it.copy(chatLogs = mockChatLogs) }
    }
}
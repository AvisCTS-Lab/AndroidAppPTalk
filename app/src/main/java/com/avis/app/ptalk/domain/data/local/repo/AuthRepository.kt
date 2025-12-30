package com.avis.app.ptalk.domain.data.local.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _loggedIn = MutableStateFlow(false)
    val isLoggedIn: Flow<Boolean> = _loggedIn.asStateFlow()

    fun login(username: String, password: String): Boolean {
        val ok = (username == "demo@ptalk" && password == "demo123")
        _loggedIn.value = ok
        return ok
    }

    fun logout() {
        _loggedIn.value = false
    }
}
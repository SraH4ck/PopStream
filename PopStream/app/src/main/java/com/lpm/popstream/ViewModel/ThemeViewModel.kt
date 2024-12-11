package com.lpm.popstream.ViewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = mutableStateOf(true)
    val isDarkTheme: State<Boolean> get() = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}

package com.kgzn.gamecenter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgzn.gamecenter.feature.settings.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
) : ViewModel() {

    val isAutoInstall: StateFlow<Boolean> = settingsManager.isAutoInstall
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val isClearPackageAfterInstall: StateFlow<Boolean> = settingsManager.isClearPackageAfterInstall
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setAutoInstall(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setAutoInstall(enabled)
        }
    }

    fun setClearPackageAfterInstall(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setClearPackageAfterInstall(enabled)
        }
    }
}

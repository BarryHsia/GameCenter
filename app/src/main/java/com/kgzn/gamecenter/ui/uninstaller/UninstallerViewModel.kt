package com.kgzn.gamecenter.ui.uninstaller

import androidx.lifecycle.ViewModel
import com.kgzn.gamecenter.feature.installer.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UninstallerViewModel @Inject constructor(
    val installManager: InstallManager,
) : ViewModel()

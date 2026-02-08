package com.kgzn.gamecenter.ui.uninstaller

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object UninstallerRoute

fun NavController.navigateToUninstaller(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(UninstallerRoute, navOptions)
}

fun NavGraphBuilder.uninstallerScreen() {
    composable<UninstallerRoute> {
        UninstallerScreen()
    }
}

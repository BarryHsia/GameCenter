package com.kgzn.gamecenter.ui.input

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object InputRoute

fun NavController.navigateInput() {
    navigate(InputRoute)
}

fun NavGraphBuilder.inputScreen() {
    composable<InputRoute> {
        InputScreen()
    }
}

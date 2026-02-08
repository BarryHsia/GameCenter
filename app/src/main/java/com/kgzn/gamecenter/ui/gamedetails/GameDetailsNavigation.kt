package com.kgzn.gamecenter.ui.gamedetails

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kgzn.gamecenter.data.InfoParam
import kotlinx.serialization.Serializable

@Serializable
data class GameDetailsRoute(
    override val configId: Int,
    override val dataId: String,
    override val contentType: Int,
    override val dataType: String,
) : InfoParam {
    constructor(infoParam: InfoParam) : this(
        configId = infoParam.configId,
        dataId = infoParam.dataId,
        contentType = infoParam.contentType,
        dataType = infoParam.dataType,
    )
}

fun NavGraphBuilder.gameDetailsScreen(
    navController: NavController,
    isOffline: Boolean,
    onSnackbar: suspend (String) -> Unit,
) {
    composable<GameDetailsRoute> {
        GameDetailsScreen(
            navController = navController,
            isOffline = isOffline,
            onSnackbar = onSnackbar,
        )
    }
}

fun Uri.isGameDetailsUri(): Boolean {
    return host == "details"
}

fun Uri.getGameDetailsRoute(): GameDetailsRoute? {
    return runCatching {
        val configId = getQueryParameter("configId")!!.toInt()
        val dataId = getQueryParameter("dataId")!!
        val contentType = getQueryParameter("contentType")!!.toInt()
        val dataType = getQueryParameter("dataType")!!
        GameDetailsRoute(
            configId = configId,
            dataId = dataId,
            contentType = contentType,
            dataType = dataType,
        )
    }.getOrNull()
}

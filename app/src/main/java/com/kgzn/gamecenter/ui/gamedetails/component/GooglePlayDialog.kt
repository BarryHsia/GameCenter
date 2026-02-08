package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.ConfirmDialog

@Composable
fun GooglePlayDialog(
    onConfirmRequest: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ConfirmDialog(
        title = stringResource(R.string.google_play_store),
        message = stringResource(R.string.dependency_confirm, stringResource(R.string.google_play_store)),
        confirmText = stringResource(R.string.download),
        dismissText = stringResource(R.string.cancel),
        onConfirmRequest = onConfirmRequest,
        onDismissRequest = onDismissRequest,
        defaultFocusDismiss = false,
    )
}

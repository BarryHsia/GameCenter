package com.kgzn.gamecenter.ui.uninstaller.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.ConfirmDialog

@Composable
fun UninstallDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    ConfirmDialog(
        modifier = modifier,
        title = stringResource(R.string.uninstall),
        message = stringResource(R.string.uninstall_confirm),
        onDismissRequest = onDismiss,
        onConfirmRequest = onConfirm,
    )
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun UninstallDialogPreview() {
    UninstallDialog()
}

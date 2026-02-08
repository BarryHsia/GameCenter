package com.kgzn.gamecenter.ui.downloader.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.ConfirmDialog

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    ConfirmDialog(
        title = stringResource(R.string.delete),
        message = stringResource(R.string.delete_confirm),
        modifier = modifier,
        onDismissRequest = onDismiss,
        onConfirmRequest = onConfirm,
    )
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun DeleteDialogPreview() {
    DeleteDialog()
}

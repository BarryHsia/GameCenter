package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonBar(
    modifier: Modifier = Modifier,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    showNegativeButton: () -> Boolean = { false },
    positiveButtonText: String = "",
    negativeButtonText: String = "",
    progress: () -> Float = { 1f },
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        DownloadButton(
            text = positiveButtonText,
            progress = progress(),
            onClick = onPositiveClick,
        )
        if (showNegativeButton()) {
            DownloadButton(
                text = negativeButtonText,
                progress = 0f,
                onClick = onNegativeClick,
            )
        }
    }
}
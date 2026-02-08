package com.kgzn.gamecenter.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun ConfirmDialog(
    title: String,
    message: String = "",
    modifier: Modifier = Modifier,
    onConfirmRequest: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    confirmText: String = stringResource(R.string.confirm),
    dismissText: String = stringResource(R.string.cancel),
    defaultFocusDismiss: Boolean = true,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .onPreviewKeyEvent(object : (KeyEvent) -> Boolean {
                    private var isHandled = true
                    override fun invoke(events: KeyEvent): Boolean {
                        if (events.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                            if (events.nativeKeyEvent.repeatCount == 0) isHandled = false
                        }
                        return isHandled
                    }
                })
                .clip(RoundedCornerShape(7.5.dp))
                .background(Color(0xFF1E1E1E))
                .padding(top = 27.5.dp, start = 17.8.dp, end = 17.8.dp, bottom = 22.95.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, style = GcTextStyle.Style7)
            Spacer(modifier = Modifier.height(22.75.dp))
            Text(message, style = GcTextStyle.Style1)
            Spacer(modifier = Modifier.height(26.8.dp))
            val (dismissFocusRequester, confirmFocusRequester) = remember { FocusRequester.createRefs() }
            Row(
                modifier = Modifier
                    .focusRestorer(if (defaultFocusDismiss) dismissFocusRequester else confirmFocusRequester)
                    .focusGroup(),
                horizontalArrangement = Arrangement.spacedBy(17.95.dp)
            ) {
                Surface(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(height = 44.dp, width = 156.75.dp)
                        .focusRequester(dismissFocusRequester),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = Color(0xFF333333),
                        focusedContainerColor = Color(0xFF1B77F7),
                    ),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
                ) {
                    Text(dismissText, modifier = Modifier.align(Alignment.Center), style = GcTextStyle.Style8)
                }
                Surface(
                    onClick = onConfirmRequest,
                    modifier = Modifier
                        .size(height = 44.dp, width = 156.75.dp)
                        .focusRequester(confirmFocusRequester),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = Color(0xFF333333),
                        focusedContainerColor = Color(0xFF1B77F7),
                    ),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
                ) {
                    Text(confirmText, modifier = Modifier.align(Alignment.Center), style = GcTextStyle.Style8)
                }
            }
        }
    }
}

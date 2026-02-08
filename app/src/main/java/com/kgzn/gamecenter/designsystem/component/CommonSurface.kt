package com.kgzn.gamecenter.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import com.kgzn.gamecenter.designsystem.modifier.shadowBorder

@Composable
fun CommonSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    scale: Float = 1f,
    focusedScale: Float = 1.1f,
    border: Border = Border.None,
    focusBorder: Border = Border(
        border = BorderStroke(1.5.dp, Color.White),
        inset = 1.5.dp,
        shape = shape,
    ),
    shadowRadius: Dp = 1.5.dp,
    containerColor: Color = Color(0xFF414141),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable (BoxScope.() -> Unit)
) {

    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()

    val currScale by animateFloatAsState(if (pressed) scale else if (focused) focusedScale else scale)
    Box(
        modifier = modifier
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .graphicsLayer {
                scaleX = currScale
                scaleY = currScale
            }
            .shadowBorder(
                border = if (focused) focusBorder else border,
                shadowRadius = shadowRadius,
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(containerColor)
        ) {
            content()
        }
    }
}

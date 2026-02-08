package com.kgzn.gamecenter.designsystem.component

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import kotlin.math.roundToInt

@Composable
fun GradientButton(
    text: String,
    percent: Float = 0.0f,
    onClick: () -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(IntOffset.Zero) }
    val cursorState = LocalCursorState.current
    val animateScale by animateFloatAsState(if (isFocused) cursorState.scale else 1.0f)
    Box(
        modifier = Modifier
            .scale(animateScale)
            .clip(shape = RoundedCornerShape(25.dp))
            .background(color = Color(255f, 255f, 255f, 0.2f))
            .background(
                brush = Brush.linearGradient(
                    0.0f to Color(0xFF418DFF),
                    1.0f to Color(0xFF30D0D8),
                    start = Offset(0f, 0f),
                    end = Offset(with(LocalDensity.current) {
                        125.dp.toPx() * percent
                    }, 0f),
                    tileMode = TileMode.Decal
                )
            )
            .size(width = 125.dp, height = 33.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (isFocused) {
                    Log.d("TAG", "onFocusChanged: $position")
                    cursorState.moveTo(
                        position = position, scale = 1.25f, size = DpSize(125.dp, 33.dp),
                        radius = 25.dp, bordWidth = 1.25.dp
                    )
                }
            }
            .onGloballyPositioned {
                position = it.positionInWindow().run { IntOffset(x.roundToInt(), y.roundToInt()) }
            }
            .clickable(onClick = onClick, indication = null, interactionSource = remember {
                MutableInteractionSource()
            }),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = GcTextStyle.Style3,
        )
    }
}

@Preview
@Composable
fun GradientButtonPreview() {
    GradientButton(
        text = "Cancel",
        percent = 0.5f,
        onClick = {},
    )
}
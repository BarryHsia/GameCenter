package com.kgzn.gamecenter.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset

@Stable
class CursorState(
    width: Dp = Dp.Hairline,
    height: Dp = Dp.Hairline,
    position: IntOffset = IntOffset.Zero,
    radius: Dp = Dp.Hairline,
    scale: Float = 1f,
    isVisible: Boolean = false,
    bordWidth: Dp = Dp.Hairline,
    initialColor: Color = Color.Transparent,
) {
    private var _size by mutableStateOf(DpSize(width, height))
    private var _position by mutableStateOf(position)
    private var _radius by mutableStateOf(radius)
    private var _scale by mutableFloatStateOf(scale)
    private var _isVisible by mutableStateOf(isVisible)
    private var _bordWidth by mutableStateOf(bordWidth)
    private var _color by mutableStateOf(initialColor)

    val size get() = _size
    val position get() = _position
    val radius get() = _radius
    val scale get() = _scale
    val isVisible get() = _isVisible
    val bordWidth get() = _bordWidth
    val color get() = _color

    fun moveTo(
        position: IntOffset = _position,
        scale: Float = _scale,
        size: DpSize = _size,
        radius: Dp = _radius,
        bordWidth: Dp = _bordWidth,
        color: Color = _color,
    ) {
        _size = size
        _position = position
        _radius = radius
        _scale = scale
        _bordWidth = bordWidth
        _color = color
        _isVisible = true
    }

    fun moveTo(
        position: IntOffset = _position,
        scale: Float = _scale,
        width: Dp = _size.width,
        height: Dp = _size.height,
        radius: Dp = _radius,
        bordWidth: Dp = _bordWidth,
        color: Color = _color,
    ) {
        _size = DpSize(width, height)
        _position = position
        _radius = radius
        _scale = scale
        _bordWidth = bordWidth
        _color = color
        _isVisible = true
    }

    fun hide() {
        _isVisible = false
    }
}

val LocalCursorState = staticCompositionLocalOf<CursorState> {
    CursorState()
}

@Composable
fun rememberCursorState(
    width: Dp = Dp.Hairline,
    height: Dp = Dp.Hairline,
    position: IntOffset = IntOffset.Zero,
    radius: Dp = Dp.Hairline,
    scale: Float = 1f,
    isVisible: Boolean = false,
    bordWidth: Dp = Dp.Hairline,
    color: Color = Color.Transparent,
): CursorState {
    return remember(width, height, position, radius, scale, isVisible, bordWidth, color) {
        CursorState(
            width = width,
            height = height,
            position = position,
            radius = radius,
            scale = scale,
            isVisible = isVisible,
            bordWidth = bordWidth,
            initialColor = color,
        )
    }
}

@Composable
fun AnimateCursor(state: CursorState = rememberCursorState()) {
    val animateWidth by animateDpAsState(state.size.width)
    val animateHeight by animateDpAsState(state.size.height)
    val animatePosition by animateIntOffsetAsState(state.position)
    val animateRadius by animateDpAsState(state.radius)
    val animateScale by animateFloatAsState(state.scale)
    val animateBordWidth by animateDpAsState(state.bordWidth)
    val animateColor by animateColorAsState(state.color)
    AnimatedVisibility(state.isVisible) {
        Box(
            modifier = Modifier
                .offset { animatePosition }
                .scale(animateScale)
                .size(animateWidth, animateHeight)
                .border(
                    width = animateBordWidth,
                    color = animateColor,
                    shape = RoundedCornerShape(animateRadius),
                )
        )
    }
}
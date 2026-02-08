package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import kotlinx.coroutines.launch

private val SCROLL_STEP = 20.dp

@Composable
fun DescDialog(
    modifier: Modifier = Modifier,
    desc: () -> String,
    onDismissRequest: () -> Unit,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        val scrollState = rememberScrollState()
        val density = LocalDensity.current
        val step = with(density) { SCROLL_STEP.toPx() }
        val scope = rememberCoroutineScope()
        Surface(
            modifier = modifier.onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

                // 2. 处理遥控器方向键事件
                when (event.key) {
                    Key.DirectionUp -> {
                        // 向上滚动（负方向）
                        scope.launch {
                            scrollState.animateScrollBy(-step)
                        }
                        true // 消费事件，避免传递给其他组件
                    }

                    Key.DirectionDown -> {
                        // 向下滚动（正方向）
                        scope.launch {
                            scrollState.animateScrollBy(step)
                        }
                        true // 消费事件
                    }

                    else -> false // 不处理其他按键
                }
            },
            onClick = onDismissRequest,
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(BorderStroke(1.5.dp, Color.White), 2.dp)
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
        ) {
            Text(
                desc(),
                style = GcTextStyle.Style3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(25.dp)
                    .verticalScroll(scrollState),
            )
        }
    }
}

@Preview
@Composable
fun DescDialogPreview() {
    DescDialog(
        modifier = Modifier.padding(vertical = 25.dp),
        desc = { "这是一个游戏描述".repeat(100) },
        onDismissRequest = {},
    )
}

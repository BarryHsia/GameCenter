package com.kgzn.gamecenter.ui.settings.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun SelectPreference(
    modifier: Modifier,
    title: String,
    options: List<String>,
    selectedIndex: () -> Int,
    onSelectedIndexChange: (Int) -> Unit = {},
) {
    val index = selectedIndex()

    SelectPreference(
        modifier = modifier,
        title = title,
        option = options[index],
        leftEnabled = { index > 0 },
        rightEnabled = { index < options.size - 1 },
        onLeftClick = { onSelectedIndexChange(index - 1) },
        onRightClick = { onSelectedIndexChange(index + 1) },
    )
}

@Composable
fun SelectPreference(
    modifier: Modifier = Modifier,
    title: String,
    option: String,
    leftEnabled: () -> Boolean = { true },
    rightEnabled: () -> Boolean = { true },
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    CommonSurface(
        modifier = modifier.onKeyEvent { event ->
            when (event.key) {
                Key.DirectionLeft -> {
                    if (event.type == KeyEventType.KeyUp && leftEnabled()) {
                        onLeftClick()
                    }
                    true
                }

                Key.DirectionRight -> {
                    if (event.type == KeyEventType.KeyUp && rightEnabled()) {
                        onRightClick()
                    }
                    true
                }

                else -> false
            }
        },
        onClick = { },
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 37.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isFocused by interactionSource.collectIsFocusedAsState()
            Text(
                modifier = Modifier
                    .weight(1f)
                    .then(if (isFocused) Modifier.basicMarquee() else Modifier),
                text = title,
                style = GcTextStyle.Style3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(19.dp),
                        painter = painterResource(id = R.drawable.ic_left),
                        contentDescription = null,
                        tint = if (leftEnabled()) Color.White else Color.White.copy(0.2f)
                    )
                    Box(
                        Modifier.width(86.dp)
                    ) {
                        Text(
                            option,
                            style = GcTextStyle.Style3,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .then(if (isFocused) Modifier.basicMarquee() else Modifier),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        modifier = Modifier.size(19.dp),
                        painter = painterResource(id = R.drawable.ic_right),
                        contentDescription = null,
                        tint = if (rightEnabled()) Color.White else Color.White.copy(0.2f)
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun SelectPreferencePreview() {
    SelectPreference(
        modifier = Modifier.size(width = 547.5.dp, height = 63.dp),
        title = "Resolution",
        option = "1920x1080",
        leftEnabled = { true },
        rightEnabled = { false },
    )
}

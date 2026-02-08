package com.kgzn.gamecenter.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import coil3.compose.SubcomposeAsyncImage
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

data class GameState(
    val name: String,
    val picUrl: String?,
)

@Composable
fun Game(
    modifier: Modifier = Modifier,
    game: GameState,
    onClick: () -> Unit = {},
    onFocused: () -> Unit = {},
    focusedScale: Float = 1.1f,
    contentScale: ContentScale = ContentScale.FillHeight,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(isFocused) {
        if (isFocused) {
            onFocused()
        }
    }
    var containerColor: Color by remember { mutableStateOf(Color(0xFF414141)) }
    CommonSurface(
        modifier = modifier,
        onClick = { onClick() },
        focusedScale = focusedScale,
        containerColor = containerColor,
        interactionSource = interactionSource,
    ) {
        val placeholder: @Composable () -> Unit = {
            Box {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp, 37.5.dp),
                    painter = painterResource(id = R.drawable.bg_game_item),
                    contentDescription = game.name,
                )
            }
        }
        SubcomposeAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .drawWithContent {
                    drawContent()
                    if (isFocused) {
                        drawRect(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x00121212),
                                    Color(0xFF121212)
                                )
                            ),
                        )
                    }
                }
                .fillMaxSize(),
            model = game.picUrl,
            contentDescription = game.name,
            error = {
                placeholder()
            },
            loading = {
                placeholder()
            },
            contentScale = contentScale,
        )
        if (isFocused) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp, start = 10.dp, end = 10.dp)
                    .basicMarquee(),
                text = game.name,
                style = GcTextStyle.Style6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
fun GamePreview() {
    Game(
        game = GameState(
            name = "游戏名称",
            picUrl = "https://gamecenter.kgzn.com.cn/game/1692822253489.png",
        ),
        onClick = {},
        onFocused = {},
    )
}

package com.kgzn.gamecenter.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import coil3.compose.SubcomposeAsyncImage
import com.kgzn.gamecenter.R

private val BrushVertical = Brush.verticalGradient(listOf(Color(0xFF418DFF), Color(0xFF30D0D8)))
private val BrushHorizontal = Brush.horizontalGradient(listOf(Color(0xFF418DFF), Color(0xFF30D0D8)))

@Composable
fun HomeBarAction(
    modifier: Modifier = Modifier,
    model: Any? = null,
    showBar: Boolean = false,
    selected: Boolean = false,
    imagePadding: PaddingValues = PaddingValues(0.dp),
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.size(37.dp),
        shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color(0xFF414141)
        ),
        onClick = onClick,
        scale = ClickableSurfaceDefaults.scale(
            scale = 0.96f,
            focusedScale = 1.05f
        )
    ) {
        var success by remember { mutableStateOf(false) }
        SubcomposeAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(imagePadding)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    if (selected) {
                        drawRect(BrushVertical, blendMode = BlendMode.SrcIn)
                    } else {
                        drawRect(Color.White, blendMode = BlendMode.SrcIn)
                    }
                },
            model = model,
            contentDescription = null,
            onSuccess = { success = true },
        )
        if (showBar && success) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = 12.dp)
                    .size(width = 13.dp, height = 1.5.dp)
                    .clip(RoundedCornerShape(1.25.dp))
                    .background(BrushHorizontal)
            )
        }
    }
}

@Preview
@Composable
fun HomeBarActionPreview() {
    Row {
        HomeBarAction(model = "https://hoppscotch.com/images/logo.svg", selected = true)
        HomeBarAction(model = R.drawable.bg_game_item, selected = true)
    }
}
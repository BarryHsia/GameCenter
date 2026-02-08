package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun GameDetailsBackground(
    modifier: Modifier = Modifier,
    model: Any?,
) {
    AsyncImage(
        modifier = modifier
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(0f to Color.Transparent, 0.72f to Color(0xf7121212), 1f to Color(0xff121212))
                )
            }.blur(2.dp),
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}
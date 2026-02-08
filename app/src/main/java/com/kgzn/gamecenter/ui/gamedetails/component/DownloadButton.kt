package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun DownloadButton(
    modifier: Modifier = Modifier,
    text: String,
    progress: Float = 0f,
    onClick: () -> Unit = {},
) {
    CommonSurface(
        modifier = modifier.size(125.dp, 33.dp),
        shape = RoundedCornerShape(25.dp),
        onClick = onClick,
    ) {
        val end = Offset(with(LocalDensity.current) { 125.dp.toPx() * progress }, 0f)
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawRect(
                        Brush.linearGradient(
                            listOf(Color(0xFF418DFF), Color(0xFF30D0D8)), end = end, tileMode = TileMode.Decal
                        ),
                    )
                },
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = GcTextStyle.Style3,
        )
    }
}

@Preview
@Composable
fun DownloadButtonPreview() {
    Column {
        DownloadButton(text = "下载")
        DownloadButton(text = "下载", progress = 0.5f)
        DownloadButton(text = "下载", progress = 1f)
    }
}

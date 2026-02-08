package com.kgzn.gamecenter.designsystem.modifier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import kotlin.math.max

fun Modifier.shadowBorder(
    border: Border,
    shadowRadius: Dp,
) = drawWithContent {
    drawContent()

    if (border == Border.None) return@drawWithContent

    val outlineRounded = border.shape.createOutline(
        size = size,
        layoutDirection = LayoutDirection.Ltr,
        density = this,
    ) as Outline.Rounded
    val cornerRadius = outlineRounded.roundRect.topLeftCornerRadius
    val shadowCornerRadius = cornerRadius.copy(
        x = max(0f, cornerRadius.x + border.inset.toPx() + border.border.width.toPx()),
        y = max(0f, cornerRadius.y + border.inset.toPx() + border.border.width.toPx()),
    )
    val borderCornerRadius = cornerRadius.copy(
        x = max(0f, cornerRadius.x + border.inset.toPx() + border.border.width.toPx() / 2),
        y = max(0f, cornerRadius.y + border.inset.toPx() + border.border.width.toPx() / 2),
    )

    inset(-border.inset.toPx() - border.border.width.toPx() / 2) {
        drawRoundRect(
            brush = border.border.brush,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = borderCornerRadius,
            style = Stroke(border.border.width.toPx())
        )
        inset(
            -border.border.width.toPx() / 2,
        ) {
            drawIntoCanvas { canvas ->
                val layerRect = Rect(
                    Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
                    Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY
                )
                canvas.saveLayer(layerRect, Paint())

                val paint = Paint().apply {
                    blendMode = BlendMode.Src
                    asFrameworkPaint().apply {
                        color = Color.Transparent.toArgb()
                        setShadowLayer(
                            /* radius= */ shadowRadius.toPx(),
                            /* dx= */ 0f,
                            /* dy= */ 0f,
                            /* shadowColor= */ Color.White.toArgb()
                        )
                    }
                }

                canvas.drawRoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = shadowCornerRadius.x,
                    radiusY = shadowCornerRadius.y,
                    paint = paint,
                )

                canvas.restore()
            }
        }
    }
}.clip(border.shape)

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun ShadowBorderPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
                .shadowBorder(
                    border = Border(
                        border = BorderStroke(1.dp, Color.White),
                        inset = 2.dp,
                        shape = RoundedCornerShape(25.dp),
                    ),
                    shadowRadius = 4.dp,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            ) { }
        }
    }
}

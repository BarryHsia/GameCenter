package com.kgzn.gamecenter.designsystem.theme


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@Composable
fun GcTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme().copy(
            surface = Color(0xFF121212),
        ),
        typography = MaterialTheme.typography.copy(
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.W500,
                color = Color.White
            )
        ),
        shapes = MaterialTheme.shapes.copy(
            medium = RoundedCornerShape(5.dp),
        ),
        content = content,
    )
}
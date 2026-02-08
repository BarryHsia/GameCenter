package com.kgzn.gamecenter.ui.search.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import androidx.tv.material3.Text
import coil3.BitmapImage
import coil3.compose.SubcomposeAsyncImage
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun SearchResultItem(
    titleBuilder: () -> String,
    imgBuilder: () -> Any? = { null },
    onClick: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        var containerColor: Color by remember { mutableStateOf(Color(0xFF414141)) }

        val title = titleBuilder()
        val img = imgBuilder()

        CommonSurface(
            modifier = Modifier.size(width = 200.dp, height = 112.5.dp),
            onClick = onClick,
            containerColor = containerColor,
            focusedScale = 1.09f,
            interactionSource = interactionSource,
        ) {
            val placeholder: @Composable () -> Unit = {
                Box {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp, 37.5.dp),
                        painter = painterResource(id = R.drawable.bg_game_item),
                        contentDescription = title,
                    )
                }
            }
            SubcomposeAsyncImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                model = img,
                contentDescription = title,
                error = {
                    placeholder()
                },
                loading = {
                    placeholder()
                },
                onSuccess = { success ->
                    val image = success.result.image
                    if (image is BitmapImage) {
                        val palette =
                            Palette.from(image.bitmap.copy(Bitmap.Config.ARGB_8888, true)).generate()
                        containerColor = Color(palette.getDominantColor(Color.White.copy(0.1f).toArgb()))
                    }
                }
            )
        }
        Text(
            modifier = Modifier.then(if (isFocused) Modifier.basicMarquee() else Modifier),
            text = title,
            style = GcTextStyle.Style3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
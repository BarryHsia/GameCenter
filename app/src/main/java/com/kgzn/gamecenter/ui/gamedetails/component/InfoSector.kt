package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun InfoSector(
    modifier: Modifier = Modifier,
    title: () -> String,
    desc: () -> String = { "" },
    tags: () -> List<String> = { emptyList() },
    onDescClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            title(), style = GcTextStyle.Style10,
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .basicMarquee(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val tags1 = tags()
        if (tags1.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .padding(top = 15.dp)
                    .height(25.dp),
                horizontalArrangement = Arrangement.spacedBy(12.5.dp),
            ) {
                items(tags1) { tag ->
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(15.dp),
                        colors = SurfaceDefaults.colors(
                            containerColor = Color.White.copy(0.1f),
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.5.dp)
                        ) {
                            Text(tag, style = GcTextStyle.Style6, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
        var descExpendEnabled by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier
                .padding(horizontal = 28.5.dp)
                .padding(top = 15.dp)
                .focusProperties {
                    canFocus = descExpendEnabled
                },
            onClick = onDescClick,
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(BorderStroke(1.5.dp, Color.White), 2.dp)
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
        ) {
            CompositionLocalProvider(LocalTextStyle provides GcTextStyle.Style3.copy(lineHeight = with(LocalDensity.current) { 22.5.dp.toSp() })) {
                Box(
                    modifier = Modifier.height(76.5.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        desc(),
                        modifier = Modifier
                            .width(376.25.dp)
                            .alpha(0.75f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            descExpendEnabled = it.hasVisualOverflow
                        }
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun InfoSectorPreview() {
    InfoSector(
        title = { "Title" },
        desc = { "Desc" },
        tags = { listOf("Tag1", "Tag2", "Tag3") },
    )
}

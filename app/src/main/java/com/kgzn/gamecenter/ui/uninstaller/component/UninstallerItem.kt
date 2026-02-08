package com.kgzn.gamecenter.ui.uninstaller.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.Text
import coil3.compose.SubcomposeAsyncImage
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@Composable
fun UninstallerItem(
    modifier: Modifier = Modifier,
    img: Any?,
    title: String,
    subTitle: String,
    trailing: () -> String = { "" },
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {

    val interactionSource = remember { MutableInteractionSource() }

    CommonSurface(
        modifier = modifier.height(102.5.dp),
        onClick = onClick,
        interactionSource = interactionSource,
        onLongClick = onLongClick,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
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
                modifier = Modifier.size(62.5.dp),
                model = img,
                contentDescription = title,
                error = {
                    placeholder()
                },
                loading = {
                    placeholder()
                }
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val isFocused by interactionSource.collectIsFocusedAsState()
                Text(
                    text = title,
                    style = GcTextStyle.Style5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.takeIf { isFocused }?.basicMarquee() ?: Modifier
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ProvideTextStyle(value = GcTextStyle.Style6) {
                        Text(
                            subTitle,
                            modifier = Modifier
                                .alpha(0.75f)
                                .weight(1f)
                                .then(Modifier.takeIf { isFocused }?.basicMarquee() ?: Modifier),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            trailing(),
                            modifier = Modifier
                                .alpha(0.75f)
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun UninstallerItemPreview() {
    UninstallerItem(
        img = "https://img.kgzn.com/2023/08/10/1691724800000.png",
        title = "Test Game",
        subTitle = "Test Game SubTitle",
        trailing = { "124 MB" },
    )
}

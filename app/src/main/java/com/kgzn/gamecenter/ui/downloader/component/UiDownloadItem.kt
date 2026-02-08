package com.kgzn.gamecenter.ui.downloader.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.Text
import coil3.compose.SubcomposeAsyncImage
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

enum class UiDownloadState {
    Paused,
    Downloading,
    Completed,
    Installing,
    DownloadError,
    InstallError,
    Installed,
}

data class UiDownloadItemState(
    val title: String,
    val img: Any?,
    val totalSize: Long,
    val downloadedSize: Long,
    val state: UiDownloadState,
)

@Composable
fun UiDownloadItem(
    modifier: Modifier = Modifier,
    state: UiDownloadItemState,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {

    fun Long.formatSize(): String {
        return when {
            this <= 0 -> "0B"
            this < 1024 -> "${this}B"
            this < 1024 * 1024 -> "${this / 1024}KB"
            this < 1024 * 1024 * 1024 -> "${this / (1024 * 1024)}MB"
            else -> "${this / (1024 * 1024 * 1024)}GB"
        }
    }

    @Composable
    fun UiDownloadState.toText(): String {
        return when (this) {
            UiDownloadState.Paused -> stringResource(R.string.paused)
            UiDownloadState.Downloading -> stringResource(R.string.downloading)
            UiDownloadState.Completed -> stringResource(R.string.completed)
            UiDownloadState.Installing -> stringResource(R.string.installing)
            UiDownloadState.DownloadError -> stringResource(R.string.download_error)
            UiDownloadState.InstallError -> stringResource(R.string.install_error)
            UiDownloadState.Installed -> stringResource(R.string.installed)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    CommonSurface(
        modifier = modifier.height(102.5.dp),
        onClick = onClick,
        onLongClick = onLongClick,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val placeholder: @Composable () -> Unit = {
                Box {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp, 37.5.dp),
                        painter = painterResource(id = R.drawable.bg_game_item),
                        contentDescription = state.title,
                    )
                }
            }
            SubcomposeAsyncImage(
                modifier = Modifier.size(68.75.dp),
                model = state.img,
                contentDescription = state.title,
                error = {
                    placeholder()
                },
                loading = {
                    placeholder()
                }
            )
            Spacer(modifier = Modifier.size(15.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val isFocused by interactionSource.collectIsFocusedAsState()
                Text(
                    text = state.title,
                    style = GcTextStyle.Style5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.takeIf { isFocused }?.basicMarquee() ?: Modifier
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProvideTextStyle(value = GcTextStyle.Style6) {
                            Text(state.totalSize.formatSize(), modifier = Modifier.alpha(0.75f))
                            Text(state.state.toText(), modifier = Modifier.alpha(0.75f))
                        }
                    }
                    LinearProgressIndicator(
                        progress = { state.downloadedSize.toFloat() / state.totalSize.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp),
                        color = Color(0xFF1B77F7),
                        trackColor = Color.White.copy(0.2f),
                        gapSize = Dp.Hairline,
                        drawStopIndicator = {},
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun UiDownloadItemPreview() {
    UiDownloadItem(
        state = UiDownloadItemState(
            title = "Test",
            totalSize = 1000000000,
            downloadedSize = 500000000,
            state = UiDownloadState.Downloading,
            img = "https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2552222422.jpg",
        )
    )
}

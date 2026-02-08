package com.kgzn.gamecenter.ui.home.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.db.playrecord.PlayRecord
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.ui.home.component.Game
import com.kgzn.gamecenter.ui.home.component.GameState

@Composable
fun MinePage(
    modifier: Modifier = Modifier,
    onGamepadsClick: () -> Unit = {},
    onDownloaderClick: () -> Unit = {},
    onUninstallerClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    playRecords: List<PlayRecord> = emptyList(),
    onRecordClick: (Int) -> Unit = {},
) {

    val requester1 = remember { FocusRequester() }

    fun (() -> Unit).save(): () -> Unit = {
        requester1.saveFocusedChild()
        this()
    }

    val actions = remember {
        listOf(
            ManageAction(
                resId = R.drawable.manage_gamepads,
                text = { stringResource(R.string.input_manager) },
                onClick = onGamepadsClick.save(),
            ),
            ManageAction(
                resId = R.drawable.manage_downloader,
                text = { stringResource(R.string.download_manager) },
                onClick = onDownloaderClick.save()
            ),
            ManageAction(
                resId = R.drawable.manage_uninstaller,
                text = { stringResource(R.string.uninstall_manager) },
                onClick = onUninstallerClick.save()
            ),
            ManageAction(
                resId = R.drawable.manage_settings,
                text = { stringResource(R.string.settings) },
                onClick = onSettingsClick.save()
            ),
            ManageAction(
                resId = R.drawable.manage_about,
                text = { stringResource(R.string.about) },
                onClick = onAboutClick.save()
            ),
        )
    }

    LaunchedEffect(Unit) {
        if (!requester1.restoreFocusedChild()) {
            requester1.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .focusRequester(requester1)
            .focusProperties {
                onEnter = {
                    if (!requester1.restoreFocusedChild()) {
                        requester1.requestFocus()
                    }
                }
                onExit = {
                    requester1.saveFocusedChild()
                }
            }
            .focusRestorer()
            .focusGroup(),
    ) {
        Text(
            stringResource(R.string.recent_play),
            style = GcTextStyle.Style3,
            modifier = Modifier.padding(top = 65.75.dp, start = 15.dp)
        )
        if (playRecords.isEmpty()) {
            NoRecentGame()
        } else {
            val recentlyRequest = remember { FocusRequester() }
            val gameStates = playRecords.map { GameState(name = it.title, picUrl = it.imgUrl) }
            LazyRow(
                modifier = Modifier
                    .focusRequester(recentlyRequest),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 12.5.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                itemsIndexed(gameStates) { index, game ->
                    Game(
                        modifier = Modifier.size(width = 200.dp, height = 115.dp),
                        game = game,
                        onClick = {
                            requester1.saveFocusedChild()
                            recentlyRequest.saveFocusedChild()
                            onRecordClick(index)
                        },
                        focusedScale = 1.07f,
                    )
                }
            }
        }
        Text(
            stringResource(R.string.manage),
            style = GcTextStyle.Style3,
            modifier = Modifier.padding(start = 15.dp, bottom = 12.5.dp)
        )
        val requester = remember { FocusRequester() }
        LazyRow(
            modifier = Modifier
                .focusRequester(requester)
                .focusProperties {
                    onEnter = {
                        if (!requester.restoreFocusedChild()) {
                            requester.requestFocus()
                        }
                    }
                    onExit = {
                        requester.saveFocusedChild()
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            items(actions, key = { it.resId }) {
                ManageActionUI(
                    resId = it.resId, text = it.text(),
                    onClick = {
                        requester.saveFocusedChild()
                        it.onClick()
                    },
                )
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun MineScreenPreview() {
    MinePage(
        playRecords = listOf(
            PlayRecord(title = "Game 1", imgUrl = "https://example.com/game1.jpg"),
            PlayRecord(title = "Game 2", imgUrl = "https://example.com/game2.jpg"),
        )
    )
}

data class ManageAction(
    val resId: Int,
    val text: @Composable () -> String,
    val onClick: () -> Unit = {},
)

@Composable
fun ManageActionUI(
    resId: Int,
    text: String,
    onClick: () -> Unit = {},
) {
    CommonSurface(
        onClick = onClick,
        modifier = Modifier
            .size(width = 160.dp, height = 213.5.dp),
        focusedScale = 1.05f,
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = resId),
            contentDescription = text
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.5.dp),
            text = text,
            fontSize = 15.sp,
            color = Color.White,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoRecentGame(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(137.5.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(25.dp),
            painter = painterResource(id = R.drawable.ic_no_recent),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(stringResource(R.string.no_recent_play_record), style = GcTextStyle.Style4)
    }
}

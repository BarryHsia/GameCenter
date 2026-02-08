package com.kgzn.gamecenter.ui.home.component

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Banner2State(
    val games: List<GameState> = emptyList(),
)

@Composable
fun Banner2(
    modifier: Modifier = Modifier,
    state: Banner2State,
    onGameClick: (Int) -> Unit = {},
    onGameFocused: (Int) -> Unit = {},
) {
    val requester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .padding(15.dp)
            .padding(top = 10.dp)
            .focusRequester(requester)
            .focusRestorer()
            .focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        for (index in 0 until 2) {
            val gameState = state.games.getOrNull(index)
            if (gameState != null) {
                Game(
                    modifier = Modifier
                        .height(240.dp)
                        .weight(1f),
                    game = gameState,
                    focusedScale = 1.03f,
                    onFocused = { onGameFocused(0) },
                    onClick = {
                        requester.saveFocusedChild()
                        onGameClick(index)
                    },
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun Banner2Preview() {
    Banner2(
        state = Banner2State(
            games = List(2) { GameState(name = "Game $it", picUrl = null) }
        ),
    )
}

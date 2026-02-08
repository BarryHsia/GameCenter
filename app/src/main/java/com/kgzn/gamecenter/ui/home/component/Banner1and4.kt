package com.kgzn.gamecenter.ui.home.component

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Banner1and4State(
    val games: List<GameState> = emptyList(),
)

@Composable
fun Banner1and4(
    state: Banner1and4State,
    onGameClick: (Int) -> Unit = {},
    onGameFocused: (Int) -> Unit = {},
) {

    val firstGame = state.games.firstOrNull() ?: return

    val containerRequest = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .height(270.dp)
            .focusRequester(containerRequest)
            .focusRestorer()
            .focusGroup()
    ) {

        Game(
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp)
                .padding(vertical = 15.dp)
                .fillMaxSize(),
            game = firstGame,
            focusedScale = 1.03f,
            onFocused = {
                containerRequest.saveFocusedChild()
                onGameFocused(0)
            },
            onClick = { onGameClick(0) },
            contentScale = ContentScale.FillBounds,
        )

        val requester = remember { FocusRequester() }
        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .focusRequester(requester),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            itemsIndexed(state.games.subList(1, state.games.size)) { index, item ->
                Game(
                    modifier = Modifier.height(112.5.dp),
                    game = item,
                    focusedScale = 1.07f,
                    onFocused = {
                        containerRequest.saveFocusedChild()
                        requester.saveFocusedChild()
                        onGameFocused(index + 1)
                    },
                    onClick = { onGameClick(index + 1) },
                )
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun Banner1and4Preview() {
    Column {
        Banner1and4(
            state = Banner1and4State(
                games = listOf(
                    GameState(name = "Play Now", picUrl = null),
                    GameState(name = "Play Now", picUrl = null),
                    GameState(name = "Play Now", picUrl = null),
                    GameState(name = "Play Now", picUrl = null),
                )
            )
        )
        Banner1and4(
            state = Banner1and4State(
                games = listOf(
                    GameState(name = "Play Now", picUrl = null),
                    GameState(name = "Play Now", picUrl = null),
                    GameState(name = "Play Now", picUrl = null),
                )
            )
        )
    }
}

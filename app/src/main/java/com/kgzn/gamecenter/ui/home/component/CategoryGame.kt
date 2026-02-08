package com.kgzn.gamecenter.ui.home.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle


data class CategoryGameState(
    val category: String? = null,
    val games: List<GameState>,
)

@Composable
fun CategoryGame(
    modifier: Modifier = Modifier,
    category: String? = null,
    games: List<GameState>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp, vertical = 12.5.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.5.dp),
    onGameFocused: (Int) -> Unit = {},
    onGameClick: (Int) -> Unit = {},
) {
    CategoryGame(
        state = CategoryGameState(category = category, games = games),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        onGameFocused = onGameFocused,
        onGameClick = onGameClick,
    )
}

@Composable
fun CategoryGame(
    state: CategoryGameState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp, vertical = 12.5.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.5.dp),
    onGameFocused: (Int) -> Unit = {},
    onGameClick: (Int) -> Unit = {},
) {

    Column(verticalArrangement = verticalArrangement) {
        var isFocused by remember { mutableStateOf(false) }
        if (state.category != null) {
            val transition = updateTransition(isFocused)
            val alpha by transition.animateFloat { if (isFocused) 1f else 0.75f }
            val fontSize by transition.animateFloat { if (isFocused) 15f else 13.5f }
            Box(
                modifier = Modifier
                    .padding(top = 12.5.dp, start = 15.dp)
                    .height(with(LocalDensity.current) { 20.sp.toDp() }),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = modifier.alpha(alpha),
                    text = state.category,
                    style = GcTextStyle.Style3.copy(
                        fontSize = fontSize.sp,
                        fontWeight = if (isFocused) FontWeight.W500 else FontWeight.W400,
                    ),
                )
            }
        }

        if (state.games.isNotEmpty()) {
            val requester = remember { FocusRequester() }
            LazyRow(
                modifier = Modifier
                    .focusRequester(requester)
                    .focusRestorer()
                    .onFocusChanged {
                        isFocused = it.isFocused or it.hasFocus
                    },
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                itemsIndexed(state.games) { index, game ->
                    Game(
                        modifier = Modifier.size(width = 200.dp, height = 115.dp),
                        game = game,
                        onClick = {
                            requester.saveFocusedChild()
                            onGameClick(index)
                        },
                        onFocused = { onGameFocused(index) },
                        focusedScale = 1.07f,
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun CategoryGamePreview() {
    val state = CategoryGameState(
        category = "Play Now",
        games = listOf(
            GameState(name = "Play Now Play Now Play Now Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
        ),
    )
    val state2 = CategoryGameState(
        games = listOf(
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
            GameState(name = "Play Now", picUrl = null),
        ),
    )
    Column {
        CategoryGame(CategoryGameState(null, emptyList()))
        CategoryGame(state)
        CategoryGame(state2)
    }
}

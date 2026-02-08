package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.ui.home.component.Game
import com.kgzn.gamecenter.ui.home.component.GameState

@Composable
fun RelativeResourceSector(
    modifier: Modifier = Modifier,
    resources: () -> List<Pair<String, Any?>> = { emptyList() },
    onResourceClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 25.dp).alpha(0.75f),
            text = stringResource(R.string.recommend),
            style = GcTextStyle.Style3,
        )
        val raw = resources()
        if (raw.isEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .height(137.5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_no_recommand),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                )
                Text(
                    text = stringResource(R.string.no_recommend_game),
                    modifier = Modifier.alpha(0.6f),
                    style = GcTextStyle.Style3,
                )
            }
        } else {
            val requester = remember { FocusRequester() }
            LazyRow(
                modifier = Modifier
                    .focusRequester(requester)
                    .focusRestorer(),
                contentPadding = PaddingValues(horizontal = 25.dp, vertical = 12.5.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                itemsIndexed(raw) { index, (title, model) ->
                    Game(
                        modifier = Modifier.size(width = 200.dp, height = 115.dp),
                        game = GameState(name = title, picUrl = model as? String ?: ""),
                        onClick = {
                            requester.saveFocusedChild()
                            onResourceClick(index)
                        },
                        focusedScale = 1.07f,
                    )
                }
            }
        }
    }
}
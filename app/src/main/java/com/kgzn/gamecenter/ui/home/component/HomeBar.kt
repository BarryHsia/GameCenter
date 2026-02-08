package com.kgzn.gamecenter.ui.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import coil3.compose.rememberAsyncImagePainter
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

private val BrushVertical = Brush.verticalGradient(listOf(Color(0xFF418DFF), Color(0xFF30D0D8)))

@Composable
fun HomeBar(
    modifier: Modifier = Modifier,
    actionsBuilder: () -> List<Pair<String?, Any?>> = { listOf("Mine" to R.drawable.ic_mine) },
    selectedActionIndex: Int = 0,
    onSearchClick: () -> Unit = {},
    onActionClick: (Int) -> Unit = {},
    onExpandChange: (Boolean) -> Unit = {},
) {

    var searchFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val actions = actionsBuilder()
    val requester = remember(actions) { List(actions.size) { FocusRequester() } }
    Column(
        modifier = modifier
            .focusProperties {
                onEnter = { requester[selectedActionIndex].requestFocus() }
            }
            .focusGroup()
            .fillMaxHeight()
            .onFocusChanged {
                expanded = it.hasFocus && !searchFocused
                onExpandChange(expanded)
            }
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 11.25.dp)
                        .clip(CircleShape)
                        .size(37.dp),
                    painter = rememberAsyncImagePainter(model = "file:///android_asset/ic_launcher.svg"),
                    contentDescription = null,
                )
                AnimatedVisibility(expanded) {
                    Text(
                        modifier = Modifier.padding(start = 7.5.dp),
                        text = stringResource(R.string.app_name),
                        style = GcTextStyle.Style3,
                    )
                }
            }
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(animateDpAsState(if (expanded) 15.dp else 7.5.dp).value)
        ) {
            for ((index, pair) in actions.withIndex()) {
                val (name, model) = pair
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val selected = selectedActionIndex == index
                    HomeBarAction(
                        modifier = Modifier
                            .padding(horizontal = 11.25.dp)
                            .focusRequester(requester[index]),
                        model = model,
                        showBar = selected,
                        selected = selected,
                        onClick = { onActionClick(index) },
                        imagePadding = if (model == R.drawable.ic_mine) PaddingValues(8.dp) else PaddingValues(0.dp),
                    )
                    AnimatedVisibility(expanded && name != null) {
                        Text(
                            modifier = Modifier.padding(start = 7.5.dp),
                            text = name!!,
                            style = GcTextStyle.Style3.copy(brush = if (selected) BrushVertical else null),
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            HomeBarAction(
                selected = searchFocused,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 11.25.dp, bottom = 8.dp)
                    .onFocusChanged {
                        searchFocused = it.isFocused
                    },
                model = R.drawable.ic_search,
                onClick = onSearchClick,
                imagePadding = PaddingValues(8.dp),
            )
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun HomeBarPreview() {
    HomeBar()
}

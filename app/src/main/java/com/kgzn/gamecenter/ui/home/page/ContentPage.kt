package com.kgzn.gamecenter.ui.home.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Resource
import com.kgzn.gamecenter.data.local.LocalDataProvider
import com.kgzn.gamecenter.designsystem.theme.AppBringIntoViewSpec
import com.kgzn.gamecenter.ui.gamedetails.GameDetailsRoute
import com.kgzn.gamecenter.ui.home.component.Banner1and4
import com.kgzn.gamecenter.ui.home.component.Banner1and4State
import com.kgzn.gamecenter.ui.home.component.Banner2
import com.kgzn.gamecenter.ui.home.component.Banner2State
import com.kgzn.gamecenter.ui.home.component.CategoryGame
import com.kgzn.gamecenter.ui.home.component.CategoryGameState
import com.kgzn.gamecenter.ui.home.component.GameState
import com.kgzn.gamecenter.ui.home.component.TopDesc
import com.kgzn.gamecenter.ui.home.component.TopDescState
import com.kgzn.gamecenter.ui.home.component.TopDescStateSaver

const val TAG = "ContentPage"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentPage(
    contentConfig: ContentConfig,
    navHostController: NavHostController = rememberNavController(),
) {

    fun getTopDescState(res: Resource): TopDescState? = res.takeIf {
        contentConfig.isHome == 1
    }?.let { TopDescState(name = it.title, tags = emptyList(), res.remark) }


    var topDescState: TopDescState? by rememberSaveable(
        stateSaver = TopDescStateSaver
    ) {
        mutableStateOf(
            contentConfig.componentList.firstOrNull()?.resourceList?.firstOrNull()?.let { getTopDescState(it) }
        )
    }
    val requester = remember { FocusRequester() }
    var selectedResource: Resource? by remember { mutableStateOf(null) }

    fun Resource.turnTo() {
        requester.saveFocusedChild()
        navHostController.navigate(
            GameDetailsRoute(
                configId = configId,
                dataId = dataId,
                contentType = contentType,
                dataType = dataType,
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (contentConfig.isHome == 1) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            Brush.verticalGradient(0f to Color.Transparent, 0.7f to Color(0xff121212))
                        )
                        drawRect(
                            Brush.horizontalGradient(0.1f to Color(0xff121212), 0.7f to Color.Transparent)
                        )
                    },
                model = selectedResource?.infoImgHUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Column {
            if (contentConfig.isHome == 1 && topDescState != null) {
                TopDesc(topDescState!!)
            }

            LaunchedEffect(Unit) {
                if (!requester.restoreFocusedChild()) {
                    requester.requestFocus()
                }
            }

            CompositionLocalProvider(LocalBringIntoViewSpec provides AppBringIntoViewSpec) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(requester)
                        .focusRestorer(),
                ) {
                    items(contentConfig.componentList) { component ->
                        when (component.showType) {
                            "top", "app" -> {
                                val gameItems = component.resourceList.map {
                                    GameState(name = it.title, picUrl = it.imgUrl)
                                }
                                val state = CategoryGameState(category = component.name, games = gameItems)
                                CategoryGame(
                                    state,
                                    onGameFocused = {
                                        selectedResource = component.resourceList[it]
                                        topDescState = getTopDescState(component.resourceList[it])
                                    },
                                    onGameClick = {
                                        component.resourceList[it].turnTo()
                                    }
                                )
                            }

                            "couchplay" -> {
                                val gameItems = component.resourceList.map {
                                    GameState(name = it.title, picUrl = it.imgUrl)
                                }
                                val state = Banner1and4State(games = gameItems)
                                Banner1and4(
                                    state = state,
                                    onGameFocused = {
                                        selectedResource = component.resourceList[it]
                                        topDescState = getTopDescState(component.resourceList[it])
                                    },
                                    onGameClick = {
                                        component.resourceList[it].turnTo()
                                    },
                                )
                            }

                            "banner" -> {
                                val gameItems = component.resourceList.map {
                                    GameState(name = it.title, picUrl = it.imgUrl)
                                }
                                val state = Banner2State(games = gameItems)
                                Banner2(
                                    state = state,
                                    onGameFocused = {
                                        selectedResource = component.resourceList[it]
                                        topDescState = getTopDescState(component.resourceList[it])
                                    },
                                    onGameClick = {
                                        component.resourceList[it].turnTo()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun ContentPagePreview() {
    ContentPage(LocalDataProvider.allContentConfigs.first())
}
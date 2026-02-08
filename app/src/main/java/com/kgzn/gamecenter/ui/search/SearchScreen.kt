package com.kgzn.gamecenter.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.ui.LocalNavController
import com.kgzn.gamecenter.ui.downloader.component.EmptyBackground
import com.kgzn.gamecenter.ui.gamedetails.GameDetailsRoute
import com.kgzn.gamecenter.ui.home.component.Loading
import com.kgzn.gamecenter.ui.search.component.SearchBar
import com.kgzn.gamecenter.ui.search.component.SearchResultItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    snackbarHostState: SnackbarHostState,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val navController = LocalNavController.current

    val results by viewModel.results.collectAsState()
    val key by viewModel.key.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                modifier = Modifier
                    .padding(top = 37.dp)
                    .size(width = 498.dp, height = 41.dp),
                text = key,
                onTextChange = {
                    viewModel.setKey(it)
                    viewModel.search(delay = 1000)
                },
                onSearch = {
                    viewModel.search(delay = 0)
                },
            )

            if (results != null && results!!.isNotEmpty()) {
                val requester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    requester.restoreFocusedChild()
                }

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(top = 22.5.dp)
                        .weight(1f)
                        .focusRequester(requester)
                        .focusRestorer(),
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(start = 42.5.dp, end = 42.5.dp, top = 15.dp, bottom = 37.5.dp),
                ) {
                    items(results!!) { search2 ->
                        SearchResultItem(
                            titleBuilder = { search2.title },
                            imgBuilder = { search2.imgUrl },
                            onClick = {
                                requester.saveFocusedChild()
                                navController.navigate(
                                    GameDetailsRoute(
                                        configId = search2.configId,
                                        dataId = search2.dataId.toString(),
                                        contentType = search2.contentType,
                                        dataType = search2.dataType,
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
        if (loading) {
            Box(Modifier.fillMaxSize()) {
                Loading(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                )
            }
        } else if (results != null && results!!.isEmpty()) {
            EmptyBackground(
                text = stringResource(R.string.no_search_result),
            )
        }
    }
}

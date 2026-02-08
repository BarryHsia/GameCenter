package com.kgzn.gamecenter.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.GcTopAppBar
import com.kgzn.gamecenter.ui.GcAppState
import com.kgzn.gamecenter.ui.settings.component.SelectPreference
import kotlinx.coroutines.launch


data class SelectSettings(
    val options: List<String>,
    val title: String,
    val current: () -> Int,
    val onSelectedIndexChange: (Int) -> Unit,
)

@Composable
fun SettingsScreen(
    appState: GcAppState,
) {

    val settingsManager = appState.settingsManager
    val isAutoInstall by settingsManager.isAutoInstall.collectAsState(true)
    val isClearPackageAfterInstall by settingsManager.isClearPackageAfterInstall.collectAsState(true)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val settings = remember {
        val options = listOf(context.getString(R.string.enable), context.getString(R.string.disable))
        listOf(
            SelectSettings(
                options = options,
                title = context.getString(R.string.auto_install_after_download),
                current = { if (isAutoInstall) 0 else 1 },
                onSelectedIndexChange = {
                    scope.launch {
                        settingsManager.setAutoInstall(it == 0)
                    }
                },
            ),
            SelectSettings(
                options = options,
                title = context.getString(R.string.clear_package_after_install),
                current = { if (isClearPackageAfterInstall) 0 else 1 },
                onSelectedIndexChange = {
                    scope.launch {
                        settingsManager.setClearPackageAfterInstall(it == 0)
                    }
                },
            ),
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GcTopAppBar(title = stringResource(R.string.settings))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(settings) { setting ->
                SelectPreference(
                    modifier = Modifier.size(width = 547.5.dp, height = 63.dp),
                    title = setting.title,
                    options = setting.options,
                    selectedIndex = setting.current,
                    onSelectedIndexChange = setting.onSelectedIndexChange
                )
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun SettingsScreenPreview() {
//    SettingsScreen()
}

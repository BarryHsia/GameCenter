package com.kgzn.gamecenter.ui.uninstaller

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.GcTopAppBar
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.ui.downloader.component.EmptyBackground
import com.kgzn.gamecenter.ui.uninstaller.component.UninstallDialog
import com.kgzn.gamecenter.ui.uninstaller.component.UninstallerItem
import kotlinx.coroutines.launch
import java.io.File

const val TAG = "UninstallerScreen"

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun UninstallerScreen(
    installManager: InstallManager,
) {

    val context = LocalContext.current
    val pm = context.packageManager
    val applicationInfos = pm?.getInstalledApplications(0).orEmpty().filterNotNull().filter {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pm.runCatching { getInstallSourceInfo(it.packageName).installingPackageName }
                .getOrNull() == context.packageName
        } else {
            TODO("VERSION.SDK_INT < R")
        }
    }

    var showDeleteDialog by remember { mutableIntStateOf(0) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val scope = rememberCoroutineScope()
    val uninstallList by installManager.uninstallListFlow.collectAsState()

    fun Long.formatSize(): String {
        return when {
            this <= 0 -> "0B"
            this < 1024 -> "${this}B"
            this < 1024 * 1024 -> "${this / 1024}KB"
            this < 1024 * 1024 * 1024 -> "${this / (1024 * 1024)}MB"
            else -> "${this / (1024 * 1024 * 1024)}GB"
        }
    }

    if (showDeleteDialog > 0) {
        UninstallDialog(
            onDismiss = { showDeleteDialog-- },
            onConfirm = {
                val index = selectedIndex
                scope.launch {
                    installManager.uninstall(applicationInfos[index].packageName)
                }
                showDeleteDialog--
            }
        )
    }

    Column {
        Box {
            GcTopAppBar(title = stringResource(R.string.uninstall_manager), trailingContent = {
                Text(
                    text = buildAnnotatedString {
                        val originalText = stringResource(R.string.uninstall_tip, "##icon##")
                        originalText.split("##icon##").forEachIndexed { index, string ->
                            if (index == 0) {
                                append(string)
                            } else {
                                appendInlineContent("##icon##")
                                append(string)
                            }
                        }
                    },
                    inlineContent = mapOf(
                        "##icon##" to InlineTextContent(
                            placeholder = Placeholder(
                                width = 32.sp,
                                height = 32.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                            ),
                            children = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_key_ok),
                                    contentDescription = it,
                                    tint = Color.White.copy(alpha = 0.6f),
                                )
                            }
                        )
                    ),
                    style = GcTextStyle.Style4,
                    maxLines = 1,
                )
            })
            if (applicationInfos.isEmpty()) {
                EmptyBackground(text = stringResource(R.string.no_install_record))
            }
        }
        if (applicationInfos.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 42.5.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                itemsIndexed(applicationInfos) { index, info ->
                    val size = File(info.sourceDir).length()
                    val drawable = remember(info.packageName) { pm.getApplicationIcon(info.packageName) }
                    UninstallerItem(
                        img = drawable,
                        title = pm.getApplicationLabel(info).toString(),
                        subTitle = uninstallList.firstOrNull {
                            it.packageName == info.packageName && it.isFinished().not()
                        }?.let { stringResource(R.string.uninstalling) } ?: size.formatSize(),
                        onClick = {
                            val intent = context.packageManager.getLaunchIntentForPackage(info.packageName)
                            if (intent != null) {
                                context.startActivity(intent)
                            } else {
                                Log.e(TAG, "openApp error: $info.packageName not found")
                            }
                        },
                        onLongClick = {
                            selectedIndex = index
                            showDeleteDialog = 1
                        },
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun UninstallerScreenPreview() {
}

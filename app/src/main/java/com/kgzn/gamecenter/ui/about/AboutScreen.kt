package com.kgzn.gamecenter.ui.about


import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.tv.material3.Text
import com.kgzn.gamecenter.BuildConfig
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.GcTopAppBar
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import kotlinx.serialization.Serializable


@Serializable
object AboutRoute

fun NavController.navigateToAbout(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(AboutRoute, navOptions)
}

fun NavGraphBuilder.aboutScreen() {
    composable<AboutRoute> {
        AboutScreen()
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun AboutScreen() {
    Box {
        GcTopAppBar(title = stringResource(R.string.about))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(70.dp),
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = stringResource(R.string.app_name),
            )
            Spacer(modifier = Modifier.height(11.dp))
            Text(text = stringResource(R.string.app_name), style = GcTextStyle.Style1)
            Spacer(modifier = Modifier.height(7.dp))
            Text(text = BuildConfig.VERSION_NAME, style = GcTextStyle.Style2)
            val context = LocalContext.current
            val packageManager = context.packageManager
            val applicationInfo =
                packageManager?.getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA)
            val appId = applicationInfo?.metaData?.getString("ProductAppID", null)
            if (appId != null) {
                Spacer(modifier = Modifier.height(7.dp))
                Text(text = appId, style = GcTextStyle.Style2)
            }
        }
    }
}

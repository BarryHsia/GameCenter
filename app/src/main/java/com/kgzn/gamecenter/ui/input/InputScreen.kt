package com.kgzn.gamecenter.ui.input

import android.view.InputDevice
import android.view.KeyEvent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.component.GcTopAppBar
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import kotlinx.serialization.Serializable

@Serializable
private object GuideRoute

@Serializable
private object ManagerRoute

@Composable
fun InputScreen() {
    val context = LocalContext.current
    val inputViewModel = viewModel { InputViewModel(context) }
    val startDestination = remember { if (inputViewModel.devices.isEmpty()) GuideRoute else ManagerRoute }

    val navController = rememberNavController()
    fun <T : Any> NavHostController.simpleNavigate(route: T) {
        if (currentDestination?.route == route.javaClass.name) {
            return
        }
        navigate(route) {
            popUpTo(currentDestination?.route ?: "") { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        modifier = Modifier
            .onPreviewKeyEvent {
                when (it.key) {
                    Key.DirectionUp -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            navController.simpleNavigate(GuideRoute)
                        }
                        true
                    }

                    Key.DirectionDown -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            navController.simpleNavigate(ManagerRoute)
                        }
                        true
                    }

                    else -> false
                }

            }
            .focusTarget(),
        navController = navController, startDestination = startDestination
    ) {
        composable<GuideRoute>(
            enterTransition = { slideInVertically { -it } },
            exitTransition = { slideOutVertically { -it } },
        ) {
            GuidePage()
        }
        composable<ManagerRoute>(
            enterTransition = { slideInVertically { it } },
            exitTransition = { slideOutVertically { it } },
        ) {
            ManagePage(
                modifier = Modifier,
                devices = inputViewModel.devices,
                onDeviceClick = { device ->
                    if (device?.bluetoothAddress != null) {
                        inputViewModel.disconnectDevice(device.id)
                    }
                }
            )
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
private fun GuidePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GcTopAppBar(title = stringResource(R.string.connect_guide))
        Spacer(modifier = Modifier.height(21.5.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            ConnectionGuideItem(iconId = R.drawable.ic_usb, text = stringResource(R.string.connect_usb))
            ConnectionGuideItem(iconId = R.drawable.ic_bluetooth, text = stringResource(R.string.connect_bluetooth))
        }
        Spacer(modifier = Modifier.height(13.5.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    val originalText = stringResource(R.string.manage_devices_tip, "##icon##")
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
                                painter = painterResource(R.drawable.ic_key_down),
                                contentDescription = it,
                                tint = Color.White,
                            )
                        }
                    )
                ),
                style = GcTextStyle.Style3,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ConnectionGuideItem(
    iconId: Int,
    text: String,
) {
    Surface(
        modifier = Modifier.size(300.dp),
        shape = RoundedCornerShape(5.dp),
        colors = SurfaceDefaults.colors(containerColor = Color.White.copy(0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 75.75.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(75.dp),
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = Color.White,
            )
            Text(text, style = GcTextStyle.Style9)
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
private fun ManagePage(
    modifier: Modifier = Modifier,
    devices: List<InputDevice> = listOf(),
    onDeviceClick: (InputDevice?) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    SideEffect {
        focusRequester.requestFocus()
    }
    Column(
        modifier = modifier.focusRequester(focusRequester),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier.padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                Icon(
                    modifier = Modifier.size(22.5.dp),
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null
                )
            }
            Text(
                text = stringResource(R.string.connected_devices, devices.size),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    val originalText = stringResource(R.string.connect_guide_tip, "##icon##")
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
                                painter = painterResource(R.drawable.ic_key_up),
                                contentDescription = it,
                                tint = Color.White,
                            )
                        }
                    )
                ),
                style = GcTextStyle.Style3,
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(25.dp),
        ) {
            repeat(4) { index ->
                val device = devices.getOrNull(index)
                val isBluetoothDevice = device?.bluetoothAddress != null
                InputDeviceItem(
                    iconId = if (device != null) R.drawable.ic_gamepads else R.drawable.ic_mine,
                    iconColor = if (device?.isEnabled == true) Color(0xFF1B77F7) else Color.White,
                    playerName = stringResource(R.string.player) + " ${index + 1}",
                    deviceName = device?.name ?: "",
                    onClick = {
                        onDeviceClick(device)
                    },
                    showTip = isBluetoothDevice,
                    enabled = device != null,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(75.dp),
        ) {
            KeyTips(R.drawable.ic_key_a, stringResource(R.string.confirm), Color(0xFF1B77F7))
            KeyTips(R.drawable.ic_key_b, stringResource(R.string.back), Color(0xFFE62D27))
        }
    }
}

@Composable
private fun KeyTips(
    iconId: Int,
    text: String,
    iconColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            modifier = Modifier.size(35.dp),
            painter = painterResource(iconId),
            contentDescription = null,
            tint = iconColor,
        )
        Text(text, style = GcTextStyle.Style3)
    }
}

@Composable
private fun InputDeviceItem(
    iconId: Int,
    iconColor: Color = Color.White,
    playerName: String,
    deviceName: String,
    onClick: () -> Unit = {},
    showTip: Boolean = false,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    fun Modifier.marqueeOnFocus() = this.then(
        if (isFocused) Modifier.basicMarquee() else Modifier
    )

    CommonSurface(
        modifier = Modifier
            .size(200.dp)
            .focusProperties {
                canFocus = enabled
            },
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(46.5.dp))
            Icon(
                modifier = Modifier.size(45.dp),
                painter = painterResource(iconId),
                contentDescription = null,
                tint = iconColor,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                playerName,
                style = GcTextStyle.Style9,
                modifier = Modifier.marqueeOnFocus(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                deviceName, style = GcTextStyle.Style3,
                modifier = Modifier
                    .alpha(0.75f)
                    .marqueeOnFocus(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(10.dp))
            if (showTip && isFocused) {
                Text(
                    stringResource(R.string.disconnect),
                    style = GcTextStyle.Style6,
                    modifier = Modifier
                        .background(Color(0xFF1b77f7), RoundedCornerShape(5.dp))
                        .padding(vertical = 5.dp, horizontal = 18.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

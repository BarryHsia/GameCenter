package com.kgzn.gamecenter.ui.gamedetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

const val TAG = "SupportTypeBar"

@Composable
fun SupportTypeBar(
    modifier: Modifier = Modifier,
    supportTypes: () -> List<String> = { emptyList() },
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for ((index, type) in supportTypes().withIndex()) {
            key(type) {
                if (index == 0) {
                    SupportTypeItem(type = type)
                } else {
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 11.75.dp)
                            .clip(shape = RoundedCornerShape(2.5.dp))
                            .size(width = 1.5.dp, height = 22.5.dp)
                            .background(Color.White.copy(0.8f))
                    )
                    SupportTypeItem(type = type)
                }
            }
        }
    }
}

@Preview
@Composable
fun SupportTypeBarPreview() {
    SupportTypeBar(
        supportTypes = { listOf("handle", "remote") }
    )
}



@Composable
fun SupportTypeItem(
    modifier: Modifier = Modifier,
    type: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(
                id = when (type) {
                    "handle" -> R.drawable.game_type_handle
                    "remote" -> R.drawable.game_type_remote
                    else -> R.drawable.game_type_handle
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(22.5.dp),
            tint = Color.White.copy(0.8f)
        )
        Text(
            text = when (type) {
                "handle" -> stringResource(R.string.handle)
                "remote" -> stringResource(R.string.remote)
                else -> stringResource(R.string.handle)
            },
            style = GcTextStyle.Style6,
            color = Color.White.copy(0.8f)
        )
    }

}
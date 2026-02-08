package com.kgzn.gamecenter.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R

@Composable
fun GcTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(22.5.dp),
            painter = painterResource(R.drawable.ic_back),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(7.5.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

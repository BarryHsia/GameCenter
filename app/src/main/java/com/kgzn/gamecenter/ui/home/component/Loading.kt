package com.kgzn.gamecenter.ui.home.component

import android.graphics.drawable.AnimatedImageDrawable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.kgzn.gamecenter.R

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                setImageResource(R.drawable.loading)
                (drawable as? AnimatedImageDrawable)?.start()
            }
        }
    )
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}

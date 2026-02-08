package com.kgzn.gamecenter.ui.home.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.google.gson.Gson
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

data class TopDescState(
    val name: String,
    val tags: List<String>,
    val desc: String,
)

object TopDescStateSaver : Saver<TopDescState?, String> {
    override fun restore(value: String): TopDescState? {
        return Gson().fromJson(value, TopDescState::class.java)
    }

    override fun SaverScope.save(value: TopDescState?): String? = Gson().toJson(value)
}

@Composable
fun TopDesc(state: TopDescState) {
    Column(
        modifier = Modifier.padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Spacer(modifier = Modifier.height(68.25.dp))
        Text(
            text = state.name,
            modifier = Modifier.basicMarquee(),
            style = GcTextStyle.Style10,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.5.dp)
        ) {
            for (tag in state.tags) {
                key(tag) {
                    Surface(
                        modifier = Modifier.height(25.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = SurfaceDefaults.colors(containerColor = Color.White.copy(0.2f)),
                    ) {
                        Box {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 11.5.dp),
                                text = tag,
                                style = GcTextStyle.Style6,
                            )
                        }
                    }
                }
            }
        }
        Text(
            text = state.desc,
            style = GcTextStyle.Style3.copy(lineHeight = with(LocalDensity.current) { 22.5.dp.toSp() }),
            modifier = Modifier
                .height(70.75.dp)
                .width(376.25.dp)
                .alpha(0.75f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
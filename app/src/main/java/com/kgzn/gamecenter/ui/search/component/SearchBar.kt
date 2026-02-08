package com.kgzn.gamecenter.ui.search.component

import android.view.KeyEvent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.CommonSurface
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (KeyboardActionScope.() -> Unit)? = null,
    searchFocusRequester: FocusRequester = remember { FocusRequester() },
) {

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
    }

    val interactionSource = remember { MutableInteractionSource() }
    CommonSurface(
        modifier = modifier,
        onClick = { searchFocusRequester.requestFocus() },
        focusedScale = 1f,
        interactionSource = interactionSource,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        SearchTextField(
            modifier = Modifier
                .matchParentSize()
                .focusRequester(searchFocusRequester)
                .onPreviewKeyEvent {
                    if (it.key == Key.DirectionCenter) {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            keyboardController?.show()
                        }
                        true
                    } else {
                        false
                    }
                },
            value = text,
            onValueChange = onTextChange,
            textStyle = GcTextStyle.Style3,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = onSearch
            ),
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(0.5f),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                )
            },
            contentPadding = PaddingValues(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                disabledPlaceholderColor = Color.White.copy(alpha = 0.5f),
                errorPlaceholderColor = Color.White.copy(alpha = 0.5f),
            ),
            interactionSource = interactionSource,
            placeholder = {
                Text(stringResource(id = R.string.search_hint))
            }
        )
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        text = "",
        onTextChange = {},
    )
}

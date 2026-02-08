package com.kgzn.gamecenter.ui.web

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kgzn.gamecenter.ui.home.component.Loading
import com.kgzn.gamecenter.uitls.clickElementById
import com.kgzn.gamecenter.uitls.fixPageSize

private const val TAG = "WebScreen"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebScreen(
    url: String,
    navHostController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    val webView = remember {
        WebView(context).apply {
            setBackgroundColor(Color.Transparent.value.toInt())

            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES

            isScrollbarFadingEnabled = true
            isSaveEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
            setNetworkAvailable(true)

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                mediaPlaybackRequiresUserGesture = true

                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT

                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                allowContentAccess = true
                allowFileAccess = true

                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setNeedInitialFocus(false)
                useWideViewPort = true
            }
            setInitialScale(100)
            setWebViewClient(object : WebViewClient() {

                override fun onPageCommitVisible(view: WebView, url: String?) {
                    Log.d(TAG, "onPageCommitVisible: $url")
                    loading = false
                    fixPageSize()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d(TAG, "onPageFinished: $url")
                    evaluateJavascript("document.body.style.position = 'unset'") {
                        Log.d(TAG, "evaluateJavascript: $it")
                    }
                    fixPageSize()
                }
            })
        }
    }

    BackHandler(true) {
        webView.clickElementById("yellowButton", callback = { success ->
            Log.d(TAG, "clickElementById<yellowButton>: $success")
            if (!success) {
                navHostController.popBackStack()
            }
        })
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        factory = { context ->
            webView.apply {
                loadUrl(url)
                loading = true
            }
        },
        onRelease = {
            it.destroy()
        },
    )

    if (loading) {
        Box(Modifier.fillMaxSize()) {
            Loading(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center),
            )
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun WebScreenPreview() {
    WebScreen("http://192.168.1.8:8185/")
}

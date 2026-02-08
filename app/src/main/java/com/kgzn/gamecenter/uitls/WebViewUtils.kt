package com.kgzn.gamecenter.uitls

import android.util.Log
import android.webkit.WebView
import org.json.JSONObject

private const val TAG = "WebViewUtils"

fun WebView.clickElementById(elementId: String, callback: ((Boolean) -> Unit)? = null) {
    val javascript = """
        (function() {
            var element = document.getElementById('$elementId');
            if (element) {
                element.click();
                return true;
            }
            return false;
        })();
    """.trimIndent()

    evaluateJavascript(javascript) { result ->
        val success = result == "true"
        callback?.invoke(success)
    }
}

fun WebView.fixPageSize() {
    runCatching {
        evaluateJavascript(
            "var content = document.getElementById('main');"
                    + "content = content ? content : document.getElementById('Displaywrapper');"
                    + "content = content ? content : document.body;"
                    + "var width = content.getBoundingClientRect().width;"
                    + "var height = content.getBoundingClientRect().height;"
                    + "JSON.stringify({height, width});"
        ) { value: String? ->
            Log.d(TAG, "onPageCommitVisible: $value")
            if (value == null || value.isEmpty() || value == "null") {
                return@evaluateJavascript
            }
            val json = JSONObject(
                value
                    .replace("^\"|\"$".toRegex(), "")
                    .replace("\\\"", "\"")
            )
            val viewWidth = this.width
            val viewHeight = this.height
            val width = json.getInt("width").takeIf { it > 0 } ?: viewWidth
            val height = json.getInt("height").takeIf { it > 0 } ?: viewHeight
            Log.d(TAG, "onPageCommitVisible: bw, $width, bh, $height, vw: $viewWidth, vh: $viewHeight")
            if (width < viewWidth || height < viewHeight) {
                val scaleW = (1.0f * viewWidth / width * 100).toInt()
                val scaleH = (1.0f * viewHeight / height * 100).toInt()
                setInitialScale(minOf(scaleW, scaleH))
            }

        }
    }.onFailure {
        Log.e(TAG, "onPageCommitVisible: $it", it)
    }
}
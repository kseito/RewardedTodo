package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class TodoistAuthWebViewClient(private val listener: AuthResultListener) : WebViewClient() {

    interface AuthResultListener {
        fun onAuthSuccess()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let { url ->
            //FIXME check scheme and host
            if (url.host?.contains("localhost") == true) {
                //TODO return code
                listener.onAuthSuccess()
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}
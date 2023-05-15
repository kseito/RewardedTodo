package jp.kztproject.rewardedtodo.feature.auth

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class TodoistAuthWebViewClient(private val listener: AuthResultListener) : WebViewClient() {

    interface AuthResultListener {
        fun onAuthSuccess(code: String)

        fun onAuthFailure()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let { url ->
            //FIXME check scheme and host
            if (url.host?.contains("localhost") == true) {
                try {
                    val code = url.getQueryParameter("code")!!
                    listener.onAuthSuccess(code)
                } catch (e: Exception) {
                    listener.onAuthFailure()
                }
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}

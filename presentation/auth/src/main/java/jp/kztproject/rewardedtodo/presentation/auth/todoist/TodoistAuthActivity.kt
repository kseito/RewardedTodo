package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.kztproject.rewardedtodo.presentation.auth.R
import jp.kztproject.rewardedtodo.presentation.auth.databinding.ActivityTodoistAuthBinding

class TodoistAuthActivity: AppCompatActivity(), TodoistAuthWebViewClient.AuthResultListener {

    private lateinit var binding : ActivityTodoistAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todoist_auth)

        val clientId = getString(R.string.todoist_client_id)
        val clientSecret = getString(R.string.todoist_client_secret)
        binding.webView.loadUrl("https://todoist.com/oauth/authorize?client_id=$clientId&scope=data:read&state=$clientSecret")
        binding.webView.webViewClient = TodoistAuthWebViewClient(this)
    }

    override fun onAuthSuccess() {
        //TODO notify auth success
        finish()
    }
}
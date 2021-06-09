package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.presentation.auth.BuildConfig
import jp.kztproject.rewardedtodo.presentation.auth.R
import jp.kztproject.rewardedtodo.presentation.auth.databinding.ActivityTodoistAuthBinding

@AndroidEntryPoint
class TodoistAuthActivity : AppCompatActivity(),
        TodoistAuthWebViewClient.AuthResultListener,
        TodoistAuthViewModel.Callback {

    private lateinit var binding: ActivityTodoistAuthBinding

    private val viewModel: TodoistAuthViewModel by viewModels()

    private val clientId by lazy {
        BuildConfig.TODOIST_CLIENT_ID
    }
    private val clientSecret by lazy {
        BuildConfig.TODOIST_CLIENT_SECRET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.callback = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todoist_auth)

        binding.webView.loadUrl("https://todoist.com/oauth/authorize?client_id=$clientId&scope=data:read&state=$clientSecret")
        binding.webView.webViewClient = TodoistAuthWebViewClient(this)
    }

    override fun onAuthSuccess(code: String) {
        viewModel.requireAccessToken(clientId, clientSecret, code)
    }

    override fun onAuthFailure() {
        Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show()
    }

    override fun onRequireAccessTokenSuccess() {
        finish()
    }

    override fun onRequireAccessTokenFailed() {
        Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show()
    }
}

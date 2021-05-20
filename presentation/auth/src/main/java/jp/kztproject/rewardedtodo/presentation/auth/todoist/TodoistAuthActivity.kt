package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import jp.kztproject.rewardedtodo.presentation.auth.R
import jp.kztproject.rewardedtodo.presentation.auth.databinding.ActivityTodoistAuthBinding
import javax.inject.Inject

class TodoistAuthActivity : AppCompatActivity(), TodoistAuthWebViewClient.AuthResultListener, TodoistAuthViewModel.Callback {

    private lateinit var binding: ActivityTodoistAuthBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoistAuthViewModel

    private val clientId by lazy {
        getString(R.string.todoist_client_id)
    }
    private val clientSecret by lazy {
        getString(R.string.todoist_client_secret)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoistAuthViewModel::class.java)
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

    override fun onRequireAccessTokenSuccess(accessToken: String) {
        Toast.makeText(this, "My token is $accessToken", Toast.LENGTH_LONG).show()
    }

    override fun onRequireAccessTokenFailed() {
        Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show()
    }
}
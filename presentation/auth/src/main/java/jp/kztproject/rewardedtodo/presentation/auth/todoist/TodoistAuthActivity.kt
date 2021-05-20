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

class TodoistAuthActivity: AppCompatActivity(), TodoistAuthWebViewClient.AuthResultListener {

    private lateinit var binding : ActivityTodoistAuthBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoistAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoistAuthViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todoist_auth)

        val clientId = getString(R.string.todoist_client_id)
        val clientSecret = getString(R.string.todoist_client_secret)
        binding.webView.loadUrl("https://todoist.com/oauth/authorize?client_id=$clientId&scope=data:read&state=$clientSecret")
        binding.webView.webViewClient = TodoistAuthWebViewClient(this)
    }

    override fun onAuthSuccess(code: String) {
        //TODO notify auth success
        finish()
    }

    override fun onAuthFailure() {
        Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show()
    }
}
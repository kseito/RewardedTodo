package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import jp.kztproject.rewardedtodo.presentation.auth.BuildConfig
import jp.kztproject.rewardedtodo.presentation.auth.R
import jp.kztproject.rewardedtodo.presentation.auth.databinding.ActivityTodoistAuthBinding
import javax.inject.Inject

class TodoistAuthActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        TodoistAuthWebViewClient.AuthResultListener,
        TodoistAuthViewModel.Callback {

    private lateinit var binding: ActivityTodoistAuthBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoistAuthViewModel

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private val clientId by lazy {
        BuildConfig.TODOIST_CLIENT_ID
    }
    private val clientSecret by lazy {
        BuildConfig.TODOIST_CLIENT_SECRET
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

    override fun onRequireAccessTokenSuccess() {
        finish()
    }

    override fun onRequireAccessTokenFailed() {
        Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}
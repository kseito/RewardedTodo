package kztproject.jp.splacounter.auth.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import project.seito.auth.R
import project.seito.auth.databinding.FragmentAuthBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class AuthFragment : Fragment(), AuthViewModel.Callback {

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(activity).apply {
            setMessage("Now Loading...")
        }
    }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private lateinit var binding: FragmentAuthBinding
    private lateinit var viewModel: AuthViewModel

    companion object {

        fun newInstance(): AuthFragment {
            val args = Bundle()
            val fragment = AuthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel::class.java)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onStartAsyncProcess() {
        progressDialog.show()
    }

    override fun onFinishAsyncProcess() {
        progressDialog.dismiss()
    }

    override fun onSuccessSignUp() {
        Toast.makeText(context, "Signed up!", Toast.LENGTH_SHORT).show()

        activity?.let {
            fragmentTransitionManager.transitionToRewardFragment(it)
        }
    }

    override fun onFailedSignUp() {
        Toast.makeText(activity, R.string.error_sign_up, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessLogin() {
        Toast.makeText(activity, R.string.login_succeeded, Toast.LENGTH_SHORT).show()

        activity?.let {
            fragmentTransitionManager.transitionToRewardFragment(it)
        }
    }

    override fun onFailedLogin(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(activity, R.string.login_failed, Toast.LENGTH_SHORT).show()
    }

    override fun onError(@StringRes stringId: Int) {
        Toast.makeText(context, getString(stringId), Toast.LENGTH_SHORT).show()
    }
}

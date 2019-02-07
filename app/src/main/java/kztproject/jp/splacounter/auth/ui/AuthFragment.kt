package kztproject.jp.splacounter.auth.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.databinding.FragmentAuthBinding
import kztproject.jp.splacounter.reward.list.ui.RewardFragment
import kztproject.jp.splacounter.ui_common.replaceFragment
import javax.inject.Inject

class AuthFragment : Fragment(), AuthViewModel.Callback {

    private var progressDialog: ProgressDialog? = null
    private var binding: FragmentAuthBinding? = null

    @Inject
    lateinit var viewModel: AuthViewModel

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
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding!!.viewModel = viewModel
        return binding!!.root
    }

    override fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("Now Loading...")
        }
        progressDialog!!.show()
    }

    override fun dismissProgressDialog() {
        progressDialog!!.dismiss()
    }

    override fun signUpSucceeded() {
        Toast.makeText(context, "Signed up!", Toast.LENGTH_SHORT).show()

        activity?.replaceFragment(R.id.container, RewardFragment.newInstance())
    }

    override fun loginSucceeded() {
        Toast.makeText(activity, R.string.login_succeeded, Toast.LENGTH_SHORT).show()

        activity?.replaceFragment(R.id.container, RewardFragment.newInstance())
    }

    override fun loginFailed(e: Throwable) {
        Toast.makeText(activity, R.string.login_failed, Toast.LENGTH_SHORT).show()
    }

    override fun showError(@StringRes stringId: Int) {
        Toast.makeText(context, getString(stringId), Toast.LENGTH_SHORT).show()
    }
}
package kztproject.jp.splacounter.view.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.activity.RewardActivity
import kztproject.jp.splacounter.databinding.FragmentPlayBinding
import kztproject.jp.splacounter.viewmodel.PlayViewModel
import javax.inject.Inject

class PlayFragment : Fragment(), PlayViewModel.Callback {

    @Inject
    lateinit var viewModel: PlayViewModel

    private var progressDialog: ProgressDialog? = null

    lateinit var binding: FragmentPlayBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Now Loading...")

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        initCounter()
    }

    private fun initCounter() {
        viewModel.getGameCount()
    }

    override fun showProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.show()
        }
    }

    override fun dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    override fun showGameCount(gameCount: Int) {
        binding.textCounter.text = gameCount.toString()
        binding.consumeButton.isEnabled = gameCount > 0
    }

    override fun showReward(gameCount: Int) {
        startActivity(RewardActivity.createIntent(context, gameCount))
    }

    override fun showError(e: Throwable) {
        Toast.makeText(activity, "Error:" + e.message, Toast.LENGTH_LONG).show()
    }

    companion object {

        fun newInstance(): PlayFragment {
            return PlayFragment()
        }
    }
}

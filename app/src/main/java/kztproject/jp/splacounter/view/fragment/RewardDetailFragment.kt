package kztproject.jp.splacounter.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.databinding.FragmentRewardDetailBinding
import kztproject.jp.splacounter.viewmodel.RewardDetailViewModel
import kztproject.jp.splacounter.viewmodel.RewardDetailViewModelCallback
import javax.inject.Inject

class RewardDetailFragment : Fragment(), RewardDetailViewModelCallback {

    private lateinit var binding: FragmentRewardDetailBinding

    @Inject
    lateinit var viewModel: RewardDetailViewModel

    companion object {

        private const val ARGS_ID = "id"

        fun newInstance(): RewardDetailFragment {
            return RewardDetailFragment()
        }

        fun newInstance(id: Int): RewardDetailFragment {
            return RewardDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARGS_ID, id)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val id = arguments.getInt(ARGS_ID)
            viewModel.initialize(id)
        }
    }

    override fun onSaveCompleted(rewardName: String) {
        Toast.makeText(context, "Added $rewardName", Toast.LENGTH_SHORT).show()
        fragmentManager.popBackStack()
    }

    override fun onError(resourceId: Int) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
    }
}
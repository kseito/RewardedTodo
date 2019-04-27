package kztproject.jp.splacounter.reward.detail.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import project.seito.reward.databinding.FragmentRewardDetailBinding
import javax.inject.Inject

class RewardDetailFragment : Fragment(), RewardDetailViewModelCallback {

    private lateinit var binding: FragmentRewardDetailBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardDetailViewModel::class.java)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt(ARGS_ID) ?: return
        viewModel.initialize(id)
    }

    override fun onSaveCompleted(rewardName: String) {
        Toast.makeText(context, "Added $rewardName", Toast.LENGTH_SHORT).show()
        fragmentManager?.popBackStack()
    }

    override fun onError(resourceId: Int) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
    }
}
package kztproject.jp.splacounter.reward.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.reward.detail.ui.RewardDetailViewModel
import kztproject.jp.splacounter.reward.detail.ui.RewardDetailViewModelCallback
import project.seito.reward.databinding.FragmentRewardDetailBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class RewardDetailFragment : Fragment(), RewardDetailViewModelCallback {

    private lateinit var binding: FragmentRewardDetailBinding

    private val args: RewardDetailFragmentArgs by navArgs()

    @Inject
    lateinit var transitionManager: IFragmentsTransitionManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: RewardDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardDetailViewModel::class.java)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.rewardId
        if (id <= 0) {
            return
        }
        viewModel.initialize(id)
    }

    override fun onSaveCompleted(rewardName: String) {
        Toast.makeText(context, "Added $rewardName", Toast.LENGTH_SHORT).show()
        transitionManager.popBackStack(activity)
    }

    override fun onError(resourceId: Int) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
    }
}
package jp.kztproject.rewardedtodo.reward.presentation.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import dagger.android.support.AndroidSupportInjection
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import project.seito.reward.R
import project.seito.reward.databinding.FragmentRewardDetailBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class RewardDetailFragment : DialogFragment(), RewardDetailViewModelCallback {

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
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
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
        viewModel.initialize(RewardId(id))
    }

    override fun onSaveCompleted(rewardName: String) {
        Toast.makeText(context, "Added $rewardName", Toast.LENGTH_SHORT).show()
        transitionManager.popBackStack(activity)
    }

    override fun onConfirmToRewardDeletion(reward: Reward) {
        val activityContext = activity as Context? ?: return
        AlertDialog.Builder(activityContext)
                .setTitle(R.string.confirm_title)
                .setMessage(String.format(getString(R.string.delete_confirm_message), reward.name.value))
                .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteReward() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> run {} }
                .show()
    }

    override fun onDeleteCompleted(rewardName: RewardName) {
        val message = String.format(getString(R.string.reward_delete_message), rewardName.value)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        transitionManager.popBackStack(activity)
    }

    override fun onError(resourceId: Int) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
    }
}
package jp.kztproject.rewardedtodo.presentation.reward.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.presentation.reward.R
import jp.kztproject.rewardedtodo.presentation.reward.databinding.FragmentRewardDetailBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@AndroidEntryPoint
class RewardDetailFragment : DialogFragment(), RewardDetailViewModelCallback {

    private lateinit var binding: FragmentRewardDetailBinding

    private val args: RewardDetailFragmentArgs by navArgs()

    @Inject
    lateinit var transitionManager: IFragmentsTransitionManager

    private val viewModel: RewardDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
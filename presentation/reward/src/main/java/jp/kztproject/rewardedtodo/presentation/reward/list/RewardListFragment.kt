package jp.kztproject.rewardedtodo.presentation.reward.list

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.presentation.reward.R
import jp.kztproject.rewardedtodo.presentation.reward.databinding.FragmentRewardBinding
import jp.kztproject.rewardedtodo.presentation.reward.helper.showDialog
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@AndroidEntryPoint
class RewardListFragment : Fragment(), RewardViewModelCallback, ClickListener {

    private val viewModel: RewardListViewModel by viewModels()

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private lateinit var binding: FragmentRewardBinding

    private var animation: Animator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    RewardListScreen(viewModel)
                }
            }
        }
    }


    @Composable
    private fun RewardListScreen(viewModel: RewardListViewModel) {
        val rewards by viewModel.rewardListLiveData.observeAsState()
        RewardList(rewards)
    }

    @Composable
    private fun RewardList(rewards: List<Reward>?) {
        Column {
            rewards?.forEachIndexed { index, reward ->
                RewardItem(reward)
                if (index < rewards.lastIndex) {
                    Divider()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun RewardListPreview() {
        RewardList(
            rewards = listOf(
                Reward(
                    RewardId(1),
                    RewardName("PS5"),
                    1,
                    Probability(1f),
                    RewardDescription("this is very rare"),
                    true
                ),
                Reward(
                    RewardId(1),
                    RewardName("New Macbook Pro"),
                    1,
                    Probability(1f),
                    RewardDescription("M1 Max is great"),
                    true
                )
            )
        )
    }

    @Composable
    private fun RewardItem(reward: Reward) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(reward) }
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(8f)
                ) {
                    Text(
                        text = reward.name.value,
                        style = MaterialTheme.typography.h4
                    )
                    Text(
                        text = reward.description.value ?: "",
                        color = Color.Gray
                    )
                }
                Text(
                    text = "${reward.probability.value} %",
                    modifier = Modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.h5
                )
            }
        }
    }

    @Preview
    @Composable
    private fun RewardItemPreview() {
        val reward = Reward(
            RewardId(1),
            RewardName("PS5"),
            1,
            Probability(1f),
            RewardDescription("this is very rare"),
            true
        )
        RewardItem(reward)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCallback(this)
        if (savedInstanceState == null) {
            viewModel.loadRewards()
        }
    }

    override fun onItemClick(rewardEntity: Reward) {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity, rewardEntity.rewardId.value)
    }

    override fun showRewardDetail() {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity)
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_acquire_reward, Toast.LENGTH_SHORT).show()
    }

    override fun onPointLoadFailed() {
        Toast.makeText(context, "Point load failed", Toast.LENGTH_SHORT).show()
    }

    override fun onStartLoadingPoint() {
        animation = AnimatorInflater.loadAnimator(context, R.animator.rotate_animation).apply {
            setTarget(binding.syncButton)
            start()
        }
    }

    override fun onTerminateLoadingPoint() {
        animation?.cancel()
    }

    override fun onHitLottery(reward: Reward) {
        val message = "You won ${reward.name.value}!"
        showDialog(message)
    }

    override fun onMissLottery() {
        val message = "You missed the lottery"
        showDialog(message)
    }
}

interface ClickListener {
    fun onItemClick(rewardEntity: Reward)
}

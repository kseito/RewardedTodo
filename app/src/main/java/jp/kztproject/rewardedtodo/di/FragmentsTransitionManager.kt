package jp.kztproject.rewardedtodo.di

import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import jp.kztproject.rewardedtodo.R
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListFragmentDirections
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class FragmentsTransitionManager @Inject constructor() : IFragmentsTransitionManager {
    override fun transitionToAuthFragment(activity: FragmentActivity?) {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(RewardListFragmentDirections.actionRewardListFragmentToTodoistAuthFragment())
    }

    override fun transitionToRewardDetailFragment(activity: FragmentActivity?) {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(RewardListFragmentDirections.toRewardDetailFragment())
    }

    override fun transitionToRewardDetailFragment(activity: FragmentActivity?, rewardId: Int) {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(RewardListFragmentDirections.toRewardDetailFragment(rewardId))
    }

    override fun popBackStack(activity: FragmentActivity?) {
        activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
    }
}
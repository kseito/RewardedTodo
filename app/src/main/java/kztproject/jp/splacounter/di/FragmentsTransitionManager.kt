package kztproject.jp.splacounter.di

import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.auth.ui.AuthFragmentDirections
import kztproject.jp.splacounter.reward.presentation.list.RewardListFragmentDirections
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class FragmentsTransitionManager @Inject constructor() : IFragmentsTransitionManager {
    override fun transitionToAuthFragment(activity: FragmentActivity?) {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(RewardListFragmentDirections.toAuthFragment())
    }

    override fun transitionToRewardFragment(activity: FragmentActivity) =
            activity.findNavController(R.id.nav_host_fragment).navigate(AuthFragmentDirections.toRewardListFragment())

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
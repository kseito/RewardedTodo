package kztproject.jp.splacounter.di

import android.support.v4.app.FragmentActivity
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.auth.ui.AuthFragment
import kztproject.jp.splacounter.reward.detail.ui.RewardDetailFragment
import kztproject.jp.splacounter.reward.list.ui.RewardListFragment
import kztproject.jp.splacounter.ui_common.replaceFragment
import kztproject.jp.splacounter.ui_common.replaceFragmentWithStack
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class FragmentsTransitionManager @Inject constructor() : IFragmentsTransitionManager {
    override fun transitionToAuthFragment(activity: FragmentActivity?) {
        activity?.replaceFragment(R.id.container, AuthFragment.newInstance())
    }

    override fun transitionToRewardFragment(activity: FragmentActivity) =
            activity.replaceFragment(R.id.container, RewardListFragment.newInstance())

    override fun transitionToRewardDetailFragment(activity: FragmentActivity?) {
        activity?.replaceFragmentWithStack(R.id.container, RewardDetailFragment.newInstance())
    }

    override fun transitionToRewardDetailFragment(activity: FragmentActivity?, rewardId: Int) {
        activity?.replaceFragment(R.id.container, RewardDetailFragment.newInstance(rewardId))
    }
}
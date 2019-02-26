package kztproject.jp.splacounter.di

import android.support.v4.app.FragmentActivity
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.reward.list.ui.RewardFragment
import kztproject.jp.splacounter.ui_common.replaceFragment
import project.seito.screen_transition.IFragmentsInitializer
import javax.inject.Inject

class FragmentsInitializer @Inject constructor() : IFragmentsInitializer {

    override fun getRewardFragment(activity: FragmentActivity) =
            activity.replaceFragment(R.id.container, RewardFragment.newInstance())
}
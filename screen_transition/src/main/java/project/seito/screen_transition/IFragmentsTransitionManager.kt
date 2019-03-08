package project.seito.screen_transition

import android.support.v4.app.FragmentActivity

interface IFragmentsTransitionManager {

    fun transitionToRewardFragment(activity: FragmentActivity)

    fun transitionToRewardDetailFragment(activity: FragmentActivity?)

    fun transitionToRewardDetailFragment(activity: FragmentActivity?, rewardId: Int)

    fun transitionToAuthFragment(activity: FragmentActivity?)
}
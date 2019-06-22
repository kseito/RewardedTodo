package project.seito.screen_transition

import androidx.fragment.app.FragmentActivity

interface IFragmentsTransitionManager {

    fun transitionToRewardFragment(activity: androidx.fragment.app.FragmentActivity)

    fun transitionToRewardDetailFragment(activity: androidx.fragment.app.FragmentActivity?)

    fun transitionToRewardDetailFragment(activity: androidx.fragment.app.FragmentActivity?, rewardId: Int)

    fun transitionToAuthFragment(activity: androidx.fragment.app.FragmentActivity?)
}
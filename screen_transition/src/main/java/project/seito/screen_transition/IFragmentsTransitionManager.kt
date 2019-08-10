package project.seito.screen_transition

import androidx.fragment.app.FragmentActivity

interface IFragmentsTransitionManager {

    fun transitionToRewardFragment(activity: FragmentActivity)

    fun transitionToRewardDetailFragment(activity: FragmentActivity?)

    fun transitionToRewardDetailFragment(activity: FragmentActivity?, rewardId: Int)

    fun transitionToAuthFragment(activity: FragmentActivity?)

    fun popBackStack(activity: FragmentActivity?)
}
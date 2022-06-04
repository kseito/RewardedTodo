package project.seito.screen_transition

import androidx.fragment.app.FragmentActivity

interface IFragmentsTransitionManager {

    fun transitionToRewardDetailFragment(activity: FragmentActivity?)

    fun transitionToRewardDetailFragment(activity: FragmentActivity?, rewardId: Int)

    fun transitionToAuthFragment(activity: FragmentActivity?)

    fun transitionToTodoListFragment(activity: FragmentActivity?)

    fun popBackStack(activity: FragmentActivity?)
}

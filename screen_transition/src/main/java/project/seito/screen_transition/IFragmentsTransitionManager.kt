package project.seito.screen_transition

import androidx.fragment.app.FragmentActivity

interface IFragmentsTransitionManager {

    fun transitionToTodoListFragment(activity: FragmentActivity)

    fun transitionToRewardListFragment(activity: FragmentActivity)

    fun popBackStack(activity: FragmentActivity?)
}

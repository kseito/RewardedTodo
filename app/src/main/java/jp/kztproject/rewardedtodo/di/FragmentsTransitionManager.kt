package jp.kztproject.rewardedtodo.di

import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import jp.kztproject.rewardedtodo.R
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListFragmentDirections
import jp.kztproject.rewardedtodo.presentation.todo.TodoListFragmentDirections
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class FragmentsTransitionManager @Inject constructor() : IFragmentsTransitionManager {

    override fun transitionToTodoListFragment(activity: FragmentActivity) {
        activity.findNavController(R.id.nav_host_fragment).navigate(RewardListFragmentDirections.toTodoListFragment())
    }

    override fun transitionToRewardListFragment(activity: FragmentActivity) {
        activity.findNavController(R.id.nav_host_fragment).navigate(TodoListFragmentDirections.toRewardListFragment())
    }

    override fun popBackStack(activity: FragmentActivity?) {
        activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
    }
}

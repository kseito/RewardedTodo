package kztproject.jp.splacounter.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.*
import kztproject.jp.splacounter.R
import project.seito.screen_transition.preference.PrefsWrapper

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            delay(500)
            withContext(Dispatchers.Main) {
                transitionToRewardListIfAuthenticated()
            }
        }
    }

    private fun transitionToRewardListIfAuthenticated() {

        val context = activity ?: return

        val prefsWrapper = PrefsWrapper(context)
        if (prefsWrapper.userId != 0L) {
            findNavController().navigate(SplashFragmentDirections.toTodoListFragment())
        } else {
            findNavController().navigate(SplashFragmentDirections.toAuthFragment())
        }
    }
}
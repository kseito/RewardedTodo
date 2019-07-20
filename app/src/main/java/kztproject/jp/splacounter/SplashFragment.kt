package kztproject.jp.splacounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kztproject.jp.splacounter.auth.ui.AuthFragment
import kztproject.jp.splacounter.reward.list.ui.RewardListFragment
import kztproject.jp.splacounter.ui_common.replaceFragmentWithNoStack
import project.seito.screen_transition.preference.PrefsWrapper

class SplashFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return SplashFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            replaceFragmentWithNoStack(R.id.container, RewardListFragment.newInstance())
        } else {
            replaceFragmentWithNoStack(R.id.container, AuthFragment.newInstance())
        }
    }
}
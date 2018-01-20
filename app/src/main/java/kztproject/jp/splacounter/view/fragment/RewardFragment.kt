package kztproject.jp.splacounter.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kztproject.jp.splacounter.MyApplication
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.databinding.FragmentRewardBinding
import kztproject.jp.splacounter.viewmodel.RewardViewModel
import javax.inject.Inject

class RewardFragment : Fragment(), RewardViewModel.Callback {

    @Inject lateinit var viewModel: RewardViewModel

    lateinit var binding:FragmentRewardBinding

    companion object {
        const val ARG_POINT = "point"
        fun newInstance(point: Int) : RewardFragment {
            val args = Bundle()
            args.putInt(ARG_POINT, point)

            val fragment = RewardFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (activity.application as MyApplication).component()!!.inject(this)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun showRewardAdd() {
        val fragment = RewardAddFragment.newInstance()
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.javaClass.canonicalName)
                .commit()
    }
}
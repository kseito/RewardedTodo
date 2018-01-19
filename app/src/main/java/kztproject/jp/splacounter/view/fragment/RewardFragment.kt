package kztproject.jp.splacounter.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kztproject.jp.splacounter.databinding.FragmentRewardBinding

class RewardFragment : Fragment() {

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

}
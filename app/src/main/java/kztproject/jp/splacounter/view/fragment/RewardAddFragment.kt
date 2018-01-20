package kztproject.jp.splacounter.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kztproject.jp.splacounter.databinding.FragmentRewardAddBinding

class RewardAddFragment: Fragment() {

    private lateinit var binding: FragmentRewardAddBinding

    companion object {
        fun newInstance(): RewardAddFragment {
            return RewardAddFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardAddBinding.inflate(inflater, container,false)
        return binding.root
    }
}
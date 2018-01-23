package kztproject.jp.splacounter.view.fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import kztproject.jp.splacounter.MyApplication
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.databinding.FragmentRewardBinding
import kztproject.jp.splacounter.databinding.ItemRewardBinding
import kztproject.jp.splacounter.model.Reward
import kztproject.jp.splacounter.viewmodel.RewardViewModel
import javax.inject.Inject

class RewardFragment : Fragment(), RewardViewModel.Callback {
    @Inject
    lateinit var viewModel: RewardViewModel


    lateinit var binding: FragmentRewardBinding

    companion object {

        const val ARG_POINT = "point"
        fun newInstance(point: Int): RewardFragment {
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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRewards()
    }

    override fun showRewardAdd() {
        val fragment = RewardAddFragment.newInstance()
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.javaClass.canonicalName)
                .commit()
    }

    override fun showRewards(rewardList: List<Reward>) {
        binding.rewardListView.adapter = RewardListAdapter(rewardList)
    }
}

class RewardListAdapter(private val rewardList: List<Reward>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rewardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reward = rewardList[position]
        holder.getBinding().setVariable(BR.reward, reward)
        holder.getBinding().executePendingBindings()
    }

}

class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    private var binding: ItemRewardBinding = DataBindingUtil.bind(itemView)

    fun getBinding(): ItemRewardBinding {
        return binding
    }
}
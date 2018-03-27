package kztproject.jp.splacounter.view.fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.database.model.Reward
import kztproject.jp.splacounter.databinding.FragmentRewardBinding
import kztproject.jp.splacounter.databinding.ItemRewardBinding
import kztproject.jp.splacounter.viewmodel.RewardViewModel
import kztproject.jp.splacounter.viewmodel.RewardViewModelCallback
import javax.inject.Inject

class RewardFragment : Fragment(), RewardViewModelCallback, ClickListener {

    @Inject
    lateinit var viewModel: RewardViewModel

    lateinit var binding: FragmentRewardBinding

    companion object {

        private const val ARG_POINT = "point"

        fun newInstance(point: Int): RewardFragment {
            val args = Bundle()
            args.putInt(ARG_POINT, point)

            val fragment = RewardFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel.setCallback(this)
        viewModel.setPoint(arguments.getInt(ARG_POINT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRewards()

        binding.bottomNavigation.addItem(AHBottomNavigationItem("Done", R.drawable.reward_done))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Edit", R.drawable.reward_edit))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Delete", R.drawable.reward_delete))
        binding.bottomNavigation.setOnTabSelectedListener({ position, wasSelected ->
            System.out.println("$position::$wasSelected")
            when (position) {
                0 -> {
                    viewModel.acquireReward()
                }
                1 -> {
                    viewModel.editReward()
                }
                2 -> {
                    viewModel.confirmDelete()
                }
            }
            true
        })

    }

    override fun onItemClick(reward: Reward) {
        viewModel.switchReward(reward)
    }

    override fun showRewardDetail() {
        replace(R.id.container, RewardDetailFragment.newInstance())
    }

    override fun showRewards(rewardList: MutableList<Reward>) {
        binding.rewardListView.adapter = RewardListAdapter(rewardList, this)
    }

    override fun showDeleteConfirmDialog(reward: Reward) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.confirm_title)
                .setMessage(String.format(getString(R.string.delete_confirm_message), reward.name))
                .setPositiveButton(android.R.string.ok, { _, _ -> viewModel.deleteReward(reward, true)})
                .setNegativeButton(android.R.string.cancel, { _, _ -> run {} })
                .show()
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_acquire_reward, Toast.LENGTH_SHORT).show()
    }

    override fun successAcquireReward(reward: Reward, point: Int) {
        Toast.makeText(context, "You consume $point points", Toast.LENGTH_SHORT).show()

        if (!reward.needRepeat) {
            viewModel.deleteRewardIfNeeded(reward)
            (binding.rewardListView.adapter as RewardListAdapter).remove(reward)
        }
    }

    override fun onRewardSelected(position: Int) {
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.rewardAddButton.visibility = View.INVISIBLE
        binding.rewardListView.adapter.notifyItemChanged(position)
    }

    override fun onRewardDeSelected(position: Int) {
        binding.bottomNavigation.visibility = View.INVISIBLE
        binding.rewardAddButton.visibility = View.VISIBLE
        binding.rewardListView.adapter.notifyItemChanged(position)
    }

    override fun onRewardEditSelected(reward: Reward) {
        replace(R.id.container, RewardDetailFragment.newInstance(reward.id))
    }

    override fun onRewardDeleted(reward: Reward) {
        val message = String.format(getString(R.string.reward_delete_message), reward.name)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        (binding.rewardListView.adapter as RewardListAdapter).remove(reward)
    }
}

class RewardListAdapter(private val rewardList: MutableList<Reward>, private val clickListener: ClickListener)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rewardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reward = rewardList[position]
        holder.getBinding().setVariable(BR.reward, reward)
        holder.getBinding().executePendingBindings()
        holder.itemView.setOnClickListener({ clickListener.onItemClick(reward) })
    }

    fun remove(deleteReward: Reward) {
        rewardList.forEachIndexed { index, reward ->
            if (deleteReward == reward) {
                rewardList.remove(reward)
                notifyItemRemoved(index)
                return
            }
        }
    }
}

class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    private var binding: ItemRewardBinding = DataBindingUtil.bind(itemView)

    fun getBinding(): ItemRewardBinding {
        return binding
    }
}

interface ClickListener {
    fun onItemClick(reward: Reward)
}

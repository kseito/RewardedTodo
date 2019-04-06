package kztproject.jp.splacounter.reward.list.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
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
import kztproject.jp.splacounter.reward.database.model.Reward
import project.seito.reward.R
import project.seito.reward.databinding.FragmentRewardBinding
import project.seito.reward.databinding.ItemRewardBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class RewardListFragment : Fragment(), RewardViewModelCallback, ClickListener {
    @Inject
    lateinit var viewModel: RewardListViewModel

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private lateinit var binding: FragmentRewardBinding

    private var animation: Animator? = null

    companion object {
        fun newInstance(): RewardListFragment {
            return RewardListFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRewards()
        viewModel.loadPoint()

        binding.bottomNavigation.addItem(AHBottomNavigationItem("Done", R.drawable.reward_done))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Edit", R.drawable.reward_edit))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Delete", R.drawable.reward_delete))
        binding.bottomNavigation.setOnTabSelectedListener { position, wasSelected ->
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
        }

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> viewModel.logout()
            }
            false
        }
    }

    override fun onItemClick(reward: Reward) {
        viewModel.switchReward(reward)
    }

    override fun showRewardDetail() {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity)
    }

    override fun showRewards(rewardList: MutableList<Reward>) {
        binding.rewardListView.adapter = RewardListAdapter(rewardList, this)
    }

    override fun showDeleteConfirmDialog(reward: Reward) {
        val activityContext = activity as Context? ?: return
        AlertDialog.Builder(activityContext)
                .setTitle(R.string.confirm_title)
                .setMessage(String.format(getString(R.string.delete_confirm_message), reward.name))
                .setPositiveButton(android.R.string.ok, { _, _ -> viewModel.deleteReward(reward, true) })
                .setNegativeButton(android.R.string.cancel, { _, _ -> run {} })
                .show()
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_acquire_reward, Toast.LENGTH_SHORT).show()
    }

    override fun successAcquireReward(reward: Reward, point: Int) {
        if (!reward.needRepeat) {
            viewModel.deleteRewardIfNeeded(reward)
            (binding.rewardListView.adapter as RewardListAdapter).remove(reward)
        }
    }

    override fun onRewardSelected(position: Int) {
        binding.rewardListView.adapter?.notifyItemChanged(position)
    }

    override fun onRewardDeSelected(position: Int) {
        binding.rewardListView.adapter?.notifyItemChanged(position)
    }

    override fun onRewardEditSelected(reward: Reward) {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity, reward.id)
    }

    override fun onRewardDeleted(reward: Reward) {
        val message = String.format(getString(R.string.reward_delete_message), reward.name)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        (binding.rewardListView.adapter as RewardListAdapter).remove(reward)
    }

    override fun onPointLoadFailed() {
        Toast.makeText(context, "Point load failed", Toast.LENGTH_SHORT).show()
    }

    override fun onStartLoadingPoint() {
        animation = AnimatorInflater.loadAnimator(context, R.animator.rotate_animation).apply {
            setTarget(binding.syncButton)
            start()
        }
    }

    override fun onTerminateLoadingPoint() {
        animation?.cancel()
    }

    override fun onLogout() {
        fragmentTransitionManager.transitionToAuthFragment(activity)
    }
}

class RewardListAdapter(private val rewardList: MutableList<Reward>, private val clickListener: ClickListener)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rewardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reward = rewardList[position]
        holder.getBinding().setVariable(BR.reward, reward)
        holder.getBinding().executePendingBindings()
        holder.itemView.setOnClickListener { clickListener.onItemClick(reward) }
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

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var binding: ItemRewardBinding = ItemRewardBinding.bind(itemView)

    fun getBinding(): ItemRewardBinding {
        return binding
    }
}

interface ClickListener {
    fun onItemClick(reward: Reward)
}

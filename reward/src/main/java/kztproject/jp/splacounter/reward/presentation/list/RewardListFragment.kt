package kztproject.jp.splacounter.reward.presentation.list

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import project.seito.reward.BR
import project.seito.reward.R
import project.seito.reward.databinding.FragmentRewardBinding
import project.seito.reward.databinding.ItemRewardBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class RewardListFragment : Fragment(), RewardViewModelCallback, ClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: RewardListViewModel

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private lateinit var binding: FragmentRewardBinding

    private var animation: Animator? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardListViewModel::class.java)
        viewModel.setCallback(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.loadRewards()
            viewModel.loadPoint()
        }

        binding.bottomNavigation.addItem(AHBottomNavigationItem("Done", R.drawable.reward_done))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Edit", R.drawable.reward_edit))
        binding.bottomNavigation.addItem(AHBottomNavigationItem("Delete", R.drawable.reward_delete))
        binding.bottomNavigation.setOnTabSelectedListener { position, wasSelected ->
            println("$position::$wasSelected")
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

        binding.rewardListView.adapter = RewardListAdapter(viewModel.rewardEntityList, this)
    }

    override fun onItemClick(rewardEntity: RewardEntity) {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity, rewardEntity.id)
    }

    override fun showRewardDetail() {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity)
    }

    override fun showRewards(rewardEntityList: MutableList<RewardEntity>) {
        binding.rewardListView.adapter = RewardListAdapter(rewardEntityList, this)
    }

    override fun showDeleteConfirmDialog(rewardEntity: RewardEntity) {
        val activityContext = activity as Context? ?: return
        AlertDialog.Builder(activityContext)
                .setTitle(R.string.confirm_title)
                .setMessage(String.format(getString(R.string.delete_confirm_message), rewardEntity.name))
                .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteReward(rewardEntity, true) }
                .setNegativeButton(android.R.string.cancel) { _, _ -> run {} }
                .show()
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_acquire_reward, Toast.LENGTH_SHORT).show()
    }

    override fun successAcquireReward(rewardEntity: RewardEntity, point: Int) {
        if (!rewardEntity.needRepeat) {
            viewModel.deleteRewardIfNeeded(rewardEntity)
            (binding.rewardListView.adapter as RewardListAdapter).remove(rewardEntity)
        }
    }

    override fun onRewardSelected(position: Int) {
        binding.rewardListView.adapter?.notifyItemChanged(position)
    }

    override fun onRewardDeSelected(position: Int) {
        binding.rewardListView.adapter?.notifyItemChanged(position)
    }

    override fun onRewardEditSelected(rewardEntity: RewardEntity) {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity, rewardEntity.id)
    }

    override fun onRewardDeleted(rewardEntity: RewardEntity) {
        val message = String.format(getString(R.string.reward_delete_message), rewardEntity.name)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        (binding.rewardListView.adapter as RewardListAdapter).remove(rewardEntity)
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

    override fun onHitLottery(rewardEntity: RewardEntity) {
        val message = "You won ${rewardEntity.name}!"
        showDialog(message)
    }

    override fun onMissLottery() {
        val message = "You missed the lottery"
        showDialog(message)
    }
}

class RewardListAdapter(private val rewardEntityList: MutableList<RewardEntity>, private val clickListener: ClickListener)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rewardEntityList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reward = rewardEntityList[position]
        holder.getBinding().setVariable(BR.reward, reward)
        holder.getBinding().executePendingBindings()
        holder.itemView.setOnClickListener { clickListener.onItemClick(reward) }
    }

    fun remove(deleteRewardEntity: RewardEntity) {
        rewardEntityList.forEachIndexed { index, reward ->
            if (deleteRewardEntity == reward) {
                rewardEntityList.remove(reward)
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
    fun onItemClick(rewardEntity: RewardEntity)
}

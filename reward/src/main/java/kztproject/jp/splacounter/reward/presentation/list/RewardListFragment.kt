package kztproject.jp.splacounter.reward.presentation.list

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.reward.domain.model.Reward
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

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> viewModel.logout()
            }
            false
        }

        binding.rewardListView.adapter = RewardListAdapter(viewModel.rewardList, this)
    }

    override fun onItemClick(rewardEntity: Reward) {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity, rewardEntity.rewardId.value)
    }

    override fun showRewardDetail() {
        fragmentTransitionManager.transitionToRewardDetailFragment(activity)
    }

    override fun showRewards(rewardList: MutableList<Reward>) {
        binding.rewardListView.adapter = RewardListAdapter(rewardList, this)
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_acquire_reward, Toast.LENGTH_SHORT).show()
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

    override fun onHitLottery(reward: Reward) {
        val message = "You won ${reward.name}!"
        showDialog(message)
    }

    override fun onMissLottery() {
        val message = "You missed the lottery"
        showDialog(message)
    }
}

class RewardListAdapter(private val rewardEntityList: MutableList<Reward>, private val clickListener: ClickListener)
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

    fun remove(deleteRewardEntity: Reward) {
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
    fun onItemClick(rewardEntity: Reward)
}

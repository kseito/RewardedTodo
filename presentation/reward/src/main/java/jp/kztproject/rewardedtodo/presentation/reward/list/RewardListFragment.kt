package jp.kztproject.rewardedtodo.presentation.reward.list

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.presentation.reward.R
import jp.kztproject.rewardedtodo.presentation.reward.databinding.FragmentRewardBinding
import jp.kztproject.rewardedtodo.presentation.reward.databinding.ItemRewardBinding
import jp.kztproject.rewardedtodo.presentation.reward.helper.showDialog
import project.seito.reward.BR
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@AndroidEntryPoint
class RewardListFragment : Fragment(), RewardViewModelCallback, ClickListener {

    private val viewModel: RewardListViewModel by viewModels()

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private lateinit var binding: FragmentRewardBinding

    private var animation: Animator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCallback(this)
        if (savedInstanceState == null) {
            viewModel.loadRewards()
            viewModel.loadPoint()
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

    override fun onHitLottery(reward: Reward) {
        val message = "You won ${reward.name.value}!"
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

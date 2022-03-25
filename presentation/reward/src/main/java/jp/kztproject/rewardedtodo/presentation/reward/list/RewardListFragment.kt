package jp.kztproject.rewardedtodo.presentation.reward.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.application.reward.model.Result
import jp.kztproject.rewardedtodo.application.reward.model.Success
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.presentation.reward.helper.showDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@ExperimentalMaterialApi
@AndroidEntryPoint
class RewardListFragment : Fragment(), RewardViewModelCallback, ClickListener {

    private val viewModel: RewardListViewModel by viewModels()

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colors = RewardedTodoScheme(isSystemInDarkTheme())
                ) {
                    RewardListScreenWithBottomSheet(viewModel)
                }
            }
        }
    }

    @Composable
    private fun RewardListScreenWithBottomSheet(viewModel: RewardListViewModel) {
        val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val onRewardSaveSelected: (String, String, String, Boolean) -> Unit =
            { title, description, chanceOfWinning, repeat ->
                // TODO use factory method
                val reward = RewardInput(
                    name = title,
                    description = description,
                    consumePoint = 1,
                    probability = chanceOfWinning.toFloat(),
                    needRepeat = repeat
                )
                viewModel.saveReward(reward)
            }

        RewardDetailBottomSheet(
            bottomSheetState = bottomSheetState,
            onRewardSaveSelected = onRewardSaveSelected,
        ) {
            RewardListScreen(
                viewModel = viewModel,
                bottomSheetState = bottomSheetState,
            )
        }
    }

    @Composable
    private fun RewardListScreen(
        viewModel: RewardListViewModel,
        bottomSheetState: ModalBottomSheetState
    ) {
        val ticket by viewModel.rewardPoint.observeAsState()
        val rewards by viewModel.rewardList.observeAsState()
        val result by viewModel.result.observeAsState()
        val coroutineScope = rememberCoroutineScope()

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Column {
                Header(ticket)
                RewardList(rewards)
            }

            val (createRewardButton, lotteryRewardButton) = createRefs()

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .constrainAs(createRewardButton) {
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                    }
                    .padding(24.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }

            FloatingActionButton(
                onClick = {
                    viewModel.startLottery()
                },
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .constrainAs(lotteryRewardButton) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(24.dp)
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Done")
            }
        }

        result?.let {
            LaunchedEffect(it) {
                when {
                    it.isSuccess -> {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                    else -> {
                        // TODO show error
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    private fun RewardListScreenPreview() {
        val viewModel = RewardListViewModel(object : LotteryUseCase {
            override suspend fun execute(rewards: RewardCollection): Reward? = null
        }, object : GetRewardsUseCase {
            private val reward = Reward(
                RewardId(1),
                RewardName("PS5"),
                10,
                Probability(0.5f),
                RewardDescription(""),
                false
            )

            override suspend fun execute(): List<Reward> {
                return listOf(reward)
            }

            override suspend fun executeAsFlow(): Flow<List<Reward>> {
                return flowOf(listOf(reward))
            }
        }, object : GetPointUseCase {
            override suspend fun execute(): NumberOfTicket {
                return NumberOfTicket(123)
            }
        }, object : SaveRewardUseCase {
            override suspend fun execute(reward: RewardInput): Result<Unit> {
                return Success(Unit)
            }
        })

        RewardListScreen(
            viewModel = viewModel,
            bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        )
    }

    @Composable
    private fun Header(ticket: Int?) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "$ticket tickets",
                fontSize = 18.sp,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }

    @Preview
    @Composable
    private fun HeaderPreview() {
        Header(1)
    }

    @Composable
    private fun RewardList(rewards: List<Reward>?) {
        LazyColumn {
            rewards?.let {
                itemsIndexed(it) { index, reward ->
                    RewardItem(reward)
                    if (index < rewards.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    private fun RewardListPreview() {
        RewardList(
            rewards = listOf(
                Reward(
                    RewardId(1),
                    RewardName("PS5"),
                    1,
                    Probability(1f),
                    RewardDescription("this is very rare"),
                    true
                ),
                Reward(
                    RewardId(1),
                    RewardName("New Macbook Pro"),
                    1,
                    Probability(1f),
                    RewardDescription("M1 Max is great"),
                    true
                )
            )
        )
    }

    @Composable
    private fun RewardItem(reward: Reward) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(reward) }
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(8f)
                ) {
                    Text(
                        text = reward.name.value,
                        style = MaterialTheme.typography.h4
                    )
                    Text(
                        text = reward.description.value ?: "",
                        color = Color.Gray
                    )
                }
                Text(
                    text = "${reward.probability.value} %",
                    modifier = Modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.h5
                )
            }
        }
    }

    @Preview
    @Composable
    private fun RewardItemPreview() {
        val reward = Reward(
            RewardId(1),
            RewardName("PS5"),
            1,
            Probability(1f),
            RewardDescription("this is very rare"),
            true
        )
        RewardItem(reward)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCallback(this)
        if (savedInstanceState == null) {
            viewModel.loadRewards()
            viewModel.loadPoint()
        }
    }

    override fun onItemClick(rewardEntity: Reward) {
        fragmentTransitionManager.transitionToRewardDetailFragment(
            activity,
            rewardEntity.rewardId.value
        )
    }

    override fun onPointLoadFailed() {
        Toast.makeText(context, "Point load failed", Toast.LENGTH_SHORT).show()
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

interface ClickListener {
    fun onItemClick(rewardEntity: Reward)
}

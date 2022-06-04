package jp.kztproject.rewardedtodo.presentation.reward.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.application.reward.usecase.*
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.presentation.reward.detail.ErrorMessageClassifier
import jp.kztproject.rewardedtodo.presentation.reward.helper.showDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@ExperimentalMaterialApi
@AndroidEntryPoint
class RewardListFragment : Fragment(), RewardViewModelCallback {

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
        val coroutineScope = rememberCoroutineScope()
        var selectedReward: Reward? by remember { mutableStateOf(null) }
        val onRewardItemClick: (Reward) -> Unit = { reward ->
            coroutineScope.launch {
                selectedReward = reward
                bottomSheetState.show()
            }
        }
        val onRewardSaveSelected: (Int?, String?, String?, String?, Boolean) -> Unit =
            { id, title, description, chanceOfWinning, repeat ->
                // TODO use factory method
                val reward = RewardInput(
                    id = id,
                    name = title,
                    description = description,
                    probability = if (chanceOfWinning.isNullOrEmpty()) null else chanceOfWinning.toFloat(),
                    needRepeat = repeat
                )
                viewModel.saveReward(reward)
            }
        val onRewardDeleteSelected: (Reward) -> Unit = { reward ->
            viewModel.deleteReward(reward)
        }

        RewardDetailBottomSheet(
            bottomSheetState = bottomSheetState,
            onRewardSaveSelected = onRewardSaveSelected,
            onRewardDeleteSelected = onRewardDeleteSelected,
            reward = selectedReward
        ) {
            RewardListScreen(
                viewModel = viewModel,
                bottomSheetState = bottomSheetState,
                onRewardItemClick = onRewardItemClick
            )
        }
    }

    @Composable
    private fun RewardListScreen(
        viewModel: RewardListViewModel,
        bottomSheetState: ModalBottomSheetState,
        onRewardItemClick: (Reward) -> Unit
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
                RewardList(rewards, onRewardItemClick)
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

        val context = LocalContext.current
        LaunchedEffect(result) {
            result?.let {
                it.fold(
                    onSuccess = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }, onFailure = { error ->
                        val errorMessageId = ErrorMessageClassifier(error).messageId
                        Toast.makeText(context, errorMessageId, Toast.LENGTH_LONG).show()
                    })
                viewModel.result.value = null
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
                return Result.success(Unit)
            }
        }, object : DeleteRewardUseCase {
            override suspend fun execute(reward: Reward) {}
        })

        RewardListScreen(
            viewModel = viewModel,
            bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
            onRewardItemClick = {}
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
    private fun RewardList(
        rewards: List<Reward>?,
        onRewardItemClick: (Reward) -> Unit
    ) {
        LazyColumn {
            rewards?.let {
                itemsIndexed(it) { index, reward ->
                    RewardItem(reward, onRewardItemClick)
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
                    Probability(1f),
                    RewardDescription("this is very rare"),
                    true
                ),
                Reward(
                    RewardId(1),
                    RewardName("New Macbook Pro"),
                    Probability(1f),
                    RewardDescription("M1 Max is great"),
                    true
                )
            ),
            onRewardItemClick = {}
        )
    }

    @Composable
    private fun RewardItem(
        reward: Reward,
        onRewardItemClick: (Reward) -> Unit
    ) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onRewardItemClick(reward) })
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
            Probability(1f),
            RewardDescription("this is very rare"),
            true
        )
        RewardItem(
            reward = reward,
            onRewardItemClick = {}
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCallback(this)
        if (savedInstanceState == null) {
            viewModel.loadRewards()
            viewModel.loadPoint()
        }
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

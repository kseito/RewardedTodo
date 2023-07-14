package jp.kztproject.rewardedtodo.feature.reward.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.feature.reward.detail.ErrorMessageClassifier
import jp.kztproject.rewardedtodo.presentation.common.CommonAlertDialog
import jp.kztproject.rewardedtodo.presentation.reward.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun RewardListScreenWithBottomSheet(
    viewModel: RewardListViewModel = hiltViewModel(),
) {
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
@ExperimentalMaterialApi
private fun RewardListScreen(
    viewModel: RewardListViewModel,
    bottomSheetState: ModalBottomSheetState,
    onRewardItemClick: (Reward) -> Unit
) {
    val ticket by viewModel.rewardPoint.observeAsState()
    val rewards by viewModel.rewardList.observeAsState()
    val result by viewModel.result.observeAsState()
    // TODO can use collectAsStateWithLifecycle() if library update
    // https://qiita.com/dosukoi_android/items/e8bbaa662c52b8e1cc20
    val obtainedReward by viewModel.obtainedReward.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            Header(ticket)

            Box {
                RewardList(rewards, onRewardItemClick)
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = {
                        ErrorSnackBar(it)
                    }
                )
            }
        }

        val (createRewardButton, lotteryRewardButton) = createRefs()

        FloatingActionButton(
            onClick = {
                viewModel.startLottery()
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
            Icon(Icons.Filled.Done, contentDescription = "Done")
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            },
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .constrainAs(lotteryRewardButton) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .padding(24.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
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
    obtainedReward?.let { it ->
        if (it.isSuccess) {
            val reward = it.getOrNull()
            if (reward == null) {
                CommonAlertDialog(
                    message = stringResource(id = R.string.missed_reward),
                    onOkClicked = {
                        viewModel.resetObtainedReward()
                    }
                )
            } else {
                CommonAlertDialog(
                    message = stringResource(R.string.won_reward, reward.name.value),
                    onOkClicked = {
                        viewModel.resetObtainedReward()
                    }
                )
            }
        } else if (it.isFailure) {
            it.exceptionOrNull()?.let { error ->
                val errorMessageId = ErrorMessageClassifier(error).messageId
                val errorMessage = stringResource(id = errorMessageId)
                LaunchedEffect(error) {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        actionLabel = null,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorSnackBar(it: SnackbarData) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = it.message,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun ErrorSnackBarPreview() {
    ErrorSnackBar(it = object : SnackbarData {
        override val actionLabel: String?
            get() = null
        override val duration: SnackbarDuration
            get() = SnackbarDuration.Indefinite
        override val message: String
            get() = "Cannot connect Internet."

        override fun dismiss() {}

        override fun performAction() {}
    })
}

@Preview
@Composable
@ExperimentalMaterialApi
private fun RewardListScreenPreview() {
    val viewModel = RewardListViewModel(object : LotteryUseCase {
        override suspend fun execute(rewards: RewardCollection): Result<Reward?> =
            Result.success(null)
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
        override suspend fun execute(): Flow<NumberOfTicket> {
            return flowOf(NumberOfTicket(100))
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
        onRewardItemClick = {},
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

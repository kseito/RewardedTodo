package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.common.ui.CommonAlertDialog
import jp.kztproject.rewardedtodo.common.ui.vibrate
import jp.kztproject.rewardedtodo.common.ui.vibrateRichly
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.feature.reward.detail.ErrorMessageClassifier
import jp.kztproject.rewardedtodo.presentation.reward.R
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardListScreenWithBottomSheet(viewModel: RewardListViewModel = hiltViewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var selectedReward: Reward? by remember { mutableStateOf(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val onRewardItemClick: (Reward) -> Unit = { reward ->
        selectedReward = reward
        showBottomSheet = true
    }
    val onAddRewardItemClick: () -> Unit = {
        selectedReward = null
        showBottomSheet = true
    }
    val onRewardSaveSelected: (Int?, String?, String?, String?, Boolean) -> Unit =
        { id, title, description, chanceOfWinning, repeat ->
            // TODO use factory method
            val reward = RewardInput(
                id = id,
                name = title,
                description = description,
                probability = chanceOfWinning?.toFloatOrNull(),
                needRepeat = repeat,
            )
            viewModel.saveReward(reward)
        }
    val onRewardDeleteSelected: (Reward) -> Unit = { reward ->
        viewModel.deleteReward(reward)
    }

    Box {
        RewardListScreen(
            viewModel = viewModel,
            onAddNewRewardClick = onAddRewardItemClick,
            onRewardItemClick = onRewardItemClick,
            onRewardSaveSucceeded = { showBottomSheet = false },
        )

        RewardDetailBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            onRewardSaveSelected = onRewardSaveSelected,
            onRewardDeleteSelected = onRewardDeleteSelected,
            reward = selectedReward,
        )
    }
}

@Composable
private fun RewardListScreen(
    viewModel: RewardListViewModel,
    onAddNewRewardClick: () -> Unit,
    onRewardItemClick: (Reward) -> Unit,
    onRewardSaveSucceeded: () -> Unit,
) {
    val ticket by viewModel.rewardPoint.collectAsStateWithLifecycle()
    val rewards by viewModel.rewardList.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val obtainedReward by viewModel.obtainedReward.collectAsStateWithLifecycle()
    val batchLotteryResult by viewModel.batchLotteryResult.collectAsStateWithLifecycle()
    val isSingleLottering by viewModel.isSingleLottering.collectAsStateWithLifecycle()
    val isBatchLottering by viewModel.isBatchLottering.collectAsStateWithLifecycle()
    val isLottering = isSingleLottering || isBatchLottering
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                RewardList(rewards, onRewardItemClick)
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = {
                        ErrorSnackBar(it)
                    },
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TicketLabel(
                ticket = ticket,
                modifier = Modifier
                    .padding(bottom = 8.dp),
            )

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isLottering) viewModel.startLottery()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.semantics { contentDescription = "single_lottery_button" },
                ) {
                    if (isSingleLottering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.Filled.Done, contentDescription = "Done")
                    }
                }

                FloatingActionButton(
                    onClick = {
                        if (!isLottering) viewModel.startBatchLottery()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.semantics { contentDescription = "batch_lottery_button" },
                ) {
                    if (isBatchLottering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(stringResource(id = R.string.batch_lottery_button, BatchLotteryResult.DEFAULT_COUNT))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                viewModel.validateRewards {
                    coroutineScope.launch {
                        onAddNewRewardClick()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }

    result?.let { res ->
        if (res.isSuccess) {
            LaunchedEffect(res) {
                onRewardSaveSucceeded()
                viewModel.clearResult()
            }
        } else if (res.isFailure) {
            res.exceptionOrNull()?.let { error ->
                val errorMessageId = ErrorMessageClassifier(error).messageId
                val errorMessage = stringResource(id = errorMessageId)
                LaunchedEffect(res) {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        actionLabel = null,
                        duration = SnackbarDuration.Short,
                    )
                    viewModel.clearResult()
                }
            }
        }
    }
    obtainedReward?.let { it ->
        if (it.isSuccess) {
            val reward = it.getOrNull()
            if (reward == null) {
                LaunchedEffect(it) {
                    context.vibrate()
                }

                CommonAlertDialog(
                    message = stringResource(id = R.string.missed_reward),
                    onOkClicked = {
                        viewModel.resetObtainedReward()
                    },
                )
            } else {
                LaunchedEffect(it) {
                    context.vibrateRichly()
                }

                CommonAlertDialog(
                    message = stringResource(R.string.won_reward, reward.name.value),
                    onOkClicked = {
                        viewModel.resetObtainedReward()
                    },
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
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }
    batchLotteryResult?.let {
        if (it.isSuccess) {
            val result = it.getOrNull()
            result?.let { batchResult ->
                LaunchedEffect(it) {
                    if (batchResult.hasWon) {
                        context.vibrateRichly()
                    } else {
                        context.vibrate()
                    }
                }

                BatchLotteryResultDialog(
                    result = batchResult,
                    onDismiss = {
                        viewModel.resetBatchLotteryResult()
                    },
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
                        duration = SnackbarDuration.Short,
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
            .fillMaxWidth(),
    ) {
        Text(
            text = it.visuals.message,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Preview
@Composable
fun ErrorSnackBarPreview() {
    ErrorSnackBar(
        object : SnackbarData {
            override val visuals = object : SnackbarVisuals {
                override val message = "Input title"
                override val actionLabel: String? = null
                override val withDismissAction = false
                override val duration = SnackbarDuration.Short
            }

            override fun dismiss() {}

            override fun performAction() {}
        },
    )
}

// Create preview ViewModel outside the composable function
private val previewViewModel = RewardListViewModel(
    object : LotteryUseCase {
        override suspend fun execute(rewards: RewardCollection): Result<Reward?> = Result.success(null)
    },
    object : BatchLotteryUseCase {
        override suspend fun execute(
            rewards: RewardCollection,
            count: Int,
        ): Result<jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult> =
            Result.success(jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult(emptyList(), count))
    },
    object : GetRewardsUseCase {
        private val reward = Reward(
            RewardId(1),
            RewardName("PS5"),
            Probability(0.5f),
            RewardDescription(""),
            false,
        )

        override suspend fun execute(): List<Reward> = listOf(reward)

        override suspend fun executeAsFlow(): Flow<List<Reward>> = flowOf(listOf(reward))
    },
    object : GetPointUseCase {
        override suspend fun execute(): Flow<NumberOfTicket> = flowOf(NumberOfTicket(100))
    },
    object : SaveRewardUseCase {
        override suspend fun execute(reward: RewardInput): Result<Unit> = Result.success(Unit)
    },
    object : DeleteRewardUseCase {
        override suspend fun execute(reward: Reward) {}
    },
)

@Preview
@Composable
fun RewardListScreenPreview() {
    RewardListScreen(
        viewModel = previewViewModel,
        onAddNewRewardClick = {},
        onRewardItemClick = {},
        onRewardSaveSucceeded = {},
    )
}

private fun createLotteringPreviewViewModel(): RewardListViewModel = RewardListViewModel(
    object : LotteryUseCase {
        override suspend fun execute(rewards: RewardCollection): Result<Reward?> = awaitCancellation()
    },
    object : BatchLotteryUseCase {
        override suspend fun execute(rewards: RewardCollection, count: Int): Result<BatchLotteryResult> =
            awaitCancellation()
    },
    object : GetRewardsUseCase {
        private val reward = Reward(
            RewardId(1),
            RewardName("PS5"),
            Probability(0.5f),
            RewardDescription(""),
            false,
        )

        override suspend fun execute(): List<Reward> = listOf(reward)

        override suspend fun executeAsFlow(): Flow<List<Reward>> = flowOf(listOf(reward))
    },
    object : GetPointUseCase {
        override suspend fun execute(): Flow<NumberOfTicket> = flowOf(NumberOfTicket(100))
    },
    object : SaveRewardUseCase {
        override suspend fun execute(reward: RewardInput): Result<Unit> = Result.success(Unit)
    },
    object : DeleteRewardUseCase {
        override suspend fun execute(reward: Reward) {}
    },
)

@Preview
@Composable
fun RewardListScreenSingleLotteringPreview() {
    val viewModel = remember { createLotteringPreviewViewModel().apply { startLottery() } }
    RewardListScreen(
        viewModel = viewModel,
        onAddNewRewardClick = {},
        onRewardItemClick = {},
        onRewardSaveSucceeded = {},
    )
}

@Preview
@Composable
fun RewardListScreenBatchLotteringPreview() {
    val viewModel = remember { createLotteringPreviewViewModel().apply { startBatchLottery() } }
    RewardListScreen(
        viewModel = viewModel,
        onAddNewRewardClick = {},
        onRewardItemClick = {},
        onRewardSaveSucceeded = {},
    )
}

@Composable
private fun TicketLabel(ticket: Int?, modifier: Modifier = Modifier) {
    Text(
        text = "$ticket tickets",
        modifier = modifier.semantics { contentDescription = "ticket_count" },
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun RewardList(rewards: List<Reward>?, onRewardItemClick: (Reward) -> Unit) {
    LazyColumn {
        rewards?.let {
            itemsIndexed(it) { index, reward ->
                RewardItem(reward, onRewardItemClick)
                if (index < rewards.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardListPreview() {
    RewardList(
        rewards = listOf(
            Reward(
                RewardId(1),
                RewardName("PS5"),
                Probability(1f),
                RewardDescription("this is very rare"),
                true,
            ),
            Reward(
                RewardId(1),
                RewardName("New Macbook Pro"),
                Probability(1f),
                RewardDescription("M1 Max is great"),
                true,
            ),
        ),
        onRewardItemClick = {},
    )
}

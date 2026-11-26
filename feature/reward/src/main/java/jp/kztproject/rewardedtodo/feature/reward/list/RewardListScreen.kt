package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextButton
import jp.kztproject.rewardedtodo.application.reward.usecase.RecoverLotteryUseCase
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.domain.reward.LotteryOutcome
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.ObserveLotteryAnimationEnabledUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
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
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                version = selectedReward?.version,
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
            onDismissRequest = { if (!isSaving) showBottomSheet = false },
            isSaving = isSaving,
            sheetState = sheetState,
            onRewardSaveSelected = onRewardSaveSelected,
            onRewardDeleteSelected = onRewardDeleteSelected,
            reward = selectedReward,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val hasPending by viewModel.hasPendingLottery.collectAsStateWithLifecycle()
    val isRecovering by viewModel.isRecovering.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isLotteryAnimationEnabled by viewModel.isLotteryAnimationEnabled.collectAsStateWithLifecycle()

    // 他のタブでチケットを獲得していても反映されるよう、表示のたびに引き直す
    LaunchedEffect(Unit) {
        viewModel.refreshPointSilently()
    }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column {
                if (hasPending || isRecovering) {
                    Text(stringResource(R.string.reward_pending), modifier = Modifier.padding(horizontal = 16.dp))
                    TextButton(onClick = viewModel::recoverLottery, enabled = !isRecovering) {
                        Text(stringResource(R.string.reward_retry_lottery))
                    }
                }
                RewardListContent(
                    rewards = rewards,
                    ticket = ticket,
                    isSingleLottering = isSingleLottering || hasPending || isRecovering,
                    isBatchLottering = isBatchLottering,
                    onSingleLottery = viewModel::startLottery,
                    onBatchLottery = { viewModel.startBatchLottery() },
                    onRewardItemClick = onRewardItemClick,
                    onAddNewRewardClick = { viewModel.validateRewards(onAddNewRewardClick) },
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { ErrorSnackBar(it) },
        )
    }

    val resultErrorMessage = result?.exceptionOrNull()
        ?.let { stringResource(id = ErrorMessageClassifier(it).messageId) }
    LaunchedEffect(result) {
        result?.let { res ->
            res.fold(
                onSuccess = {
                    onRewardSaveSucceeded()
                },
                onFailure = {
                    resultErrorMessage?.let { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = null,
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                },
            )
            viewModel.clearResult()
        }
    }
    // A completed draw can be dismissed while the ViewModel is still refreshing its lists.
    // Do not reopen the waiting dialog during that refresh (including after rotation).
    var lotteryDismissed by rememberSaveable { mutableStateOf(false) }
    val drawing = isSingleLottering || isBatchLottering
    LaunchedEffect(drawing) {
        if (!drawing) lotteryDismissed = false
    }
    val singleSuccess = obtainedReward?.isSuccess == true
    val batchSuccess = batchLotteryResult?.isSuccess == true
    val waitingForDraw = drawing && obtainedReward?.isFailure != true && batchLotteryResult?.isFailure != true
    if (!lotteryDismissed && (waitingForDraw || singleSuccess || batchSuccess)) {
        val batch = isBatchLottering || batchSuccess
        val singleReward = obtainedReward?.getOrNull()
        val batchResult = batchLotteryResult?.getOrNull()
        LotteryAnimationDialog(
            ready = singleSuccess || batchSuccess,
            rewardNames = if (batch) {
                batchResult?.wonRewards.orEmpty().map { it.name.value }
            } else {
                listOfNotNull(singleReward?.name?.value)
            },
            missCount = if (batch) {
                batchResult?.missCount ?: 0
            } else if (singleReward == null) {
                1
            } else {
                0
            },
            isBatch = batch,
            onDismiss = {
                lotteryDismissed = drawing
                viewModel.resetObtainedReward()
                viewModel.resetBatchLotteryResult()
            },
            playAnimation = isLotteryAnimationEnabled,
        )
    }
    val lotteryError = obtainedReward?.exceptionOrNull() ?: batchLotteryResult?.exceptionOrNull()
    val lotteryErrorMessage = lotteryError?.let { stringResource(ErrorMessageClassifier(it).messageId) }
    LaunchedEffect(lotteryError, isSingleLottering, isBatchLottering) {
        if (lotteryErrorMessage != null && !isSingleLottering && !isBatchLottering) {
            viewModel.resetObtainedReward()
            viewModel.resetBatchLotteryResult()
            snackbarHostState.showSnackbar(lotteryErrorMessage)
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
        override suspend fun execute(): Result<AcquiredReward?> = Result.success(null)
    },
    object : BatchLotteryUseCase {
        override suspend fun execute(count: Int): Result<jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult> =
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
    object : RecoverLotteryUseCase {
        override suspend fun hasPendingRequest(): Boolean = false
        override suspend fun execute(): LotteryOutcome? = null
    },
    object : ObserveLotteryAnimationEnabledUseCase {
        override fun execute(): Flow<Boolean> = flowOf(true)
    },
)

@Preview
@Composable
fun RewardListScreenPreview() {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        RewardListScreen(
            viewModel = previewViewModel,
            onAddNewRewardClick = {},
            onRewardItemClick = {},
            onRewardSaveSucceeded = {},
        )
    }
}

private fun createLotteringPreviewViewModel(): RewardListViewModel = RewardListViewModel(
    object : LotteryUseCase {
        override suspend fun execute(): Result<AcquiredReward?> = awaitCancellation()
    },
    object : BatchLotteryUseCase {
        override suspend fun execute(count: Int): Result<BatchLotteryResult> = awaitCancellation()
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
    object : RecoverLotteryUseCase {
        override suspend fun hasPendingRequest(): Boolean = false
        override suspend fun execute(): LotteryOutcome? = null
    },
    object : ObserveLotteryAnimationEnabledUseCase {
        override fun execute(): Flow<Boolean> = flowOf(true)
    },
)

@Preview
@Composable
fun RewardListScreenSingleLotteringPreview() {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        val viewModel = remember { createLotteringPreviewViewModel().apply { startLottery() } }
        RewardListScreen(
            viewModel = viewModel,
            onAddNewRewardClick = {},
            onRewardItemClick = {},
            onRewardSaveSucceeded = {},
        )
    }
}

@Preview
@Composable
fun RewardListScreenBatchLotteringPreview() {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        val viewModel = remember { createLotteringPreviewViewModel().apply { startBatchLottery() } }
        RewardListScreen(
            viewModel = viewModel,
            onAddNewRewardClick = {},
            onRewardItemClick = {},
            onRewardSaveSucceeded = {},
        )
    }
}

private val emptyPreviewViewModel = RewardListViewModel(
    object : LotteryUseCase {
        override suspend fun execute(): Result<AcquiredReward?> = Result.success(null)
    },
    object : BatchLotteryUseCase {
        override suspend fun execute(count: Int): Result<BatchLotteryResult> =
            Result.success(BatchLotteryResult(emptyList(), count))
    },
    object : GetRewardsUseCase {
        override suspend fun execute(): List<Reward> = emptyList()

        override suspend fun executeAsFlow(): Flow<List<Reward>> = flowOf(emptyList())
    },
    object : GetPointUseCase {
        override suspend fun execute(): Flow<NumberOfTicket> = flowOf(NumberOfTicket(0))
    },
    object : SaveRewardUseCase {
        override suspend fun execute(reward: RewardInput): Result<Unit> = Result.success(Unit)
    },
    object : DeleteRewardUseCase {
        override suspend fun execute(reward: Reward) {}
    },
    object : RecoverLotteryUseCase {
        override suspend fun hasPendingRequest(): Boolean = false
        override suspend fun execute(): LotteryOutcome? = null
    },
    object : ObserveLotteryAnimationEnabledUseCase {
        override fun execute(): Flow<Boolean> = flowOf(true)
    },
)

@Preview
@Composable
fun RewardListScreenEmptyPreview() {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        RewardListScreen(
            viewModel = emptyPreviewViewModel,
            onAddNewRewardClick = {},
            onRewardItemClick = {},
            onRewardSaveSucceeded = {},
        )
    }
}

@Preview
@Composable
fun RewardListScreenErrorPreview() {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        Box(modifier = Modifier.fillMaxSize()) {
            RewardListScreen(
                viewModel = previewViewModel,
                onAddNewRewardClick = {},
                onRewardItemClick = {},
                onRewardSaveSucceeded = {},
            )
            ErrorSnackBar(
                object : SnackbarData {
                    override val visuals = object : SnackbarVisuals {
                        override val message = "抽選に失敗しました"
                        override val actionLabel: String? = null
                        override val withDismissAction = false
                        override val duration = SnackbarDuration.Short
                    }

                    override fun dismiss() {}

                    override fun performAction() {}
                },
            )
        }
    }
}

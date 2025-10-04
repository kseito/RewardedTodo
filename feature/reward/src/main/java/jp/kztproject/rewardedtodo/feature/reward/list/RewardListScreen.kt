@file:OptIn(ExperimentalMaterialApi::class)

package jp.kztproject.rewardedtodo.feature.reward.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.common.ui.CommonAlertDialog
import jp.kztproject.rewardedtodo.common.ui.vibrate
import jp.kztproject.rewardedtodo.common.ui.vibrateRichly
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun RewardListScreenWithBottomSheet(viewModel: RewardListViewModel = hiltViewModel()) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var selectedReward: Reward? by remember { mutableStateOf(null) }
    val onRewardItemClick: (Reward) -> Unit = { reward ->
        coroutineScope.launch {
            selectedReward = reward
            bottomSheetState.show()
        }
    }
    val onAddRewardItemClick: () -> Unit = {
        coroutineScope.launch {
            selectedReward = null
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
                needRepeat = repeat,
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
        reward = selectedReward,
    ) {
        RewardListScreen(
            viewModel = viewModel,
            bottomSheetState = bottomSheetState,
            onAddNewRewardClick = onAddRewardItemClick,
            onRewardItemClick = onRewardItemClick,
        )
    }
}

@Composable
@ExperimentalMaterialApi
private fun RewardListScreen(
    viewModel: RewardListViewModel,
    bottomSheetState: ModalBottomSheetState,
    onAddNewRewardClick: () -> Unit,
    onRewardItemClick: (Reward) -> Unit,
) {
    val ticket by viewModel.rewardPoint.collectAsStateWithLifecycle()
    val rewards by viewModel.rewardList.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val obtainedReward by viewModel.obtainedReward.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
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

            FloatingActionButton(
                onClick = {
                    viewModel.startLottery()
                },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 24.dp),
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Done")
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
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }

    LaunchedEffect(result) {
        result?.let {
            it.fold(
                onSuccess = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                },
                onFailure = { error ->
                    val errorMessageId = ErrorMessageClassifier(error).messageId
                    Toast.makeText(context, errorMessageId, Toast.LENGTH_LONG).show()
                },
            )
            viewModel.clearResult()
        }
    }
    obtainedReward?.let { it ->
        if (it.isSuccess) {
            val reward = it.getOrNull()
            if (reward == null) {
                context.vibrate()

                CommonAlertDialog(
                    message = stringResource(id = R.string.missed_reward),
                    onOkClicked = {
                        viewModel.resetObtainedReward()
                    },
                )
            } else {
                context.vibrateRichly()

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
}

@Composable
private fun ErrorSnackBar(it: SnackbarData) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = it.message,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Preview
@Composable
fun ErrorSnackBarPreview() {
    ErrorSnackBar(
        it = object : SnackbarData {
            override val actionLabel: String?
                get() = null
            override val duration: SnackbarDuration
                get() = SnackbarDuration.Indefinite
            override val message: String
                get() = "Cannot connect Internet."

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
        bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
        onAddNewRewardClick = {},
        onRewardItemClick = {},
    )
}

@Composable
private fun TicketLabel(ticket: Int?, modifier: Modifier = Modifier) {
    Text(
        text = "$ticket tickets",
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = Color.Black,
    )
}

@Composable
private fun RewardList(rewards: List<Reward>?, onRewardItemClick: (Reward) -> Unit) {
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

@Composable
private fun RewardItem(reward: Reward, onRewardItemClick: (Reward) -> Unit) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = { onRewardItemClick(reward) },
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(8f),
            ) {
                Text(
                    text = reward.name.value,
                    style = MaterialTheme.typography.h4,
                )
                Text(
                    text = reward.description.value ?: "",
                    color = Color.Gray,
                )
            }
            Text(
                text = "${reward.probability.value} %",
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.h5,
            )
        }
    }
}

@Preview
@Composable
fun RewardItemPreview() {
    val reward = Reward(
        RewardId(1),
        RewardName("PS5"),
        Probability(1f),
        RewardDescription("this is very rare"),
        true,
    )
    RewardItem(
        reward = reward,
        onRewardItemClick = {},
    )
}

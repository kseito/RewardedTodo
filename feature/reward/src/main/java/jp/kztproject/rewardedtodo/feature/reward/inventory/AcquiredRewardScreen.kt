package jp.kztproject.rewardedtodo.feature.reward.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.feature.reward.detail.ErrorMessageClassifier
import jp.kztproject.rewardedtodo.presentation.reward.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun AcquiredRewardScreen(viewModel: AcquiredRewardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.refresh() }
    AcquiredRewardContent(
        state = state,
        onSelect = viewModel::select,
        onRefresh = viewModel::refresh,
        onMore = viewModel::loadMore,
        onUse = { viewModel.use(it.id) },
    )
}

@Composable
internal fun AcquiredRewardContent(
    state: InventoryState,
    onSelect: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onMore: () -> Unit,
    onUse: (AcquiredReward) -> Unit,
) {
    var selected by remember { mutableStateOf<AcquiredReward?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = !state.used,
                onClick = { onSelect(false) },
                enabled = !state.loading,
                label = { Text(stringResource(R.string.reward_unused)) },
            )
            FilterChip(
                selected = state.used,
                onClick = { onSelect(true) },
                enabled = !state.loading,
                label = { Text(stringResource(R.string.reward_used)) },
            )
            TextButton(onClick = onRefresh, enabled = !state.loading) {
                Text(stringResource(R.string.reward_refresh))
            }
        }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let {
            Text(stringResource(ErrorMessageClassifier(it).messageId), color = MaterialTheme.colorScheme.error)
        }
        if (state.loaded && state.items.isEmpty()) Text(stringResource(R.string.reward_inventory_empty))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.items, key = { it.id.value }) { reward ->
                AcquiredRewardCard(reward, !state.loading) { selected = reward }
            }
            if (state.cursor != null) {
                item {
                    TextButton(onClick = onMore, enabled = !state.loading) {
                        Text(stringResource(R.string.reward_more))
                    }
                }
            }
        }
    }
    selected?.let { reward ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(stringResource(R.string.reward_use)) },
            text = { Text(stringResource(R.string.reward_use_confirm, reward.name.value)) },
            confirmButton = {
                TextButton(onClick = {
                    selected = null
                    onUse(reward)
                }) {
                    Text(stringResource(R.string.reward_use))
                }
            },
            dismissButton = {
                TextButton(onClick = { selected = null }) { Text(stringResource(R.string.reward_cancel)) }
            },
        )
    }
}

@Composable
private fun AcquiredRewardCard(reward: AcquiredReward, enabled: Boolean, onUse: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(reward.name.value, style = MaterialTheme.typography.titleMedium)
            reward.description.value?.takeIf { it.isNotBlank() }?.let { Text(it) }
            Text(stringResource(R.string.reward_acquired_at, localTime(reward.acquiredAt)))
            reward.usedAt?.let { Text(stringResource(R.string.reward_used_at, localTime(it))) }
            if (reward.usedAt == null) {
                Button(onClick = onUse, enabled = enabled) { Text(stringResource(R.string.reward_use)) }
            }
        }
    }
}

private fun localTime(value: String): String = runCatching {
    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").withZone(ZoneId.systemDefault()).format(Instant.parse(value))
}.getOrDefault(value)

@Preview
@Composable
fun AcquiredRewardPreview() {
    MaterialTheme {
        AcquiredRewardContent(
            state = InventoryState(
                items = listOf(
                    AcquiredReward(
                        AcquiredReward.Id("preview"),
                        RewardId(1),
                        RewardName("映画を見る"),
                        RewardDescription("好きな映画を1本"),
                        "2026-11-07T00:00:00Z",
                        null,
                    ),
                ),
                loaded = true,
            ),
            onSelect = {},
            onRefresh = {},
            onMore = {},
            onUse = {},
        )
    }
}

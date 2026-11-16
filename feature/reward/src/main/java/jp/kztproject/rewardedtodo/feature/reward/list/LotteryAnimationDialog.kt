package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jp.kztproject.rewardedtodo.common.ui.vibrate
import jp.kztproject.rewardedtodo.common.ui.vibrateRichly
import jp.kztproject.rewardedtodo.presentation.reward.R
import kotlinx.coroutines.delay

private val LotteryGold = Color(0xFFE9CC91)
private val LotteryNight = Color(0xFF10151D)

/** Presentation only: skipping or recreating this dialog never invokes a lottery use case. */
@Composable
fun LotteryAnimationDialog(
    ready: Boolean,
    rewardNames: List<String>,
    missCount: Int,
    isBatch: Boolean,
    onDismiss: () -> Unit,
) {
    var revealed by rememberSaveable { mutableStateOf(false) }
    var skipped by rememberSaveable { mutableStateOf(false) }
    var visibleCount by rememberSaveable { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }
    val celebration = remember { Animatable(0f) }
    val context = LocalContext.current
    val entries = rewardNames + List(missCount) { null }

    LaunchedEffect(ready, skipped) {
        if (ready && !revealed) {
            if (!skipped) progress.animateTo(1f, tween(3800, easing = LinearEasing))
            revealed = true
            if (rewardNames.isEmpty()) context.vibrate() else context.vibrateRichly()
        }
    }
    LaunchedEffect(revealed, skipped) {
        if (skipped) celebration.snapTo(1f)
        if (revealed && !skipped && rewardNames.isNotEmpty()) {
            celebration.animateTo(1f, tween(1600, easing = LinearEasing))
        }
    }
    LaunchedEffect(revealed, skipped) {
        if (revealed) {
            if (skipped) {
                visibleCount = entries.size
            } else {
                while (visibleCount < entries.size) {
                    visibleCount++
                    delay(if (isBatch) 140 else 0)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = { if (revealed) onDismiss() else skipped = true },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(LotteryNight, Color(0xFF243039), LotteryNight)),
            ),
        ) {
            LotteryStage({
                progress.value
            }, revealed, rewardNames.isNotEmpty(), Modifier.fillMaxSize(), { celebration.value }, isBatch)
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(if (isBatch) R.string.lottery_batch_heading else R.string.lottery_single_heading),
                    color = LotteryGold,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    stringResource(
                        when {
                            !ready -> R.string.lottery_waiting
                            !revealed -> R.string.lottery_opening
                            rewardNames.isEmpty() -> R.string.lottery_try_again
                            else -> R.string.lottery_congratulations
                        },
                    ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
                if (revealed) {
                    Text(
                        stringResource(R.string.lottery_summary, rewardNames.size, missCount),
                        color = LotteryGold,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = if (isBatch) Arrangement.spacedBy(10.dp) else Arrangement.Center,
                    ) {
                        itemsIndexed(entries) { index, name ->
                            AnimatedVisibility(
                                visible = index < visibleCount,
                                enter = fadeIn(tween(360)) +
                                    slideInVertically(tween(440, easing = FastOutSlowInEasing)) { it / 3 } +
                                    scaleIn(tween(440), initialScale = 0.96f),
                            ) {
                                LotteryResultCard(index, name)
                            }
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LotteryGold, contentColor = LotteryNight),
                    ) {
                        Text(stringResource(R.string.lottery_done))
                    }
                } else {
                    Box(Modifier.weight(1f))
                    if (!ready) {
                        CircularProgressIndicator(color = LotteryGold, modifier = Modifier.size(24.dp))
                    }
                }
                if (!revealed || visibleCount < entries.size) {
                    TextButton(onClick = { skipped = true }, enabled = !skipped) {
                        Text(stringResource(R.string.lottery_skip), color = LotteryGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LotteryResultCard(index: Int, name: String?) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(if (name != null) Color(0xFF2E3538) else Color(0xFF1C252D), RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (name !=
                    null
                ) {
                    LotteryGold.copy(alpha = 0.55f)
                } else {
                    Color(0xFF39454E)
                },
                RoundedCornerShape(16.dp),
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("%02d".format(index + 1), color = LotteryGold, style = MaterialTheme.typography.titleMedium)
        Column {
            Text(
                stringResource(if (name != null) R.string.lottery_prize else R.string.lottery_miss),
                color = if (name != null) LotteryGold else Color(0xFFCCC6DA),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                name ?: stringResource(R.string.lottery_miss_message),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
fun LotteryCapsulePreview() {
    Box(Modifier.size(360.dp, 640.dp).background(LotteryNight)) {
        LotteryStage({ 0.4f }, false, false, Modifier.fillMaxSize())
    }
}

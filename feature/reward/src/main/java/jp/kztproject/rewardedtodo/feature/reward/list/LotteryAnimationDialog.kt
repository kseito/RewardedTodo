package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
            if (!skipped) progress.animateTo(1f, tween(2400, easing = LinearEasing))
            revealed = true
            if (rewardNames.isEmpty()) context.vibrate() else context.vibrateRichly()
        }
    }
    LaunchedEffect(revealed, skipped) {
        if (skipped) celebration.snapTo(1f)
        if (revealed && !skipped && rewardNames.isNotEmpty()) {
            celebration.animateTo(1f, tween(1800, easing = LinearEasing))
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
            }, revealed, rewardNames.isNotEmpty(), Modifier.fillMaxSize(), { celebration.value })
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

@Composable
private fun LotteryStage(
    progressValue: () -> Float,
    revealed: Boolean,
    hasWon: Boolean,
    modifier: Modifier = Modifier,
    celebrationValue: () -> Float = { 0f },
) {
    Canvas(modifier) {
        val progress = progressValue()
        val celebration = celebrationValue()
        val stageCenter = Offset(size.width / 2, size.height * 0.47f)
        val radius = size.minDimension * 0.21f
        val arrival = FastOutSlowInEasing.transform((progress / 0.22f).coerceIn(0f, 1f))
        val charge = ((progress - 0.22f) / 0.42f).coerceIn(0f, 1f)
        val opening = FastOutSlowInEasing.transform(((progress - 0.64f) / 0.28f).coerceIn(0f, 1f))
        val dissolve = ((progress - 0.86f) / 0.14f).coerceIn(0f, 1f)
        val glow = if (revealed) 0.10f else 0.12f + charge * 0.12f
        drawCircle(
            Brush.radialGradient(listOf(LotteryGold.copy(alpha = glow), Color.Transparent), stageCenter, radius * 3f),
            radius * 3f,
            stageCenter,
        )
        // Fine orbit lines and distant stars leave the capsule as the focal point.
        repeat(32) { index ->
            val x = ((index * 127 % 997) / 997f) * size.width
            val y = ((index * 239 % 991) / 991f) * size.height
            val alpha = 0.08f + (sin(index + progress * 6f) + 1f) * 0.09f
            drawCircle(LotteryGold.copy(alpha = alpha), (if (index % 4 == 0) 1.5f else 0.8f).dp.toPx(), Offset(x, y))
        }
        if (!revealed) {
            repeat(3) { index ->
                rotate(progress * 55f + index * 60f, stageCenter) {
                    val orbit = radius * (1.38f + index * 0.18f)
                    drawArc(
                        LotteryGold.copy(alpha = (0.12f + charge * 0.16f) * (1f - dissolve)),
                        30f,
                        240f,
                        false,
                        stageCenter - Offset(orbit, orbit),
                        Size(orbit * 2, orbit * 2),
                        style = Stroke(0.7.dp.toPx()),
                    )
                }
            }
            val lift = (1f - arrival) * radius * 0.6f - sin(progress * PI.toFloat()) * radius * 0.1f
            val shake = sin(charge * PI.toFloat() * 10f) * charge * 5f * (1f - opening)
            translate(top = lift) {
                scale(0.82f + arrival * 0.18f - charge * 0.04f, pivot = stageCenter) {
                    rotate(shake, stageCenter) {
                        drawCapsuleHalf(stageCenter, radius, -opening * radius * 1.6f, true, 1f - dissolve)
                        drawCapsuleHalf(stageCenter, radius, opening * radius, false, 1f - dissolve)
                        if (opening < 0.01f) {
                            drawCircle(LotteryNight, radius * 0.23f, stageCenter)
                            drawCircle(LotteryGold, radius * 0.18f, stageCenter, style = Stroke(1.5.dp.toPx()))
                            rotate(45f + charge * 90f, stageCenter) {
                                drawRect(
                                    LotteryGold,
                                    stageCenter - Offset(radius * 0.06f, radius * 0.06f),
                                    Size(radius * 0.12f, radius * 0.12f),
                                )
                            }
                        }
                    }
                }
            }
            val burst = sin(opening * PI.toFloat()).coerceIn(0f, 1f)
            drawCircle(
                Brush.radialGradient(
                    listOf(Color(0xFFFFF4D8).copy(alpha = burst * 0.9f), Color.Transparent),
                    stageCenter,
                    radius * 2.5f,
                ),
                radius * 2.5f,
                stageCenter,
            )
            drawCircle(
                LotteryGold.copy(alpha = burst * 0.5f),
                radius * (1f + opening * 1.6f),
                stageCenter,
                style = Stroke(1.dp.toPx()),
            )
        }
        if (revealed && hasWon) {
            repeat(36) { index ->
                val angle = index * 2.39996f
                val travel = radius * (0.7f + celebration * (1.8f + index % 4 * 0.2f))
                val position =
                    stageCenter +
                        Offset(
                            cos(angle) * travel,
                            sin(angle) * travel + celebration * celebration * size.height * 0.4f,
                        )
                rotate(index * 31f + celebration * 160f, position) {
                    drawRect(
                        (
                            if (index % 3 ==
                                0
                            ) {
                                Color(0xFFB6D9D3)
                            } else {
                                LotteryGold
                            }
                            ).copy(alpha = (1f - celebration) * 0.7f),
                        position,
                        Size(2.dp.toPx(), 5.dp.toPx()),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCapsuleHalf(center: Offset, radius: Float, offset: Float, top: Boolean, opacity: Float) {
    translate(top = offset) {
        drawArc(
            brush = Brush.linearGradient(
                listOf(Color(0xFF8E6738), LotteryGold, Color(0xFFFFF4D8), Color(0xFFA67D46)).map {
                    it.copy(alpha = opacity)
                },
                center - Offset(radius, radius),
                center + Offset(radius, radius),
            ),
            startAngle = if (top) 180f else 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
        )
        drawLine(
            LotteryGold.copy(alpha = opacity),
            center - Offset(radius, 0f),
            center + Offset(radius, 0f),
            1.dp.toPx(),
        )
        if (top) {
            drawArc(
                Color.White.copy(alpha = 0.45f * opacity),
                210f,
                55f,
                false,
                center - Offset(radius * 0.8f, radius * 0.8f),
                Size(radius * 1.6f, radius * 1.6f),
                style = Stroke(2.dp.toPx()),
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

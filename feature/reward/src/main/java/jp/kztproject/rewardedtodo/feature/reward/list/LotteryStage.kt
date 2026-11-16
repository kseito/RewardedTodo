package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val Gold = Color(0xFFFFD478)
private val Ice = Color(0xFFA3EAFF)
private val WhiteGold = Color(0xFFFFF8DE)

/** All effects are procedural. Surface markings rotate around the Y axis, not in the screen plane. */
@Composable
internal fun LotteryStage(
    progressValue: () -> Float,
    revealed: Boolean,
    hasWon: Boolean,
    modifier: Modifier = Modifier,
    celebrationValue: () -> Float = { 0f },
    isBatch: Boolean = false,
) {
    Canvas(modifier) {
        val progress = progressValue()
        val afterglow = celebrationValue()
        val center = Offset(size.width * 0.5f, size.height * 0.47f)
        val radius = size.minDimension * 0.235f
        val arrival = ease(progress.phase(0f, 0.12f))
        val spin = progress.phase(0.10f, 0.66f)
        // Smoothstep gives zero velocity at both ends and ten complete horizontal turns.
        val yaw = (spin * spin * (3f - 2f * spin)) * 20f * PI.toFloat()
        val speed = sin(spin * PI.toFloat()).coerceAtLeast(0f)
        val charge = progress.phase(0.23f, 0.68f)
        val opening = ease(progress.phase(0.69f, 0.88f))
        val fade = 1f - progress.phase(0.88f, 0.98f)
        val burst = if (revealed) (1f - afterglow) * 0.45f else sin(opening * PI.toFloat()).coerceAtLeast(0f)

        bloom(center, radius * 3.8f, Gold, if (revealed) 0.12f else 0.12f + charge * 0.28f)
        // A stage beneath the ball provides depth without competing with the opening.
        val floor = center + Offset(0f, radius * 1.65f)
        drawOval(
            Brush.radialGradient(listOf(Gold.copy(alpha = 0.22f), Color.Transparent), floor, radius * 2f),
            floor - Offset(radius * 2f, radius * 0.38f),
            Size(radius * 4f, radius * 0.76f),
        )
        lightRays(
            center,
            radius,
            progress + afterglow * 0.15f,
            if (revealed) {
                if (hasWon) 0.16f + burst * 0.35f else 0f
            } else {
                charge * 0.12f + burst * 0.75f
            },
        )

        if (!revealed) {
            if (isBatch) {
                // Nine smaller capsules frame the main capsule: ten in total.
                repeat(9) { index ->
                    val angle = index / 9f * 2f * PI.toFloat() - PI.toFloat() / 2
                    val orbit = center + Offset(cos(angle) * radius * 1.9f, sin(angle) * radius * 1.4f)
                    val littleRadius = radius * 0.25f * arrival
                    if (littleRadius > 0f) {
                        metallicSphere(orbit, littleRadius, yaw + index * 0.25f, fade * 0.65f)
                        bloom(orbit, littleRadius * 2.5f, Gold, burst * 0.45f)
                    }
                }
            }
            // Sweeping ellipses are motion trails, rather than particles/confetti.
            repeat(6) { index ->
                val orbit = radius * (1.35f + index * 0.14f)
                drawArc(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Ice.copy(alpha = speed * 0.65f),
                            WhiteGold.copy(
                                alpha =
                                speed * 0.95f,
                            ),
                            Color.Transparent,
                        ),
                    ),
                    startAngle = yaw * 90f / PI.toFloat() + index * 65f,
                    sweepAngle = 200f,
                    useCenter = false,
                    topLeft = center - Offset(orbit, radius * (0.30f + index * 0.09f)),
                    size = Size(orbit * 2f, radius * (0.60f + index * 0.18f)),
                    style = Stroke(radius * (0.014f + index * 0.004f)),
                )
            }
            val lift = (1f - arrival) * radius * 0.9f + sin(progress * 7f) * radius * 0.035f
            translate(left = sin(progress * 150f) * radius * 0.018f * speed, top = lift) {
                scale(0.72f + arrival * 0.28f + charge * 0.06f, pivot = center) {
                    if (opening == 0f) {
                        metallicSphere(center, radius, yaw, 1f, speed)
                    } else {
                        // Split left and right, exposing luminous inner rims.
                        shellHalf(center, radius, opening, true, fade)
                        shellHalf(center, radius, opening, false, fade)
                    }
                }
            }
            if (charge > 0f && opening < 0.95f) {
                electricArcs(center, radius, progress, charge * (1f - opening))
            }
            // Energy builds at the seam before the single, soft exposure burst.
            val seam = progress.phase(0.58f, 0.72f) * (1f - opening)
            bloom(center, radius * 0.6f, WhiteGold, seam * 0.9f)
            drawLine(
                WhiteGold.copy(alpha = seam),
                center - Offset(0f, radius * 0.83f),
                center + Offset(0f, radius * 0.83f),
                radius * 0.025f,
            )
            if (opening > 0f) {
                bloom(center, radius * (1f + opening * 4f), Gold, burst * 0.8f)
                bloom(center, radius * (0.35f + opening), Color.White, burst)
                // Horizontal flare + tall pillar of light emerge from the open capsule.
                drawOval(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = burst), Gold.copy(alpha = burst * 0.3f), Color.Transparent),
                        center,
                        size.width,
                    ),
                    center - Offset(size.width, radius * 0.11f),
                    Size(size.width * 2f, radius * 0.22f),
                )
                drawOval(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, WhiteGold.copy(alpha = burst * 0.8f), Color.Transparent),
                    ),
                    Offset(center.x - radius * 0.18f, 0f),
                    Size(radius * 0.36f, size.height),
                )
                drawCircle(
                    Gold.copy(alpha = burst * 0.8f),
                    radius * (1f + opening * 3.5f),
                    center,
                    style = Stroke(radius * 0.018f),
                )
                // One exposure swell, no repeated full-screen flashing.
                drawRect(WhiteGold.copy(alpha = burst * 0.32f))
            }
        }
        val sparkStrength = if (revealed) {
            if (hasWon) (1f - afterglow) else 0f
        } else {
            charge * (1f - opening) + burst
        }.coerceIn(0f, 1f)
        repeat(22) { index ->
            val angle = index * 2.39996f + progress * 0.7f
            val distance = radius * (1.2f + (index % 5) * 0.25f + opening * 2f + afterglow * 1.7f)
            val point = center + Offset(cos(angle) * distance, sin(angle) * distance)
            val alpha = sparkStrength * (0.45f + 0.55f * abs(sin(index + progress * 4f + afterglow)))
            star(point, radius * (0.025f + (index % 3) * 0.017f), alpha)
        }
    }
}

private fun Float.phase(start: Float, end: Float): Float = ((this - start) / (end - start)).coerceIn(0f, 1f)
private fun ease(value: Float): Float = FastOutSlowInEasing.transform(value)

private fun DrawScope.bloom(center: Offset, radius: Float, color: Color, opacity: Float) {
    if (opacity <= 0f || radius <= 0f) return
    drawCircle(
        Brush.radialGradient(
            listOf(
                color.copy(alpha = opacity.coerceIn(0f, 1f)),
                color.copy(
                    alpha =
                    opacity.coerceIn(0f, 1f) * 0.22f,
                ),
                Color.Transparent,
            ),
            center,
            radius,
        ),
        radius,
        center,
    )
}

private fun DrawScope.metallicSphere(center: Offset, radius: Float, yaw: Float, opacity: Float, motion: Float = 0f) {
    // Fixed world-space lighting preserves volume while the surface spins.
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0xFFFFFBE5), Color(0xFFFFE297), Color(0xFFCB8A28), Color(0xFF593011)).map {
                it.copy(alpha = opacity)
            },
            center - Offset(radius * 0.38f, radius * 0.45f),
            radius * 1.9f,
        ),
        radius,
        center,
    )
    drawCircle(Gold.copy(alpha = opacity * 0.8f), radius, center, style = Stroke(radius * 0.022f))
    // Longitudinal seams follow the front hemisphere using an orthographic Y-axis projection.
    repeat(6) { index ->
        val longitude = yaw + index * PI.toFloat() / 3f
        if (cos(longitude) > 0f) {
            val path = Path()
            repeat(33) { step ->
                val latitude = -PI.toFloat() / 2f + PI.toFloat() * step / 32f
                val x = center.x + sin(longitude) * cos(latitude) * radius
                val y = center.y + sin(latitude) * radius
                if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path,
                Color(0xFF70471D).copy(alpha = opacity * 0.5f * (1f - motion * 0.7f)),
                style = Stroke(radius * 0.022f),
            )
            drawPath(
                path,
                WhiteGold.copy(alpha = opacity * 0.42f * (1f - motion * 0.7f)),
                style = Stroke(radius * 0.007f),
            )
        }
    }
    if (motion > 0f) {
        repeat(3) { index ->
            val y = center.y + (index - 1) * radius * 0.36f
            drawArc(
                WhiteGold.copy(alpha = opacity * motion * 0.23f),
                12f,
                156f,
                false,
                Offset(center.x - radius * 0.92f, y - radius * 0.12f),
                Size(radius * 1.84f, radius * 0.24f),
                style = Stroke(radius * 0.045f),
            )
        }
    }
    // The front emblem crosses the face, compresses at the edge, and disappears behind it.
    val facing = cos(yaw)
    if (facing > 0f) {
        val emblem = center + Offset(sin(yaw) * radius * 0.88f, 0f)
        scale(facing.coerceAtLeast(0.01f), 1f, emblem) {
            drawCircle(Color(0xFF382A1D).copy(alpha = opacity), radius * 0.28f, emblem)
            drawCircle(WhiteGold.copy(alpha = opacity), radius * 0.23f, emblem, style = Stroke(radius * 0.022f))
            star(emblem, radius * 0.14f, opacity)
        }
    }
    // A specular highlight slides as the polished surface rotates.
    val shine = center + Offset(sin(yaw - 0.5f) * radius * 0.5f, -radius * 0.45f)
    bloom(shine, radius * 0.38f, Color.White, opacity * 0.38f)
    drawArc(
        WhiteGold.copy(alpha = opacity * 0.8f),
        215f,
        64f,
        false,
        center - Offset(radius * 0.87f, radius * 0.87f),
        Size(radius * 1.74f, radius * 1.74f),
        style = Stroke(radius * 0.025f),
    )
}

private fun DrawScope.shellHalf(center: Offset, radius: Float, opening: Float, left: Boolean, opacity: Float) {
    val direction = if (left) -1f else 1f
    translate(left = direction * opening * radius * 0.95f, top = -opening * radius * 0.18f) {
        rotate(direction * opening * 24f, center) {
            clipRect(
                left = if (left) center.x - radius * 1.1f else center.x,
                top = center.y - radius * 1.1f,
                right = if (left) center.x else center.x + radius * 1.1f,
                bottom = center.y + radius * 1.1f,
            ) {
                metallicSphere(center, radius, 0f, opacity)
            }
            drawOval(
                Brush.horizontalGradient(
                    listOf(Gold.copy(alpha = opacity), Color.White.copy(alpha = opacity), Gold.copy(alpha = opacity)),
                ),
                center - Offset(radius * opening * 0.12f, radius),
                Size(radius * opening * 0.24f + 0.1f, radius * 2f),
            )
        }
    }
}

private fun DrawScope.lightRays(center: Offset, radius: Float, time: Float, opacity: Float) {
    if (opacity <= 0f) return
    repeat(18) { index ->
        val angle = index * 20f + time * 35f
        rotate(angle, center) {
            val length = size.maxDimension * (0.7f + (index % 3) * 0.15f)
            val width = radius * (0.06f + (index % 4) * 0.035f)
            val ray = Path().apply {
                moveTo(center.x, center.y)
                lineTo(center.x - width, center.y - length)
                lineTo(center.x + width, center.y - length)
                close()
            }
            drawPath(
                ray,
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Gold.copy(
                            alpha =
                            opacity.coerceIn(0f, 1f) * 0.6f,
                        ),
                        WhiteGold.copy(alpha = opacity.coerceIn(0f, 1f)),
                    ),
                    center.y - length,
                    center.y,
                ),
            )
        }
    }
}

private fun DrawScope.electricArcs(center: Offset, radius: Float, time: Float, opacity: Float) {
    repeat(5) { index ->
        val path = Path()
        val base = index * 2f * PI.toFloat() / 5f + time * 3f
        repeat(19) { step ->
            val angle = base + step * 0.045f
            val jitter = sin(step * 2.7f + time * 38f + index) * radius * 0.075f
            val distance = radius * 1.17f + jitter
            val point = center + Offset(cos(angle) * distance, sin(angle) * distance)
            if (step == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(path, Ice.copy(alpha = opacity * 0.18f), style = Stroke(radius * 0.09f))
        drawPath(path, Ice.copy(alpha = opacity * 0.7f), style = Stroke(radius * 0.025f))
        drawPath(path, Color.White.copy(alpha = opacity), style = Stroke(radius * 0.009f))
    }
}

private fun DrawScope.star(center: Offset, radius: Float, opacity: Float) {
    if (opacity <= 0f) return
    bloom(center, radius * 3f, Gold, opacity * 0.32f)
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + radius * 0.2f, center.y - radius * 0.2f)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + radius * 0.2f, center.y + radius * 0.2f)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - radius * 0.2f, center.y + radius * 0.2f)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - radius * 0.2f, center.y - radius * 0.2f)
        close()
    }
    drawPath(path, WhiteGold.copy(alpha = opacity.coerceIn(0f, 1f)))
}

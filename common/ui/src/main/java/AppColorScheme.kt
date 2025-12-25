package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

fun RewardedTodoScheme(isDarkTheme: Boolean) = if (isDarkTheme) DarkColorScheme else LightColorScheme

val LightColorScheme = lightColorScheme()

val DarkColorScheme = darkColorScheme()
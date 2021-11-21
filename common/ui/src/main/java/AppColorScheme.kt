package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun RewardedTodoScheme(isDarkTheme: Boolean) = if (isDarkTheme) DarkColorScheme else LightColorScheme

val LightColorScheme = lightColors()

val DarkColorScheme = darkColors()
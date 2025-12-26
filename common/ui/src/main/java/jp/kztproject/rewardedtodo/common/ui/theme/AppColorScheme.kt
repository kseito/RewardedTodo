package jp.kztproject.rewardedtodo.common.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun RewardedTodoScheme(isDarkTheme: Boolean) = if (isDarkTheme) darkColorScheme() else lightColorScheme()

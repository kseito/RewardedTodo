package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@ExperimentalMaterialApi
@Composable
fun RewardDetailBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            RewardDetailBottomSheetContent()
        },
        content = content
    )
}

@Preview
@ExperimentalMaterialApi
@Composable
private fun RewardDetailBottomSheetContent() {
    // TODO show content
    LazyColumn {
        items(50) {
            ListItem(
                text = { Text("Item $it") },
                icon = {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Localized description"
                    )
                }
            )
        }
    }
}
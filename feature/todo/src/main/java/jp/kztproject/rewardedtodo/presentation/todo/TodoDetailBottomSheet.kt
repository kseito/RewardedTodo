package jp.kztproject.rewardedtodo.presentation.todo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
import jp.kztproject.rewardedtodo.feature.todo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    todo: Todo,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    isSaving: Boolean = false,
) {
    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismissRequest() },
        sheetState = sheetState,
    ) {
        TodoDetailBottomSheetContent(
            todo = todo,
            onTodoSaveSelected = onTodoSaveSelected,
            isSaving = isSaving,
        )
    }
}

@Composable
private fun TodoDetailBottomSheetContent(
    todo: Todo,
    onTodoSaveSelected: (EditingTodo) -> Unit,
    isSaving: Boolean = false,
) {
    var ticketCount by remember(todo.todoistId) { mutableIntStateOf(todo.numberOfTicketsObtained) }
    val presets = listOf(1, 2, 3, 5)
    val decreaseDescription = stringResource(R.string.todo_decrease_tickets)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.todo_edit_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.todo_task_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(Modifier.height(6.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Text(
                text = todo.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(14.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.todo_ticket_on_completion),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(9.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalIconButton(
                onClick = { ticketCount = (ticketCount - 1).coerceAtLeast(1) },
                enabled = !isSaving && ticketCount > 1,
                modifier = Modifier
                    .size(52.dp)
                    .semantics { contentDescription = decreaseDescription },
            ) {
                Text("−", style = MaterialTheme.typography.headlineSmall)
            }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ticket),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = ticketCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            FilledTonalIconButton(
                onClick = { ticketCount = (ticketCount + 1).coerceAtMost(100) },
                enabled = !isSaving && ticketCount < 100,
                modifier = Modifier.size(52.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.todo_increase_tickets))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            presets.forEach { count ->
                val selected = ticketCount == count
                val containerColor =
                    if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                val contentColor =
                    if (selected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                OutlinedButton(
                    onClick = { ticketCount = count },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(11.dp),
                    border = BorderStroke(
                        1.dp,
                        if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) {
                    Text(count.toString())
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            enabled = !isSaving,
            onClick = {
                onTodoSaveSelected(
                    EditingTodo.from(todo).copy(numberOfTicketsObtained = ticketCount),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(stringResource(if (isSaving) R.string.todo_saving else R.string.todo_save))
        }
    }
}

@Preview
@Composable
fun TodoDetailBottomSheetContentEditingPreview() {
    TodoDetailBottomSheetContentEditingThemedPreview(isDark = false)
}

@Preview
@Composable
fun TodoDetailBottomSheetContentEditingDarkPreview() {
    TodoDetailBottomSheetContentEditingThemedPreview(isDark = true)
}

@Composable
private fun TodoDetailBottomSheetContentEditingThemedPreview(isDark: Boolean) {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDark)) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TodoDetailBottomSheetContent(
                todo = Todo(
                    todoistId = "abc123",
                    name = "英語学習",
                    numberOfTicketsObtained = 3,
                    isRepeat = false,
                ),
                onTodoSaveSelected = {},
            )
        }
    }
}

@Preview
@Composable
fun TodoDetailBottomSheetContentRepeatPreview() {
    TodoDetailBottomSheetContentRepeatThemedPreview(isDark = false)
}

@Preview
@Composable
fun TodoDetailBottomSheetContentRepeatDarkPreview() {
    TodoDetailBottomSheetContentRepeatThemedPreview(isDark = true)
}

@Composable
private fun TodoDetailBottomSheetContentRepeatThemedPreview(isDark: Boolean) {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDark)) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TodoDetailBottomSheetContent(
                todo = Todo(
                    todoistId = "repeat-task",
                    name = "ランニング",
                    numberOfTicketsObtained = 1,
                    isRepeat = true,
                ),
                onTodoSaveSelected = {},
            )
        }
    }
}

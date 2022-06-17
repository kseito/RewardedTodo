package jp.kztproject.rewardedtodo.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.presentation.common.TopBar
import jp.kztproject.rewardedtodo.presentation.reward.list.DarkColorScheme
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardedTodoScheme
import jp.kztproject.rewardedtodo.presentation.todo.databinding.ViewTodoDetailBinding
import jp.kztproject.rewardedtodo.presentation.todo.model.EditingTodo
import jp.kztproject.rewardedtodo.todo.application.*
import jp.kztproject.rewardedtodo.todo.domain.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@AndroidEntryPoint
class TodoListFragment : Fragment(), TodoListViewAdapter.OnItemClickListener,
    TodoListViewModel.Callback {

    private val viewModel: TodoListViewModel by viewModels()

    @Inject
    lateinit var adapter: TodoListViewAdapter

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    private var todoDetailDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val onTodoClicked = {}
        val onRewardClicked = {
            fragmentTransitionManager.transitionToRewardListFragment(requireActivity())
        }
        val onSettingClicked = {
            fragmentTransitionManager.transitionToSettingFragmentFromTodoListFragment(
                requireActivity()
            )
        }
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colors = RewardedTodoScheme(isSystemInDarkTheme())
                ) {
                    TodoListScreen(viewModel, onTodoClicked, onRewardClicked, onSettingClicked)
                }
            }
        }
    }

    @Composable
    private fun TodoListScreen(
        viewModel: TodoListViewModel,
        onTodoClicked: () -> Unit,
        onRewardClicked: () -> Unit,
        onSettingClicked: () -> Unit,
    ) {
        val todoList by viewModel.todoList.observeAsState()

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colors.background)
        ) {
            Column {
                TopBar(onTodoClicked, onRewardClicked, onSettingClicked)
                todoList?.forEachIndexed { index, todo ->
                    TodoListItem(
                        todo = todo,
                        onItemClicked = {
                            val editingTodo = EditingTodo.from(todo)
                            showTodoDetail(editingTodo)
                        },
                        onTodoDone = {
                            viewModel.completeTodo(it)
                        })
                    if (index < todoList!!.lastIndex) {
                        Divider()
                    }
                }
            }

            val (createTodoButton) = createRefs()

            FloatingActionButton(
                onClick = {
                    println("test")
                },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .constrainAs(createTodoButton) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(24.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    }

    @Preview
    @Composable
    fun TodoListScreenPreview() {
        val viewModel = TodoListViewModel(
            object : GetTodoListUseCase {
                override fun execute(): Flow<List<Todo>> {
                    // TODO cannot display
                    return flowOf(
                        listOf(
                            Todo(1, 1001, "英語学習", 2f, true),
                        )
                    )
                }
            },
            object : FetchTodoListUseCase {
                override suspend fun execute() {}
            },
            object : UpdateTodoUseCase {
                override suspend fun execute(todo: Todo) {}
            },
            object : DeleteTodoUseCase {
                override suspend fun execute(todo: Todo) {}
            },
            object : CompleteTodoUseCase {
                override suspend fun execute(todo: Todo) {}
            }
        )
        TodoListScreen(
            viewModel = viewModel,
            onTodoClicked = {},
            onRewardClicked = {},
            onSettingClicked = {}
        )
    }

    @Composable
    private fun TodoListItem(todo: Todo, onItemClicked: () -> Unit, onTodoDone: (Todo) -> Unit) {
        var isDone by remember { mutableStateOf(false) }
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onItemClicked)
                .padding(16.dp)
        ) {
            val (checkbox, title, ticketImage, ticketCount) = createRefs()

            Checkbox(
                checked = isDone,
                onCheckedChange = { onTodoDone.invoke(todo) },
                modifier = Modifier
                    .constrainAs(checkbox) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(0.dp, 0.dp, 16.dp, 0.dp)
            )
            Text(
                text = todo.name,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(checkbox.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_ticket),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(36.dp)
                    .constrainAs(ticketImage) {
                        top.linkTo(title.bottom)
                        start.linkTo(title.start)
                    }
            )
            Text(
                text = "${todo.numberOfTicketsObtained}",
                fontSize = 20.sp,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .constrainAs(ticketCount) {
                        top.linkTo(ticketImage.top)
                        start.linkTo(ticketImage.end, 16.dp)
                        bottom.linkTo(ticketImage.bottom)
                    }
            )
        }
    }

    @Preview
    @Composable
    private fun TodoListItemPreview() {
        Surface {
            val todo = Todo(1, 1, "Buy ingredients for dinner", 1f, false)
            TodoListItem(todo, {}) {}
        }
    }

    @Preview
    @Composable
    private fun DarkModeTodoListItemPreview() {
        MaterialTheme(
            colors = DarkColorScheme
        ) {
            val todo = Todo(1, 1, "Buy ingredients for dinner", 1f, false)
            TodoListItem(todo, {}) {}
        }
    }

    override fun onClick(item: Todo) {
        val editingTodo = EditingTodo.from(item)
        showTodoDetail(editingTodo)
    }

    override fun onCompleted(item: Todo) {
        viewModel.completeTodo(item)
    }

    private fun showTodoDetail(item: EditingTodo) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val binding = DataBindingUtil.inflate<ViewTodoDetailBinding>(
            LayoutInflater.from(context), R.layout.view_todo_detail, view as ViewGroup, false
        )
        bottomSheet.setContentView(binding.root)
        binding.todo = item
        binding.viewModel = viewModel
        bottomSheet.show()

        todoDetailDialog = bottomSheet
    }

    override fun afterTodoUpdate() {
        todoDetailDialog?.dismiss()
    }

    override fun onError(error: Throwable) {
        Toast.makeText(requireContext(), "Fail!", Toast.LENGTH_LONG).show()
    }
}

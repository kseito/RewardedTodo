package jp.kztproject.rewardedtodo.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardedTodoScheme
import jp.kztproject.rewardedtodo.presentation.todo.databinding.FragmentTodoListBinding
import jp.kztproject.rewardedtodo.presentation.todo.databinding.ViewTodoDetailBinding
import jp.kztproject.rewardedtodo.presentation.todo.model.EditingTodo
import jp.kztproject.rewardedtodo.todo.domain.Todo
import javax.inject.Inject

@AndroidEntryPoint
class TodoListFragment : Fragment(), TodoListViewAdapter.OnItemClickListener, TodoListViewModel.Callback {

    private lateinit var binding: FragmentTodoListBinding

    private val viewModel: TodoListViewModel by viewModels()

    @Inject
    lateinit var adapter: TodoListViewAdapter

    private var todoDetailDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colors = RewardedTodoScheme(isSystemInDarkTheme())
                ) {
                    TodoListScreen(viewModel)
                }
            }
        }
    }

    @Composable
    private fun TodoListScreen(viewModel: TodoListViewModel) {
        val todoList by viewModel.todoList.observeAsState()
        Column {
            todoList?.forEach { todo ->
                TodoListItem(todo)
            }
        }
    }

    @Composable
    private fun TodoListItem(todo: Todo) {
        var isDone by remember { mutableStateOf(false) }
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (checkbox, title, ticketImage, ticketCount) = createRefs()

            Checkbox(
                checked = isDone,
                onCheckedChange = {
                    //Done task
                },
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
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(checkbox.end)
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
        val todo = Todo(1, 1, "Buy ingredients for dinner", 1f, false)
        TodoListItem(todo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        initTodoListViewAdapter()
//
//        binding.addButton.setOnClickListener {
//            showTodoDetail(EditingTodo())
//        }
    }

//    private fun initTodoListViewAdapter() {
//        val layoutManager = LinearLayoutManager(context)
//        adapter.setListener(this)
//        binding.todoListView.apply {
//            this.layoutManager = layoutManager
//            addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
//            this.adapter = this@TodoListFragment.adapter
//        }
//        viewModel.todoList.observe(viewLifecycleOwner, Observer { todoList ->
//            adapter.setTodo(todoList)
//        })
//    }

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
            LayoutInflater.from(context), R.layout.view_todo_detail, this.binding.root as ViewGroup, false
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
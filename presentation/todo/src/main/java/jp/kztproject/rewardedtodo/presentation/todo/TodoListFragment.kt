package jp.kztproject.rewardedtodo.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTodoListViewAdapter()

        binding.addButton.setOnClickListener {
            showTodoDetail(EditingTodo())
        }
    }

    private fun initTodoListViewAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setListener(this)
        binding.todoListView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
            this.adapter = this@TodoListFragment.adapter
        }
        viewModel.todoList.observe(viewLifecycleOwner, Observer { todoList ->
            adapter.setTodo(todoList)
        })
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
}
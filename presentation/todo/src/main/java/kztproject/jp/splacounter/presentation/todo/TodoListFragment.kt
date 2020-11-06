package kztproject.jp.splacounter.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.presentation.todo.databinding.FragmentTodoListBinding
import kztproject.jp.splacounter.presentation.todo.databinding.ViewTodoDetailBinding
import kztproject.jp.splacounter.presentation.todo.model.EditingTodo
import kztproject.jp.splacounter.todo.domain.Todo
import javax.inject.Inject

class TodoListFragment : Fragment(), TodoListViewAdapter.OnItemClickListener, TodoListViewModel.Callback {

    private lateinit var binding: FragmentTodoListBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoListViewModel

    @Inject
    lateinit var adapter: TodoListViewAdapter

    private var todoDetailDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoListViewModel::class.java)
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
        binding.todoListView.layoutManager = layoutManager
        adapter.setListener(this)
        binding.todoListView.adapter = adapter
        binding.todoListView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        viewModel.todoList.observe(viewLifecycleOwner, Observer { todoList ->
            adapter.setTodo(todoList)
        })
    }

    override fun onClick(item: Todo) {
        val editingTodo = EditingTodo.from(item)
        showTodoDetail(editingTodo)
    }

    private fun showTodoDetail(item: EditingTodo) {
        val bottomSheet = BottomSheetDialog(context!!)
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
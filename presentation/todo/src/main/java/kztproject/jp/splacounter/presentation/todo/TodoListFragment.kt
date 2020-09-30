package kztproject.jp.splacounter.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.presentation.todo.databinding.FragmentTodoListBinding
import javax.inject.Inject

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoListViewModel

    @Inject
    lateinit var adapter: TodoListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.todoListView.layoutManager = LinearLayoutManager(context)
        binding.todoListView.adapter = adapter
        viewModel.observeTodo().observe(viewLifecycleOwner, Observer { todoList ->
            adapter.setTodo(todoList)
        })
        viewModel.loadTodo()
    }
}
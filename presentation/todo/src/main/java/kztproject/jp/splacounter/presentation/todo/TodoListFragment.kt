package kztproject.jp.splacounter.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.AndroidSupportInjection
import kztproject.jp.splacounter.presentation.todo.databinding.FragmentTodoListBinding
import kztproject.jp.splacounter.presentation.todo.model.DummyTodo
import javax.inject.Inject

class TodoListFragment : Fragment(), TodoListViewAdapter.OnItemClickListener {

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
        initTodoListViewAdapter()
        initBottomSheet()

        viewModel.loadTodo()
    }

    private fun initTodoListViewAdapter() {
        binding.todoListView.layoutManager = LinearLayoutManager(context)
        adapter.setListener(this)
        binding.todoListView.adapter = adapter
        viewModel.observeTodo().observe(viewLifecycleOwner, Observer { todoList ->
            adapter.setTodo(todoList)
        })
    }

    private fun initBottomSheet() {
        val behavior = BottomSheetBehavior.from(binding.todoDetail)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(item: DummyTodo) {
        val behavior = BottomSheetBehavior.from(binding.todoDetail)
        if(behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            // TODO display detail
        }
    }
}
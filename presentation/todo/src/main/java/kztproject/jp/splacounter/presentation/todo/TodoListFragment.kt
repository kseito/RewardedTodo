package kztproject.jp.splacounter.presentation.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kztproject.jp.splacounter.presentation.todo.databinding.FragmentTodoListBinding

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }
}
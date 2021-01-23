package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.kztproject.rewardedtodo.presentation.auth.databinding.FragmentTodoistAuthBinding

class TodoistAuthFragment : Fragment() {

    private lateinit var binding: FragmentTodoistAuthBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoistAuthBinding.inflate(inflater, container, false)
        return binding.root
    }
}
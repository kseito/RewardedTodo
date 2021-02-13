package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.kztproject.rewardedtodo.presentation.auth.databinding.FragmentTodoistAssociationBinding

class TodoistAssociationFragment : Fragment() {

    private lateinit var binding: FragmentTodoistAssociationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoistAssociationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startAuthButton.setOnClickListener {
            //TODO execute authorization
        }
    }
}
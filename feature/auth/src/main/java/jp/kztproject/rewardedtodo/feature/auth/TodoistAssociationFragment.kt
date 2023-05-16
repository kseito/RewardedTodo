package jp.kztproject.rewardedtodo.feature.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.feature.auth.databinding.FragmentTodoistAssociationBinding

@AndroidEntryPoint
class TodoistAssociationFragment : Fragment() {

    private lateinit var binding: FragmentTodoistAssociationBinding

    private val viewModel: TodoistAssociationViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoistAssociationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startAuthButton.setOnClickListener {
            startActivity(Intent(context, TodoistAuthActivity::class.java))
        }
        binding.stopAssociationButton.setOnClickListener {
            viewModel.clearAccessToken()
        }

        viewModel.hasAccessToken.observe(viewLifecycleOwner) {
            binding.startAuthButton.visibility = if (it) View.INVISIBLE else View.VISIBLE
            binding.stopAssociationButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadAccessToken()
    }
}

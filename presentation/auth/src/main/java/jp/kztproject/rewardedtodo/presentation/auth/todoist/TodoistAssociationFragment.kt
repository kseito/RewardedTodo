package jp.kztproject.rewardedtodo.presentation.auth.todoist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import jp.kztproject.rewardedtodo.presentation.auth.databinding.FragmentTodoistAssociationBinding
import javax.inject.Inject

class TodoistAssociationFragment : Fragment() {

    private lateinit var binding: FragmentTodoistAssociationBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoistAssociationViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoistAssociationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoistAssociationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startAuthButton.setOnClickListener {
            startActivity(Intent(context, TodoistAuthActivity::class.java))
        }

        viewModel.hasAccessToken.observe(viewLifecycleOwner) {
            //TODO apply UI
            if (it) {
                Toast.makeText(requireContext(), "I have a token", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "I don't have any token", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadAccessToken()
    }
}
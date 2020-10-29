package kztproject.jp.splacounter.presentation.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.todo.application.GetTodoListUseCase
import kztproject.jp.splacounter.todo.application.UpdateTodoUseCase
import kztproject.jp.splacounter.todo.domain.Todo
import javax.inject.Inject

class TodoListViewModel @Inject constructor(
        private val getTodoListUseCase: GetTodoListUseCase,
        private val updateTodoUseCase: UpdateTodoUseCase
) : ViewModel() {

    private val todoList: MutableLiveData<List<Todo>> = MutableLiveData()

    fun loadTodo() {
        viewModelScope.launch(Dispatchers.Default) {
            val newTodoList = getTodoListUseCase.execute()

            withContext(Dispatchers.Main) {
                todoList.value = newTodoList
            }
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Default) {
            updateTodoUseCase.execute(todo)
        }
    }


    fun observeTodo(): LiveData<List<Todo>> {
        return todoList
    }

    interface Callback {
        fun loadTodoCompleted()
    }
}

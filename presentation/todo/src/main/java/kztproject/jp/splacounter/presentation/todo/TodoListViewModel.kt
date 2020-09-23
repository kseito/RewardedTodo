package kztproject.jp.splacounter.presentation.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kztproject.jp.splacounter.presentation.todo.model.DummyTodo
import javax.inject.Inject

class TodoListViewModel @Inject constructor() : ViewModel() {

    private val todoList: MutableLiveData<List<DummyTodo>> = MutableLiveData()

    fun loadTodo() {
        val todoList = listOf(1, 2, 3, 4, 5)
                .map {
                    DummyTodo("Test Todo $it")
                }
        this.todoList.value = todoList
    }

    fun observeTodo(): LiveData<List<DummyTodo>> {
        return todoList
    }

    interface Callback {
        fun loadTodoCompleted()
    }
}

package kztproject.jp.splacounter.presentation.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kztproject.jp.splacounter.presentation.todo.databinding.TodoListItemBinding
import kztproject.jp.splacounter.presentation.todo.model.DummyTodo
import javax.inject.Inject

class TodoListViewAdapter @Inject constructor() : RecyclerView.Adapter<TodoListViewAdapter.ViewHolder>() {

    private val todoList = mutableListOf<DummyTodo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.todo = todoList[position]
    }

    override fun getItemCount(): Int = todoList.size

    fun setTodo(todoList: List<DummyTodo>) {
        this.todoList.clear()
        this.todoList.addAll(todoList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TodoListItemBinding = TodoListItemBinding.bind(view)
    }
}
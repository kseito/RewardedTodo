package jp.kztproject.rewardedtodo.presentation.todo.model

import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.InvalidNumberOfTicketsException
import jp.kztproject.rewardedtodo.domain.todo.NameEmptyException
import jp.kztproject.rewardedtodo.domain.todo.NameLengthTooLongException
import org.junit.Test

class EditingTodoTest {

    @Test
    fun shouldBeValidWhenNameLengthIs1() {
        val todo = EditingTodo(name = "test_todo", numberOfTicketsObtained = 1)
        todo.validate()
    }

    @Test(expected = NameEmptyException::class)
    fun shouldNotBeValidWhenNameLengthIs0() {
        val todo = EditingTodo(name = "")
        todo.validate()
    }

    @Test(expected = NameLengthTooLongException::class)
    fun shouldNotBeValidWhenNameLengthIs101() {
        val todo = EditingTodo(name = "a".repeat(101))
        todo.validate()
    }

    @Test
    fun shouldBeValidWhenNumberOfTicketsIs100() {
        val todo = EditingTodo(name = "test_todo", numberOfTicketsObtained = 100)
        todo.validate()
    }

    @Test(expected = InvalidNumberOfTicketsException::class)
    fun shouldNotBeValidWhenNumberOfTicketsIs0() {
        val todo = EditingTodo(name = "test_todo", numberOfTicketsObtained = 0)
        todo.validate()
    }

    @Test(expected = InvalidNumberOfTicketsException::class)
    fun shouldNotBeValidWhenNumberOfTicketsIs101() {
        val todo = EditingTodo(name = "test_todo", numberOfTicketsObtained = 101)
        todo.validate()
    }
}

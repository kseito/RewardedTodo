package jp.kztproject.rewardedtodo.data.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val name: String,
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
)
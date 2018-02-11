package kztproject.jp.splacounter.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Reward(@PrimaryKey(autoGenerate = true) val id: Int,
                  val name: String,
                  val consumePoint: Int,
                  val description: String?)
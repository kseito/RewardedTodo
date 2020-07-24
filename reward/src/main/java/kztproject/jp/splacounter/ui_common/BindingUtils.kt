package kztproject.jp.splacounter.ui_common

import androidx.databinding.InverseMethod

object BindingUtils {

    @JvmStatic
    @InverseMethod("toInt")
    fun toString(value: Int): String {
        return if (value == 0) "" else value.toString()
    }

    @JvmStatic
    fun toInt(value: String): Int {
        return if (value.isEmpty()) 0 else Integer.parseInt(value)
    }

    @JvmStatic
    @InverseMethod("toFloat")
    fun toString(value: Float): String {
        return if (value == 0F) "" else value.toString()
    }

    @JvmStatic
    fun toFloat(value: String): Float {
        return if (value.isEmpty()) 0F else value.toFloat()
    }
}

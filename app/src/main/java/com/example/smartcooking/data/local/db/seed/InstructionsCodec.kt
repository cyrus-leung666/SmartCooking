package com.example.smartcooking.data.local.db.seed

object InstructionsCodec {

    fun encode(vararg steps: String): String =
        steps.joinToString(prefix = "[", postfix = "]", separator = ",") { step ->
            "\"${escape(step)}\""
        }

    private fun escape(value: String): String = buildString {
        value.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                else -> if (ch < ' ') append("\\u%04x".format(ch.code)) else append(ch)
            }
        }
    }
}

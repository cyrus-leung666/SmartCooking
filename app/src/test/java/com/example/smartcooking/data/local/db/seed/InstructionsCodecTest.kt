package com.example.smartcooking.data.local.db.seed

import org.junit.Assert.assertEquals
import org.junit.Test

class InstructionsCodecTest {

    @Test
    fun `encodes steps as a json string array`() {
        assertEquals("[\"第一步\",\"第二步\"]", InstructionsCodec.encode("第一步", "第二步"))
    }

    @Test
    fun `escapes quotes backslashes and control characters`() {
        assertEquals(
            "[\"a\\\"b\\\\c\\nd\\te\"]",
            InstructionsCodec.encode("a\"b\\c\nd\te")
        )
    }

    @Test
    fun `encodes no steps as an empty array`() {
        assertEquals("[]", InstructionsCodec.encode())
    }
}

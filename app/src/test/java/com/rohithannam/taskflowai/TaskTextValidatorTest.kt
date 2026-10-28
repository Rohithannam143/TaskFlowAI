package com.rohithannam.taskflowai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskTextValidatorTest {
    @Test fun trimsAndCollapsesWhitespace() {
        assertEquals("Study Python", TaskTextValidator.normalize("  Study   Python  "))
    }

    @Test fun rejectsBlankTasks() {
        assertNull(TaskTextValidator.normalize("   "))
    }

    @Test fun limitsLongTaskTitles() {
        assertEquals(TaskTextValidator.MAX_LENGTH,
            TaskTextValidator.normalize("x".repeat(200))!!.length)
    }
}

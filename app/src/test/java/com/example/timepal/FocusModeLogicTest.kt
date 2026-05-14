package com.example.timepal

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for TimePal business logic.
 * These run on the JVM — no Android device or emulator required.
 *
 * Covers the progress calculation logic extracted from FocusModeActivity.updateProgress()
 * and the Focus Engine deadline state machine.
 */
class FocusModeLogicTest {

    // ---------------------------------------------------------------------------
    // Progress calculation
    // ---------------------------------------------------------------------------

    /** Helper — mirrors FocusModeActivity.updateProgress() calculation. */
    private fun calculateProgress(completed: Int, total: Int): Int {
        if (total == 0) return 0
        return ((completed / total.toFloat()) * 100).toInt()
    }

    @Test
    fun `progress is 100 when all steps completed`() {
        assertEquals(100, calculateProgress(3, 3))
    }

    @Test
    fun `progress is 0 when no steps completed`() {
        assertEquals(0, calculateProgress(0, 5))
    }

    @Test
    fun `progress is 50 when half steps completed`() {
        assertEquals(50, calculateProgress(2, 4))
    }

    @Test
    fun `progress is 0 when step list is empty`() {
        assertEquals(0, calculateProgress(0, 0))
    }

    @Test
    fun `progress rounds down for partial completion`() {
        // 1/3 = 33.3% → int truncation → 33
        assertEquals(33, calculateProgress(1, 3))
    }

    // ---------------------------------------------------------------------------
    // Deadline state
    // ---------------------------------------------------------------------------

    /** Mirrors the countdown logic: negative millis → deadline passed. */
    private fun isDeadlinePassed(deadlineTimestamp: Long, nowMillis: Long): Boolean {
        return (deadlineTimestamp - nowMillis) <= 0
    }

    @Test
    fun `deadline is not passed when future timestamp`() {
        val future = System.currentTimeMillis() + 60_000
        assertEquals(false, isDeadlinePassed(future, System.currentTimeMillis()))
    }

    @Test
    fun `deadline is passed when past timestamp`() {
        val past = System.currentTimeMillis() - 1
        assertEquals(true, isDeadlinePassed(past, System.currentTimeMillis()))
    }
}

package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PointHistoryRepositoryTest {

    private val pointHistoryTable = mockk<PointHistoryTable>()
    private val pointHistoryRepository = PointHistoryRepository(pointHistoryTable)

    @Test
    fun findByUserId() {
        val userId = 1L
        val expectedHistories = listOf(
            PointHistory(1L, userId, TransactionType.CHARGE, 1000L, System.currentTimeMillis()),
            PointHistory(2L, userId, TransactionType.USE, 500L, System.currentTimeMillis())
        )

        every { pointHistoryTable.selectAllByUserId(userId) } returns expectedHistories

        val actualHistories = pointHistoryRepository.findByUserId(userId)

        assertEquals(expectedHistories, actualHistories)
    }

}
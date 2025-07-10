package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserPointRepositoryTest {

    private val userPointTable = mockk<UserPointTable>()
    private val userPointRepository = UserPointRepository(userPointTable)

    @Test
    fun getPointByUserId() {
        val userId = 1L
        val expectedPoint = UserPoint(userId, 1000L, System.currentTimeMillis())

        every { userPointTable.selectById(userId) } returns expectedPoint

        val actualPoint = userPointRepository.getPointByUserId(userId)

        assertEquals(expectedPoint, actualPoint)
    }

}
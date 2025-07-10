package io.hhplus.tdd.point

import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class PointServiceTest {

    private val userPointRepository = mockk<UserPointRepository>()
    private val userPointHistoryRepository = mockk<PointHistoryRepository>()
    private val pointService = PointService(userPointRepository, userPointHistoryRepository)

    @Test
    fun getPointByUserId() {
        val userId = 1L
        val userPointExpected = UserPoint(userId, 2, 1)

        every { userPointRepository.getPointByUserId(userId) } returns userPointExpected

        val userPointResult = pointService.getPointByUserId(userId)

        assert(userPointResult == userPointExpected)
    }

    @Test
    fun getPointHistoriesByUserId_return_empty_history() {
        val userId = 1L

        every { userPointHistoryRepository.findByUserId(userId) } returns emptyList()

        val pointHistories = pointService.getPointHistoriesByUserId(userId)

        assert(pointHistories.isEmpty())
    }

    @Test
    fun getPointHistoriesByUserId_return_multi_Histories() {
        val userId = 1L

        val expectedPointHistories = listOf(
            PointHistory(1, userId, TransactionType.CHARGE, 1000, System.currentTimeMillis()),
            PointHistory(2, userId, TransactionType.USE, 500, System.currentTimeMillis())
        )
        every { userPointHistoryRepository.findByUserId(userId) } returns expectedPointHistories
        val actualPointHistories = pointService.getPointHistoriesByUserId(userId)

        assert(actualPointHistories.isNotEmpty())
        assert(actualPointHistories.size == 2)
        assert(actualPointHistories[0] == expectedPointHistories[0])
        assert(actualPointHistories[1] == expectedPointHistories[1])
    }


}
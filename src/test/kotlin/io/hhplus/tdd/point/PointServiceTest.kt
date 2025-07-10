package io.hhplus.tdd.point

import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
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

    @Test
    fun chargePoint() {
        val userId = 1L
        val initialPoint = 100L
        val chargeAmount = 50L
        val updatedPoint = initialPoint + chargeAmount

        val userPoint = UserPoint(id = userId, point = initialPoint, updateMillis = System.currentTimeMillis())

        every { userPointRepository.getPointByUserId(userId) } returns userPoint
        every { userPointHistoryRepository.save(any()) } answers { firstArg<PointHistory>() }
        every { userPointRepository.save(any()) } answers { firstArg<UserPoint>() }

        val result = pointService.chargePoint(userId, chargeAmount)

        result.id shouldBe userId
        result.point shouldBe updatedPoint
    }

    @Test
    fun chargePoint_throws_IllegalArgumentException_cause_by_negative_point() {
        val userId = 1L
        val chargeAmount = -50L

        val userPoint = UserPoint(id = userId, point = 100L, updateMillis = System.currentTimeMillis())

        every { userPointRepository.getPointByUserId(userId) } returns userPoint

        shouldThrow<IllegalArgumentException> { pointService.chargePoint(userId, chargeAmount) }
    }

}
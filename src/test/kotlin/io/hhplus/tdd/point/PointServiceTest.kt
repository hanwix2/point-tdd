package io.hhplus.tdd.point

import io.hhplus.tdd.point.repository.UserPointRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class PointServiceTest {

    private val userPointRepository = mockk<UserPointRepository>()
    private val pointService = PointService(userPointRepository)

    @Test
    fun getPointByUserId() {
        val userId = 1L
        val userPointExpected = UserPoint(userId, 2, 1)

        every { userPointRepository.getPointByUserId(userId) } returns userPointExpected

        val userPointResult = pointService.getPointByUserId(userId)

        assert(userPointResult == userPointExpected)
    }

}
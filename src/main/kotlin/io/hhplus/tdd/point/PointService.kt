package io.hhplus.tdd.point

import io.hhplus.tdd.point.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointRepository: UserPointRepository,
) {

    fun getPointByUserId(userId: Long): UserPoint {
        return userPointRepository.getPointByUserId(userId)
    }

}
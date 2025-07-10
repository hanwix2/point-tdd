package io.hhplus.tdd.point

import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {

    fun getPointByUserId(userId: Long): UserPoint {
        return userPointRepository.getPointByUserId(userId)
    }

    fun getPointHistoriesByUserId(id: Long): List<PointHistory> {
        return pointHistoryRepository.findByUserId(id)
    }

}
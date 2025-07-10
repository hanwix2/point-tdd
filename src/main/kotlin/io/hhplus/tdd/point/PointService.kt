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

    fun chargePoint(id: Long, amount: Long): UserPoint {
        val userPoint = userPointRepository.getPointByUserId(id)
        val pointAddedUserPoint = userPoint.addPoint(amount)

        pointHistoryRepository.save(PointHistory.chargeHistory(id, amount))

        return userPointRepository.save(pointAddedUserPoint)
    }

    fun usePoint(id: Long, amount: Long): UserPoint {
        val userPoint = userPointRepository.getPointByUserId(id)
        val pointUsedUserPoint = userPoint.usePoint(amount)

        pointHistoryRepository.save(PointHistory.useHistory(id, amount))

        return userPointRepository.save(pointUsedUserPoint)
    }

}
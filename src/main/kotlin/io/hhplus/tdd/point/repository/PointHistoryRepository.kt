package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepository(
    private val pointHistoryTable: PointHistoryTable
) {

    fun findByUserId(userId: Long): List<PointHistory> = pointHistoryTable.selectAllByUserId(userId)

    fun save(pointHistory: PointHistory): PointHistory =
        pointHistoryTable.insert(pointHistory.userId, pointHistory.amount, pointHistory.type, pointHistory.timeMillis)

}
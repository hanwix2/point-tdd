package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Repository

@Repository
class UserPointRepository(
    private val userPointTable: UserPointTable
) {

    fun getPointByUserId(userId: Long): UserPoint = userPointTable.selectById(userId)

    fun save(userPoint: UserPoint): UserPoint = userPointTable.insertOrUpdate(userPoint.id, userPoint.point)

}
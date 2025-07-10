package io.hhplus.tdd.point

data class PointHistory(
    val id: Long,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,
    val timeMillis: Long,
) {
    companion object {
        fun chargeHistory(userId: Long, amount: Long, timeMillis: Long = System.currentTimeMillis()): PointHistory =
            PointHistory(0L, userId, TransactionType.CHARGE, amount, timeMillis)
    }
}

/**
 * 포인트 트랜잭션 종류
 * - CHARGE : 충전
 * - USE : 사용
 */
enum class TransactionType {
    CHARGE, USE
}
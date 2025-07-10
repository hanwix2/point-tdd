package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
)
) {

    fun addPoint(amount: Long): UserPoint {
        require(amount >= 0) { "추가할 포인트는 0 이상이어야 합니다." }
        return this.copy(point = this.point + amount, updateMillis = System.currentTimeMillis())
    }
}

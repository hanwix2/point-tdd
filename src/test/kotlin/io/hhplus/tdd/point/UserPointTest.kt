package io.hhplus.tdd.point

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserPointTest {

    @Test
    fun addPoint() {
        val userPoint = UserPoint(id = 1L, point = 100L, updateMillis = System.currentTimeMillis())
        val result = userPoint.addPoint(50L)

        userPoint.point shouldBe 100L
        result.id shouldBe userPoint.id
        result.point shouldBe 150L
    }

    @Test
    fun addPoint_throws_IllegalArgumentException_cause_by_negative_point() {
        val userPoint = UserPoint(id = 1L, point = 100L, updateMillis = System.currentTimeMillis())

        shouldThrow<IllegalArgumentException> { userPoint.addPoint(-50L) }
    }

    @Test
    fun usePoint() {
        val userPoint = UserPoint(id = 1L, point = 100L, updateMillis = System.currentTimeMillis())
        val result = userPoint.usePoint(50L)

        userPoint.point shouldBe 100L
        result.id shouldBe userPoint.id
        result.point shouldBe 50L
    }

    @Test
    fun usePoint_throws_IllegalArgumentException_cause_by_negative_point() {
        val userPoint = UserPoint(id = 1L, point = 100L, updateMillis = System.currentTimeMillis())

        shouldThrow<IllegalArgumentException> { userPoint.usePoint(-50L) }.apply {
            message shouldBe "사용할 포인트는 0 이상이어야 합니다."
        }
    }

    @Test
    fun usePoint_throws_IllegalArgumentException_cause_by_insufficient_point() {
        val userPoint = UserPoint(id = 1L, point = 100L, updateMillis = System.currentTimeMillis())

        shouldThrow<IllegalArgumentException> { userPoint.usePoint(150L) }.apply {
            message shouldBe "포인트가 부족합니다."
        }
    }


}
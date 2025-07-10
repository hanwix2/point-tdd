package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc,
    val pointHistoryTable: PointHistoryTable,
    val userPointTable: UserPointTable
) {

    @Test
    fun `포인트가 없는 유저의 포인트 조회 플로우`() {
        val userId = 1L

        mockMvc.get("/point/$userId")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.id") { value("1") }
                    jsonPath("$.point") { value("0") }
                    jsonPath("$.updateMillis") { isNumber() }
                }
            }
    }

    @Test
    fun `포인트가 없는 유저의 포인트 히스토리 조회 플로우`() {
        val userId = 2L

        mockMvc.get("/point/$userId/histories")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$") { isArray() }
                    jsonPath("$") { isEmpty() }
                }
            }
    }

    @Test
    fun `포인트가(충전한 이력이) 있는 유저의 포인트 조회 플로우`() {
        val userId = 3L
        val amount = 1000L

        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis())
        userPointTable.insertOrUpdate(userId, amount)

        mockMvc.get("/point/$userId")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.id") { value("$userId") }
                    jsonPath("$.point") { value("$amount") }
                    jsonPath("$.updateMillis") { isNumber() }
                }
            }
    }

    @Test
    fun `포인트가(충전한 이력이) 있는 유저의 포인트 히스토리 조회 플로우`() {
        val userId = 4L
        val chargeAmount = 1000L
        val useAmount = 500L

        pointHistoryTable.insert(userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis())
        pointHistoryTable.insert(userId, useAmount, TransactionType.USE, System.currentTimeMillis())
        userPointTable.insertOrUpdate(userId, chargeAmount - useAmount)

        mockMvc.get("/point/$userId/histories")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$[0].userId") { value("$userId") }
                    jsonPath("$[0].amount") { value("$chargeAmount") }
                    jsonPath("$[0].type") { value("CHARGE") }
                    jsonPath("$[1].userId") { value("$userId") }
                    jsonPath("$[1].amount") { value("$useAmount") }
                    jsonPath("$[1].type") { value("USE") }
                }
            }
    }

    @Test
    fun `포인트 충전 플로우`() {
        val userId = 5L
        val chargeAmount = 500L

        mockMvc.patch("/point/$userId/charge") {
            contentType = MediaType.APPLICATION_JSON
            content = "$chargeAmount"
        }.andExpect {
            status().isOk
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id") { value("$userId") }
                jsonPath("$.point") { value("$chargeAmount") }
                jsonPath("$.updateMillis") { isNumber() }
            }
        }
    }

    @Test
    fun `포인트 충전 후 사용 플로우`() {
        val userId = 6L
        val chargeAmount = 500L
        val useAmount = 200L

        // 포인트 충전
        mockMvc.patch("/point/$userId/charge") {
            contentType = MediaType.APPLICATION_JSON
            content = "$chargeAmount"
        }.andExpect {
            status().isOk
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id") { value("$userId") }
                jsonPath("$.point") { value("$chargeAmount") }
                jsonPath("$.updateMillis") { isNumber() }
            }
        }

        // 포인트 사용
        mockMvc.patch("/point/$userId/use") {
            contentType = MediaType.APPLICATION_JSON
            content = "$useAmount"
        }.andExpect {
            status().isOk
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id") { value("$userId") }
                jsonPath("$.point") { value("${chargeAmount - useAmount}") }
                jsonPath("$.updateMillis") { isNumber() }
            }
        }

        // 포인트 사용 후 히스토리 조회
        mockMvc.get("/point/$userId/histories")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$[0].userId") { value("$userId") }
                    jsonPath("$[0].amount") { value("$chargeAmount") }
                    jsonPath("$[0].type") { value("CHARGE") }
                    jsonPath("$[1].userId") { value("$userId") }
                    jsonPath("$[1].amount") { value("$useAmount") }
                    jsonPath("$[1].type") { value("USE") }
                }
            }
    }

    @Test
    fun `포인트 부족으로 인한 포인트 사용 실패 플로우`() {
        val userId = 7L
        val chargeAmount = 500L
        val useAmount = 600L

        // 포인트 충전
        mockMvc.patch("/point/$userId/charge") {
            contentType = MediaType.APPLICATION_JSON
            content = "$chargeAmount"
        }.andExpect {
            status().isOk
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id") { value("$userId") }
                jsonPath("$.point") { value("$chargeAmount") }
                jsonPath("$.updateMillis") { isNumber() }
            }
        }

        // 보유한 포인트보다 많은 포인트 사용 시도
        mockMvc.patch("/point/$userId/use") {
            contentType = MediaType.APPLICATION_JSON
            content = "$useAmount"
        }.andExpect {
            status().isBadRequest
        }
    }

}
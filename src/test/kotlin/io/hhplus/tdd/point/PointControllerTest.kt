package io.hhplus.tdd.point

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PointControllerTest {

    private val pointService = mockk<PointService>()
    private val pointController = PointController(pointService)

    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(pointController).build()
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun getUserPointApi() {
        val userId = 1L
        val expectedUserPoint = UserPoint(userId, 1000L, System.currentTimeMillis())

        every { pointService.getPointByUserId(userId) } returns expectedUserPoint

        mockMvc.get("/point/$userId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id", userId.toString())
                jsonPath("$.point", "1000")
            }
        }
    }

    @Test
    fun getUserPointHistoriesApi() {
        val userId = 1L
        val histories = listOf(
            PointHistory(
                id = 1L,
                userId = userId,
                amount = 100L,
                type = TransactionType.CHARGE,
                timeMillis = System.currentTimeMillis()
            ),
            PointHistory(
                id = 2L,
                userId = userId,
                amount = 50L,
                type = TransactionType.USE,
                timeMillis = System.currentTimeMillis()
            )
        )
        every { pointService.getPointHistoriesByUserId(userId) } returns histories

        mockMvc.get("/point/$userId/histories")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$[0].id", "1")
                    jsonPath("$[0].userId", userId)
                    jsonPath("$[0].amount", "100")
                    jsonPath("$[0].type", "CHARGE")
                    jsonPath("$[1].id", "2")
                    jsonPath("$[1].userId", userId)
                    jsonPath("$[1].amount", "50")
                    jsonPath("$[1].type", "USE")
                }
            }

        verify(exactly = 1) { pointService.getPointHistoriesByUserId(userId) }
    }

    @Test
    fun chargeUserPointApi() {
        val userId = 1L
        val chargeAmount = 500L
        val updatedUserPoint = UserPoint(userId, 1500L, System.currentTimeMillis())

        every { pointService.chargePoint(userId, chargeAmount) } returns updatedUserPoint

        mockMvc.patch("/point/$userId/charge") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(chargeAmount)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id", userId.toString())
                jsonPath("$.point", "1500")
            }
        }

        verify(exactly = 1) { pointService.chargePoint(userId, chargeAmount) }
    }

    @Test
    fun useUserPointApi() {
        val userId = 1L
        val useAmount = 300L
        val updatedUserPoint = UserPoint(userId, 700L, System.currentTimeMillis())

        every { pointService.usePoint(userId, useAmount) } returns updatedUserPoint

        mockMvc.patch("/point/$userId/use") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(useAmount)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id", userId.toString())
                jsonPath("$.point", "700")
            }
        }

        verify(exactly = 1) { pointService.usePoint(userId, useAmount) }
    }

}
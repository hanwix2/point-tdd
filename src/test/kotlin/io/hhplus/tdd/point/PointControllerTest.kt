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

}
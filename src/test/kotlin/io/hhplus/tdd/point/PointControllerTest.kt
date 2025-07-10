package io.hhplus.tdd.point

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PointControllerTest {

    private val pointService = mockk<PointService>()
    private val pointController = PointController(pointService)

    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(pointController).build()

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

}
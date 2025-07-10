package io.hhplus.tdd.point

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService
) {

    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPoint {
        return pointService.getPointByUserId(id)
    }

    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistory> {
        return pointService.getPointHistoriesByUserId(id)
    }

    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        return pointService.chargePoint(id, amount)
    }

    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        return pointService.usePoint(id, amount)
    }
}
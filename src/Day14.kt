class IntPointRange(p1: MutableIntPoint, p2: MutableIntPoint) : ClosedRange<MutableIntPoint> {
    override val start: MutableIntPoint
    override val endInclusive: MutableIntPoint

    init {
        require(p1.x == p2.x || p1.y == p2.y)
        when {
            p1.x == p2.x -> {
                val topMostY = minOf(p1.y, p2.y)
                val bottomMostY = maxOf(p1.y, p2.y)
                start = MutableIntPoint(p1.x, topMostY)
                endInclusive = MutableIntPoint(p1.x, bottomMostY)
            }

            p1.y == p2.y -> {
                val leftMostX = minOf(p1.x, p2.x)
                val rightMostX = maxOf(p1.x, p2.x)
                start = MutableIntPoint(leftMostX, p1.y)
                endInclusive = MutableIntPoint(rightMostX, p1.y)
            }

            else -> TODO()
        }
    }

    private val isXAligned = start.x == endInclusive.x
    private val isYAligned = start.y == endInclusive.y
    val yRange = start.y..endInclusive.y
    val xRange = start.x..endInclusive.x

    override fun contains(value: MutableIntPoint): Boolean {
        when {
            isXAligned -> {
                if (value.x != start.x) return false
                return value.y in yRange
            }

            isYAligned -> {
                if (value.y != start.y) return false
                return value.x in xRange
            }
        }
        return false
    }

    override fun toString(): String {
        return "IntPointRange(start=$start, endInclusive=$endInclusive)"
    }
}

sealed class DropSandResult {
    data class StabalizedGrain(val grain: MutableIntPoint) : DropSandResult()
    object EndlessDrop : DropSandResult()
    object BlockedAtStart : DropSandResult()
}

data class Simulator(
    val pointSequences: List<List<IntPointRange>>,
    val hasFloor: Boolean
) {
    val grains: MutableSet<MutableIntPoint> = mutableSetOf()

    private fun pointIsBlocked(p: MutableIntPoint): Boolean {
        return pointIsBlockedByGrains(p) || pointIsBlockedByWalls(p) || pointIsBlockedByFloor(p)
    }

    private fun pointIsBlockedByGrains(p: MutableIntPoint): Boolean {
        return grains.contains(p)
    }

    private fun pointIsBlockedByWalls(p: MutableIntPoint): Boolean {
        return pointSequences.any { ranges ->
            ranges.any {
                p in it
            }
        }
    }

    fun pointIsBlockedByFloor(p: MutableIntPoint): Boolean {
        if (hasFloor) {
            return p.y >= bottomMostYWithFloor
        }
        return false
    }

    private fun nextState(currPoint: MutableIntPoint): MutableIntPoint? {
        val copy = currPoint.copy()
        copy.y++
        if (!pointIsBlocked(copy)) {
            return copy
        }

        // Down, left
        copy.x--
        if (!pointIsBlocked(copy)) {
            return copy
        }

        // Down, right
        copy.x += 2
        if (!pointIsBlocked(copy)) {
            return copy
        }
        return null
    }

    fun dropSand(): DropSandResult {
        var sand = MutableIntPoint(500, 0)
        if (pointIsBlocked(sand)) {
            return DropSandResult.BlockedAtStart
        }
        do {
            val nextState = nextState(sand)
                ?: return DropSandResult.StabalizedGrain(sand)
            if (!hasFloor) {
                if (nextState.y >= bottomMostY) {
                    return DropSandResult.EndlessDrop
                }
            }
            sand = nextState
        } while (true)
    }

    private val bottomMostY = pointSequences.maxOf {
            it.maxOf {
                maxOf(it.start.y, it.endInclusive.y)
            }
        } + 1
    private val bottomMostYWithFloor = bottomMostY + 1

    fun printSimulator() {
        val leftMostX = pointSequences.minOf {
            it.minOf {
                minOf(it.start.x, it.endInclusive.x)
            }
        } - 1
        val rightMostX = pointSequences.maxOf {
            it.maxOf {
                maxOf(it.start.x, it.endInclusive.x)
            }
        } + 1
        val topMostY = 0
        val bottomMostY = if (hasFloor) bottomMostYWithFloor else bottomMostY

        for (y in topMostY..bottomMostY) {
            for (x in leftMostX..rightMostX) {
                val p = x toip y
                if (pointIsBlockedByWalls(p) || pointIsBlockedByFloor(p)) {
                    print('#')
                } else if (pointIsBlockedByGrains(p)) {
                    print('O')
                } else {
                    print('.')
                }
            }
            println()
        }
    }
}

fun main() {
    fun part1(input: List<String>): Unit {
        var lowestY = 0
        val pointSequences = input.map { line ->
            line.split(" -> ").map {
                it.split(",").run {
                    val currY = get(1).toInt()
                    lowestY = maxOf(lowestY, currY)
                    MutableIntPoint(get(0).toInt() to currY)
                }
            }.windowed(2).map {
                it[0] iR it[1]
            }
        }

        val simulator = Simulator(pointSequences, false)

        println(pointSequences)

        var it = 0
        while (true) {
            when (val sand = simulator.dropSand()) {
                DropSandResult.BlockedAtStart -> {
                    println("Sand ${it + 1} is blocked at source!")
                    simulator.printSimulator()
                    break
                }

                DropSandResult.EndlessDrop -> {
                    println("Sand ${it + 1} drops forever!")
                    simulator.printSimulator()
                    break
                }

                is DropSandResult.StabalizedGrain -> {
                    simulator.grains.add(sand.grain)
                }
            }
            println(it + 1)
            it++
        }

        //        println(IntPoint(498 to 4) >= IntPoint(498 to 4) )
        //        println(IntPoint(498 to 4) <= IntPoint(498 to 6) )

        //        println(IntPoint(498 to 3) in IntPoint(498, 6) iR IntPoint(498 to 4))
        //        println(IntPoint(498 to 4) in IntPoint(498, 6) iR IntPoint(498 to 4))
        //        println(IntPoint(498 to 5) in IntPoint(498, 6) iR IntPoint(498 to 4))
        //        println(IntPoint(498 to 6) in IntPoint(498, 6) iR IntPoint(498 to 4))
        //        println(IntPoint(498 to 7) in IntPoint(498, 6) iR IntPoint(498 to 4))
        //
        //        println()
        //        println(IntPoint(498 to 3) in IntPoint(498 to 4) iR IntPoint(498, 6))
        //        println(IntPoint(498 to 4) in IntPoint(498 to 4) iR IntPoint(498, 6))
        //        println(IntPoint(498 to 5) in IntPoint(498 to 4) iR IntPoint(498, 6))
        //        println(IntPoint(498 to 6) in IntPoint(498 to 4) iR IntPoint(498, 6))
        //        println(IntPoint(498 to 7) in IntPoint(498 to 4) iR IntPoint(498, 6))
        //
        //        println()
        //        println(IntPoint(495 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        //        println(IntPoint(496 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        //        println(IntPoint(497 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        //        println(IntPoint(498 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        //        println(IntPoint(499 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
    }

    fun part2(input: List<String>): Unit {
        var lowestY = 0
        val pointSequences = input.map { line ->
            line.split(" -> ").map {
                it.split(",").run {
                    val currY = get(1).toInt()
                    lowestY = maxOf(lowestY, currY)
                    MutableIntPoint(get(0).toInt() to currY)
                }
            }.windowed(2).map {
                it[0] iR it[1]
            }
        }

        val simulator = Simulator(pointSequences, true)

        println(pointSequences)

        var it = 0
        while (true) {
//            if (it == 20000) break
            when (val sand = simulator.dropSand()) {
                DropSandResult.BlockedAtStart -> {
                    println("Sand ${it + 1} is blocked at source!")
                    simulator.printSimulator()
                    break
                }

                DropSandResult.EndlessDrop -> {
                    println("Sand ${it + 1} drops forever!")
                    simulator.printSimulator()
                    break
                }

                is DropSandResult.StabalizedGrain -> {
                    simulator.grains.add(sand.grain)
                }
            }
            println(it + 1)
            it++
        }

    }

    val dayString = "day14"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //    part1(testInput)
//    part2(testInput)

    val input = readInput("${dayString}_input")
    //        part1(input)
        part2(input)
}

private infix fun MutableIntPoint.iR(mutableIntPoint: MutableIntPoint) = IntPointRange(this, mutableIntPoint)


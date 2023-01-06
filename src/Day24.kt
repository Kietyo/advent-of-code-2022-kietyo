import java.util.LinkedList

data class ExpeditionState(
    val itrNum: Int,
    val currPosition: IntPoint
)

val START_POSITION = 1 toip 0

fun printState(
    height: Int,
    width: Int,
    walls: Set<IntPoint>,
    blizzards: List<Grid.GridElement<Direction>>,
    currPosition: IntPoint = START_POSITION
) {
    for (y in 0..height - 1) {
        for (x in 0..width - 1) {
            val point = x toip y
            if (point == currPosition) {
                print('E')
            } else if (walls.contains(point)) {
                print('#')
            } else {
                val blizzardsAtPoint = blizzards.filter { it.x == point.x && it.y == point.y }
                if (blizzardsAtPoint.isEmpty()) {
                    print('.')
                } else if (blizzardsAtPoint.size == 1) {
                    val firstBlizzard = blizzardsAtPoint.first()
                    print(
                        when (firstBlizzard.value) {
                            Direction.RIGHT -> '>'
                            Direction.DOWN -> 'v'
                            Direction.LEFT -> '<'
                            Direction.UP -> '^'
                        }
                    )
                } else {
                    print(blizzardsAtPoint.size)
                }
            }
        }
        println()
    }
}

class OptimizationContext(
    val width: Int,
    val height: Int,
    val walls: Set<IntPoint>,
    val blizzards: List<Grid.GridElement<Direction>>,
    val blizzardXRange: IntRange,
    val blizzardYRange: IntRange
) {
    val boardXRange = 0 until width
    val boardYRange = 0 until height
    val blizzardRegionWidth = blizzardXRange.last - blizzardXRange.first + 1
    val blizzardRegionHeight = blizzardYRange.last - blizzardYRange.first + 1

    val itrToBlizzardCache = mutableMapOf<Int, List<Grid.GridElement<Direction>>>()

    fun getBlizzardLocationsAfterIterations(itrNum: Int): List<Grid.GridElement<Direction>> {
        return itrToBlizzardCache.computeIfAbsent(itrNum) {
            val newPosition = mutableListOf<Grid.GridElement<Direction>>()
            for (blizzard in blizzards) {
                val normalizedPos = blizzard.x - 1 toip blizzard.y - 1
                val offset = blizzard.value.movementOffset * itrNum
                val newPos = normalizedPos + offset
                newPos.x = normalizeIndex(newPos.x, blizzardRegionWidth) + 1
                newPos.y = normalizeIndex(newPos.y, blizzardRegionHeight) + 1
                newPosition.add(Grid.GridElement(newPos.x, newPos.y, blizzard.value))
            }
            newPosition
        }

    }

    fun getNextAvailableStates(
        currPosition: IntPoint,
        newBlizzards: List<Grid.GridElement<Direction>>
    ): List<IntPoint> {
        val nextStates = mutableListOf<IntPoint>()
        for (direction in Direction.values()) {
            val offset = currPosition + direction.movementOffset
            if (walls.contains(offset)) {
                continue
            }
            if (offset.x !in boardXRange || offset.y !in boardYRange) {
                continue
            }
            val existsBlizzard = newBlizzards.any {
                it.x == offset.x && it.y == offset.y
            }
            if (!existsBlizzard) nextStates.add(offset)
        }
        val existsBlizzardCurrentSpot = newBlizzards.any {
            it.x == currPosition.x && it.y == currPosition.y
        }
        if (!existsBlizzardCurrentSpot) nextStates.add(currPosition)
        return nextStates
    }

    var bestItrNum = Int.MAX_VALUE

    fun calculate(state: ExpeditionState, targetPosition: IntPoint): Int {
        println("state.itrNum: ${state.itrNum}")
        if (state.currPosition == targetPosition) {
            bestItrNum = minOf(bestItrNum, state.itrNum)
            println("went here?!")
            return 0
        }
        val nextBlizzardState = getBlizzardLocationsAfterIterations(state.itrNum)
        printState(nextBlizzardState, state.currPosition)

        var minMinutesNeeded = Int.MAX_VALUE
        val nextPositions = getNextAvailableStates(state.currPosition, nextBlizzardState)
        require(nextPositions.isNotEmpty())
        for (nextPosition in nextPositions) {
            minMinutesNeeded = minOf(
                minMinutesNeeded,
                calculate(ExpeditionState(state.itrNum + 1, nextPosition), targetPosition) + 1
            )
        }

        return minMinutesNeeded
    }

    var statesSeen = 0

    fun calculateBfs(state: ExpeditionState, targetPosition: IntPoint): Int {
        val seenSet = mutableSetOf<ExpeditionState>()
        println("state.itrNum: ${state.itrNum}")
        val queue = LinkedList<ExpeditionState>()
        queue.add(state)

        while (true) {
            statesSeen++
            if (statesSeen % 10000 == 0) {
                println("statesSeen: $statesSeen, seenSet.size: ${seenSet.size}")
            }
            val currentState = queue.removeFirst()!!
            if (currentState.currPosition == targetPosition) {
                return currentState.itrNum
            }
            if (currentState in seenSet) continue
            seenSet += currentState
            val nextBlizzardState = getBlizzardLocationsAfterIterations(currentState.itrNum)
            val nextPositions = getNextAvailableStates(currentState.currPosition, nextBlizzardState)
            for (nextPosition in nextPositions) {
                queue.add(ExpeditionState(currentState.itrNum + 1, nextPosition))
            }
        }
        TODO()
    }

    fun printState(
        blizzards: List<Grid.GridElement<Direction>> = this.blizzards,
        currPosition: IntPoint = START_POSITION
    ) {
        printState(height, width, walls, blizzards, currPosition)
    }

}

fun main() {
    fun part1(input: List<String>): Unit {
        val grid = input.toGrid()

        grid.print()

        val walls = grid.filter { x, y, value -> value == '#' }.map { it.point }.toSet()
        val blizzards = grid.filter { x, y, value ->
            value == '<' || value == '>' || value == '^' || value == 'v'
        }.map {
            Grid.GridElement(
                it.x, it.y, when (it.value) {
                    '<' -> Direction.LEFT
                    '>' -> Direction.RIGHT
                    '^' -> Direction.UP
                    'v' -> Direction.DOWN
                    else -> TODO()
                }
            )
        }

        val endPosition = grid.maxColumns - 2 toip grid.maxRows - 1

        val blizzardXRange = 1..grid.maxColumns - 2
        val blizzardYRange = 1..grid.maxRows - 2

        println("blizzardXRange: $blizzardXRange")
        println("blizzardYRange: $blizzardYRange")

        val context = OptimizationContext(
            grid.maxColumns,
            grid.maxRows,
            walls,
            blizzards,
            blizzardXRange, blizzardYRange
        )

        context.printState()
        println()

        val newBlizzardLocations = context.getBlizzardLocationsAfterIterations(7)
        printState(context.height, context.width, context.walls, newBlizzardLocations)

        val part1 = context.calculateBfs(
            ExpeditionState(0, START_POSITION),
            endPosition
        )

        val part2 = context.calculateBfs(
            ExpeditionState(part1, endPosition),
            START_POSITION
        )

        val part3 = context.calculateBfs(
            ExpeditionState(part2, START_POSITION),
            endPosition
        )

        val totalTime = part1 + part2 + part3

        println("""
            part1: $part1
            part2: $part2
            part3: $part3
        """.trimIndent())

        //        println(context.getNextAvailableStates(6 toip 4, emptyList()))
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day24"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//            part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    part1(input)
    //        part2(input)
}



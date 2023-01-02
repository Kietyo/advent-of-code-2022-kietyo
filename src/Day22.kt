fun String.splitByPredicate(predicate: (Char) -> Boolean): List<String> {
    val splitData = mutableListOf<String>()

    val runningSb = StringBuilder()
    val itr = this.iterator()
    while (itr.hasNext()) {
        val currChar = itr.nextChar()
        if (predicate(currChar)) {
            splitData.add(runningSb.toString())
            splitData.add(currChar.toString())
            runningSb.clear()
        } else {
            runningSb.append(currChar)
        }
    }
    if (runningSb.isNotEmpty()) {
        splitData.add(runningSb.toString())
    }
    return splitData
}

fun normalizeIndex(idx: Int, size: Int): Int {
    val mod = idx % size
    return if (mod < 0) mod + size else mod
}

fun <T> Array<T>.getCyclic(idx: Int): T {
    return get(normalizeIndex(idx, size))
}

fun <T> List<T>.getCyclic(idx: Int): T {
    return get(normalizeIndex(idx, size))
}

enum class Rotation {
    NONE,
    CLOCKWISE,
    COUNTER_CLOCKWISE,
    HALF_ROTATION;
}

data class SideRotationTranslation(
    val sourceSide: Int,
    val destinationSide: Int,
    val directionFromSource: Direction,
    val rotation: Rotation
) {
    //    fun translateSourceToDest(direction: Direction): Direction {
    //        var currDirection = direction
    //        for (rotation in rotations) {
    //            currDirection = when (rotation) {
    //                Rotation.CLOCKWISE -> currDirection.getNextDirectionClockwise()
    //                Rotation.COUNTER_CLOCKWISE -> currDirection.getNextDirectionCounterClockwise()
    //            }
    //        }
    //        return currDirection
    //    }
    //
    //    fun translateDestToSource(direction: Direction): Direction {
    //        var currDirection = direction
    //        for (rotation in rotations.reversed()) {
    //            currDirection = when (rotation) {
    //                Rotation.CLOCKWISE -> currDirection.getNextDirectionCounterClockwise()
    //                Rotation.COUNTER_CLOCKWISE -> currDirection.getNextDirectionClockwise()
    //            }
    //        }
    //        return currDirection
    //    }
}

val TEST_ROTATION_LIST = listOf(
    SideRotationTranslation(1, 2, Direction.UP, Rotation.HALF_ROTATION),
    SideRotationTranslation(1, 3, Direction.LEFT, Rotation.COUNTER_CLOCKWISE),
    SideRotationTranslation(1, 4, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(1, 6, Direction.RIGHT, Rotation.HALF_ROTATION),

    SideRotationTranslation(2, 1, Direction.UP, Rotation.HALF_ROTATION),
    SideRotationTranslation(2, 3, Direction.RIGHT, Rotation.NONE),
    SideRotationTranslation(2, 5, Direction.DOWN, Rotation.HALF_ROTATION),
    SideRotationTranslation(2, 6, Direction.LEFT, Rotation.CLOCKWISE),

    SideRotationTranslation(3, 2, Direction.LEFT, Rotation.NONE),
    SideRotationTranslation(3, 1, Direction.UP, Rotation.CLOCKWISE),
    SideRotationTranslation(3, 4, Direction.RIGHT, Rotation.NONE),
    SideRotationTranslation(3, 5, Direction.DOWN, Rotation.COUNTER_CLOCKWISE),

    SideRotationTranslation(4, 1, Direction.UP, Rotation.NONE),
    SideRotationTranslation(4, 3, Direction.LEFT, Rotation.NONE),
    SideRotationTranslation(4, 5, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(4, 6, Direction.RIGHT, Rotation.CLOCKWISE),

    SideRotationTranslation(5, 3, Direction.LEFT, Rotation.CLOCKWISE),
    SideRotationTranslation(5, 4, Direction.UP, Rotation.NONE),
    SideRotationTranslation(5, 2, Direction.DOWN, Rotation.HALF_ROTATION),
    SideRotationTranslation(5, 6, Direction.RIGHT, Rotation.NONE),

    SideRotationTranslation(6, 5, Direction.LEFT, Rotation.NONE),
    SideRotationTranslation(6, 4, Direction.UP, Rotation.COUNTER_CLOCKWISE),
    SideRotationTranslation(6, 1, Direction.RIGHT, Rotation.HALF_ROTATION),
    SideRotationTranslation(6, 2, Direction.DOWN, Rotation.COUNTER_CLOCKWISE),
)

val INPUT_ROTATION_LIST = listOf(
    // checked
    SideRotationTranslation(1, 6, Direction.UP, Rotation.CLOCKWISE),
    SideRotationTranslation(1, 4, Direction.LEFT, Rotation.HALF_ROTATION),
    SideRotationTranslation(1, 3, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(1, 2, Direction.RIGHT, Rotation.NONE),

    // checked
    SideRotationTranslation(2, 6, Direction.UP, Rotation.NONE),
    SideRotationTranslation(2, 1, Direction.LEFT, Rotation.NONE),
    SideRotationTranslation(2, 3, Direction.DOWN, Rotation.CLOCKWISE),
    SideRotationTranslation(2, 5, Direction.RIGHT, Rotation.HALF_ROTATION),

    // checked
    SideRotationTranslation(3, 1, Direction.UP, Rotation.NONE),
    SideRotationTranslation(3, 4, Direction.LEFT, Rotation.COUNTER_CLOCKWISE),
    SideRotationTranslation(3, 5, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(3, 2, Direction.RIGHT, Rotation.COUNTER_CLOCKWISE),

    // checked
    SideRotationTranslation(4, 3, Direction.UP, Rotation.CLOCKWISE),
    SideRotationTranslation(4, 1, Direction.LEFT, Rotation.HALF_ROTATION),
    SideRotationTranslation(4, 6, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(4, 5, Direction.RIGHT, Rotation.NONE),

    // checked
    SideRotationTranslation(5, 3, Direction.UP, Rotation.NONE),
    SideRotationTranslation(5, 4, Direction.LEFT, Rotation.NONE),
    SideRotationTranslation(5, 6, Direction.DOWN, Rotation.CLOCKWISE),
    SideRotationTranslation(5, 2, Direction.RIGHT, Rotation.HALF_ROTATION),

    //
    SideRotationTranslation(6, 4, Direction.UP, Rotation.NONE),
    SideRotationTranslation(6, 1, Direction.LEFT, Rotation.COUNTER_CLOCKWISE),
    SideRotationTranslation(6, 2, Direction.DOWN, Rotation.NONE),
    SideRotationTranslation(6, 5, Direction.RIGHT, Rotation.COUNTER_CLOCKWISE),
)

enum class Direction(
    val movementOffset: IntPoint,
) {
    RIGHT(1 toip 0),
    DOWN(0 toip 1),
    LEFT(-1 toip 0),
    UP(0 toip -1);

    fun getNextDirectionClockwise(): Direction {
        return Direction.values().getCyclic(ordinal + 1)
    }

    fun getNextDirectionCounterClockwise(): Direction {
        return Direction.values().getCyclic(ordinal - 1)
    }
}

data class Region(
    val id: Int,
    val xRange: IntRange,
    val yRange: IntRange
) {
    val first get() = xRange
    val second get() = yRange

    fun isPointInRegion(point: IntPoint): Boolean {
        return point.x in xRange && point.y in yRange
    }

    fun getPointRelativeToSourceRegion(point: IntPoint): IntPoint {
        require(isPointInRegion(point))
        return MutableIntPoint(
            point.x - xRange.first,
            point.y - yRange.first
        )
    }

    fun relativePointToWorldPoint(point: IntPoint): MutableIntPoint {
        return MutableIntPoint(point.x + xRange.first, point.y + yRange.first)
    }
}

val CREATE_TEST_REGION_FN = { cubeLength: Int ->
    listOf(
        Region(1, ((cubeLength * 2) until (cubeLength * 3)), (cubeLength * 0 until cubeLength)),
        Region(2, (cubeLength * 0 until cubeLength), (cubeLength until (cubeLength * 2))),
        Region(3, ((cubeLength) until (cubeLength * 2)), (cubeLength until (cubeLength * 2))),
        Region(4, ((cubeLength * 2) until (cubeLength * 3)), (cubeLength until (cubeLength * 2))),
        Region(
            5,
            ((cubeLength * 2) until (cubeLength * 3)),
            ((cubeLength * 2) until (cubeLength * 3))
        ),
        Region(
            6,
            ((cubeLength * 3) until (cubeLength * 4)),
            ((cubeLength * 2) until (cubeLength * 3))
        ),
    )
}

val CREATE_INPUT_REGION_FN = { cubeLength: Int ->
    listOf(
        Region(1, ((cubeLength * 1) until (cubeLength * 2)), (0 until cubeLength)),
        Region(2, (cubeLength * 2 until (cubeLength * 3)), (0 until cubeLength)),
        Region(3, ((cubeLength * 1) until (cubeLength * 2)), (cubeLength until (cubeLength * 2))),
        Region(4, (0 until cubeLength), (cubeLength * 2 until (cubeLength * 3))),
        Region(5, (cubeLength until cubeLength * 2), (cubeLength * 2 until (cubeLength * 3))),
        Region(6, (0 until cubeLength), (cubeLength * 3 until (cubeLength * 4))),
    )
}

fun calculatePointRelativeToDestinationRegion(
    sourcePointWithRespectToRegion: IntPoint,
    cubeLength: Int,
    directionFromSourceToDest: Direction,
    destRotationRelativeToSource: Rotation
): MutableIntPoint {
    return when (directionFromSourceToDest) {
        Direction.RIGHT -> when (destRotationRelativeToSource) {
            Rotation.NONE -> 0 toip sourcePointWithRespectToRegion.y // checked
            Rotation.CLOCKWISE -> cubeLength - sourcePointWithRespectToRegion.y - 1 toip 0 // checked
            Rotation.COUNTER_CLOCKWISE -> sourcePointWithRespectToRegion.y toip cubeLength - 1 // checked
            Rotation.HALF_ROTATION -> cubeLength - 1 toip cubeLength - 1 - sourcePointWithRespectToRegion.y // checked
        }

        Direction.DOWN -> when (destRotationRelativeToSource) {
            Rotation.NONE -> sourcePointWithRespectToRegion.x toip 0 // checked
            Rotation.CLOCKWISE -> cubeLength - 1 toip sourcePointWithRespectToRegion.x
            Rotation.COUNTER_CLOCKWISE -> 0 toip cubeLength - sourcePointWithRespectToRegion.x - 1 // checked
            Rotation.HALF_ROTATION -> cubeLength - sourcePointWithRespectToRegion.x - 1 toip cubeLength - 1 // checked
        }

        Direction.LEFT -> when (destRotationRelativeToSource) {
            Rotation.NONE -> cubeLength - 1 toip sourcePointWithRespectToRegion.y // checked
            Rotation.CLOCKWISE -> cubeLength - sourcePointWithRespectToRegion.y - 1 toip cubeLength - 1 // checked
            Rotation.COUNTER_CLOCKWISE -> sourcePointWithRespectToRegion.y toip 0 // fixed
            Rotation.HALF_ROTATION -> 0 toip cubeLength - 1 - sourcePointWithRespectToRegion.y // checked
        }

        Direction.UP -> when (destRotationRelativeToSource) {
            Rotation.NONE -> sourcePointWithRespectToRegion.x toip cubeLength - 1 // checked
            Rotation.CLOCKWISE -> 0 toip sourcePointWithRespectToRegion.x // fixed
            Rotation.COUNTER_CLOCKWISE -> cubeLength - 1 toip cubeLength - sourcePointWithRespectToRegion.x - 1 // checked
            Rotation.HALF_ROTATION -> cubeLength - 1 - sourcePointWithRespectToRegion.x toip 0
        }
    }
}

data class CubeGrid(
    val grid: Grid<Char>,
    val rotationList: List<SideRotationTranslation>,
    val createRegionFn: (Int) -> List<Region>,
    val cubeLength: Int = grid.maxRows / 3
) {

    val regions: List<Region> = createRegionFn(cubeLength)

    fun getPointWithRespectToRegion(point: IntPoint): Pair<Region, MutableIntPoint> {
        val regionRangeWithIndex = try {
            regions.withIndex().first {
                point.x in it.value.first && point.y in it.value.second
            }
        } catch (e: Exception) {
            println("Went here?1")
            IndexedValue(0, regions.first())
        }

        val regionRange = regionRangeWithIndex.value

        val pointWithRespectToRegion = MutableIntPoint(
            point.x - regionRange.first.start,
            point.y - regionRange.second.start
        )

        return regionRangeWithIndex.value to pointWithRespectToRegion
    }

    fun getNextPointForCube(
        currPoint: IntPoint,
        direction: Direction
    ): Pair<IntPoint, Direction> {
        val (currRegion, sourcePointWithRespectToRegion) = getPointWithRespectToRegion(currPoint)
        val offset = direction.movementOffset
        val newPoint = currPoint.clone()
        newPoint.inPlaceAdd(offset)
        if (currRegion.isPointInRegion(newPoint)) {
            val c = grid.getCyclicOrDefault(newPoint.x, newPoint.y) { ' ' }
            if (c == '.') {
                newPoint.x = normalizeIndex(newPoint.x, grid.maxColumns)
                newPoint.y = normalizeIndex(newPoint.y, grid.maxRows)
                return newPoint to direction
            }
            if (c == '#') return currPoint to direction
        } else {
            val translation = rotationList.first {
                it.sourceSide == currRegion.id && direction == it.directionFromSource
            }
            val pointRelativeToDestinationRegion: MutableIntPoint =
                calculatePointRelativeToDestinationRegion(
                    sourcePointWithRespectToRegion, cubeLength, translation.directionFromSource,
                    translation.rotation
                )

            val destinationRegion = regions.first { it.id == translation.destinationSide }
            val finalPoint = destinationRegion.relativePointToWorldPoint(pointRelativeToDestinationRegion)
            val finaldirection = when (translation.rotation) {
                Rotation.CLOCKWISE -> direction.getNextDirectionClockwise()
                Rotation.COUNTER_CLOCKWISE -> direction.getNextDirectionCounterClockwise()
                Rotation.NONE -> direction
                Rotation.HALF_ROTATION -> direction.getNextDirectionClockwise()
                    .getNextDirectionClockwise()
            }

            val c = grid.getCyclicOrDefault(finalPoint.x, finalPoint.y) { ' ' }
            if (c == '.') {
                finalPoint.x = normalizeIndex(finalPoint.x, grid.maxColumns)
                finalPoint.y = normalizeIndex(finalPoint.y, grid.maxRows)
                return finalPoint to finaldirection
            }
            if (c == '#') return currPoint to direction

            println("Went here?!")
        }


        TODO()
    }
}

fun Grid<Char>.getNextPoint(
    currPoint: IntPoint,
    direction: Direction
): IntPoint {
    val offset = direction.movementOffset
    val clone = currPoint.clone()
    while (true) {
        clone.inPlaceAdd(offset)
        val c = getCyclicOrDefault(clone.x, clone.y) {
            ' '
        }
        if (c == '.') {
            clone.x = normalizeIndex(clone.x, maxColumns)
            clone.y = normalizeIndex(clone.y, maxRows)
            return clone
        }
        if (c == '#') return currPoint
    }
}

fun main() {
    fun part1(input: List<String>): Unit {
        val gridLines = input.dropLast(2)
        val commandLine = input.last()

        println(gridLines.joinToString("\n"))
        println(commandLine)

        val grid = Grid(gridLines.map { it.toCharArray().toTypedArray() })

        val commands = commandLine.splitByPredicate {
            it == 'L' || it == 'R'
        }
        println(commands)

        var currPoint: IntPoint = grid.getRow(0).indexOfFirst {
            it == '.'
        } toip 0

        var currDirection = Direction.RIGHT

        for (command in commands) {
            val numOrNull = command.toIntOrNull()
            when {
                numOrNull == null -> {
                    currDirection = when (command) {
                        "R" -> currDirection.getNextDirectionClockwise()
                        "L" -> currDirection.getNextDirectionCounterClockwise()
                        else -> TODO()
                    }
                }

                else -> {
                    val numTimesToMove = numOrNull!!
                    var num = 0
                    while (num++ < numTimesToMove) {
                        val newPoint = grid.getNextPoint(currPoint, currDirection)
                        if (newPoint == currPoint) break
                        currPoint = newPoint
                    }
                }
            }
        }

        val score = (1000 * currPoint.y.inc()) + 4 * currPoint.x.inc() + currDirection.ordinal
        println("currPoint: $currPoint, currDirection: $currDirection, score: $score")

    }

    fun part2(input: List<String>, isInput: Boolean): Unit {
        val gridLines = input.dropLast(2)
        val commandLine = input.last()

        println(gridLines.joinToString("\n"))
        println(commandLine)

        val grid = Grid(gridLines.map { it.toCharArray().toTypedArray() })

        val commands = commandLine.splitByPredicate {
            it == 'L' || it == 'R'
        }
        println(commands)

        var currPoint: IntPoint = grid.getRow(0).indexOfFirst {
            it == '.'
        } toip 0

        val cubeGrid = if (isInput) CubeGrid(
            grid,
            INPUT_ROTATION_LIST,
            CREATE_INPUT_REGION_FN,
            cubeLength = 50
        )
        else CubeGrid(grid, TEST_ROTATION_LIST, CREATE_TEST_REGION_FN)
        var currDirection = Direction.RIGHT
        val path = mutableListOf(currPoint)
        val directions = mutableListOf(currDirection)
        for (command in commands) {
            val numOrNull = command.toIntOrNull()
            when {
                numOrNull == null -> {
                    currDirection = when (command) {
                        "R" -> currDirection.getNextDirectionClockwise()
                        "L" -> currDirection.getNextDirectionCounterClockwise()
                        else -> TODO()
                    }
                }

                else -> {
                    val numTimesToMove = numOrNull!!
                    var num = 0
                    while (num++ < numTimesToMove) {
                        val (newPoint, newDirection) = cubeGrid.getNextPointForCube(
                            currPoint,
                            currDirection
                        )
                        if (newPoint == currPoint) break
                        path.add(newPoint)
                        directions.add(newDirection)
                        currPoint = newPoint
                        currDirection = newDirection
                    }
                }
            }
        }

        val score = (1000 * currPoint.y.inc()) + 4 * currPoint.x.inc() + currDirection.ordinal
        val zip = path.zip(directions)
        println("zip")
        println(zip.joinToString("\n"))
        println("currPoint: $currPoint, currDirection: $currDirection, score: $score")
    }

    val dayString = "day22"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //        part1(testInput)
    //        part2(testInput, false)

    val input = readInput("${dayString}_input")
    //                part1(input)

    // 125079 - too high
    // 182107 too high
    part2(input, true)
}



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
    val rotation: Rotation,
    val directionFromSource: Direction
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

val rotationList = listOf(
    SideRotationTranslation(1, 2, Rotation.HALF_ROTATION, Direction.UP),
    SideRotationTranslation(1, 3, Rotation.COUNTER_CLOCKWISE, Direction.LEFT),
    SideRotationTranslation(1, 4, Rotation.NONE, Direction.DOWN),
    SideRotationTranslation(1, 6, Rotation.HALF_ROTATION, Direction.RIGHT),

    SideRotationTranslation(2, 1, Rotation.HALF_ROTATION, Direction.UP),
    SideRotationTranslation(2, 3, Rotation.NONE, Direction.RIGHT),
    SideRotationTranslation(2, 5, Rotation.HALF_ROTATION, Direction.DOWN),
    SideRotationTranslation(2, 6, Rotation.CLOCKWISE, Direction.LEFT),

    SideRotationTranslation(3, 2, Rotation.NONE, Direction.LEFT),
    SideRotationTranslation(3, 1, Rotation.CLOCKWISE, Direction.UP),
    SideRotationTranslation(3, 4, Rotation.NONE, Direction.RIGHT),
    SideRotationTranslation(3, 5, Rotation.COUNTER_CLOCKWISE, Direction.DOWN),

    SideRotationTranslation(4, 1, Rotation.NONE, Direction.UP),
    SideRotationTranslation(4, 3, Rotation.NONE, Direction.LEFT),
    SideRotationTranslation(4, 5, Rotation.NONE, Direction.DOWN),
    SideRotationTranslation(4, 6, Rotation.CLOCKWISE, Direction.RIGHT),

    SideRotationTranslation(5, 3, Rotation.CLOCKWISE, Direction.LEFT),
    SideRotationTranslation(5, 4, Rotation.NONE, Direction.UP),
    SideRotationTranslation(5, 2, Rotation.HALF_ROTATION, Direction.DOWN),
    SideRotationTranslation(5, 6, Rotation.NONE, Direction.RIGHT),

    SideRotationTranslation(6, 5, Rotation.NONE, Direction.LEFT),
    SideRotationTranslation(6, 4, Rotation.COUNTER_CLOCKWISE, Direction.UP),
    SideRotationTranslation(6, 1, Rotation.HALF_ROTATION, Direction.RIGHT),
    SideRotationTranslation(6, 2, Rotation.COUNTER_CLOCKWISE, Direction.DOWN),
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
        Region(1, ((cubeLength * 2) until (cubeLength * 3)), (0 until cubeLength)),
        Region(2, (0 until cubeLength), (cubeLength until (cubeLength * 2))),
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

fun calculatePointRelativeToDestinationRegion(
    sourcePointWithRespectToRegion: IntPoint,
    cubeLength: Int,
    directionFromSourceToDest: Direction,
    destRotationRelativeToSource: Rotation
) {
    when (directionFromSourceToDest) {
        Direction.RIGHT -> when (destRotationRelativeToSource) {
            Rotation.NONE -> null
            Rotation.CLOCKWISE -> TODO()
            Rotation.COUNTER_CLOCKWISE -> TODO()
            Rotation.HALF_ROTATION -> cubeLength - 1 toip cubeLength - 1 - sourcePointWithRespectToRegion.y
        }
        Direction.DOWN -> TODO()
        Direction.LEFT -> TODO()
        Direction.UP -> TODO()
    }
}

data class CubeGrid(
    val grid: Grid<Char>,
    val createRegionFn: (Int) -> List<Region>
) {
    val cubeLength = grid.maxRows / 3
    val regions: List<Region> = createRegionFn(cubeLength)

    init {
        println("went here?1")
    }

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
            val pointRelativeToDestinationRegion = when {
                (translation.sourceSide == 1 && translation.destinationSide == 3) ||
                        (translation.sourceSide == 3 && translation.destinationSide == 1) -> {
                    sourcePointWithRespectToRegion.y toip sourcePointWithRespectToRegion.x
                }

                (translation.sourceSide == 1 && translation.destinationSide == 2) ||
                        (translation.sourceSide == 2 && translation.destinationSide == 1) -> {
                    sourcePointWithRespectToRegion
                }

                (translation.sourceSide == 2 && translation.destinationSide == 5) ||
                        (translation.sourceSide == 5 && translation.destinationSide == 2) -> {
                    cubeLength - sourcePointWithRespectToRegion.x - 1 toip cubeLength - 1
                }

                (translation.sourceSide == 3 && translation.destinationSide == 5) ||
                        (translation.sourceSide == 6 && translation.destinationSide == 2) -> {
                    0 toip cubeLength - sourcePointWithRespectToRegion.x - 1
                }

                (translation.sourceSide == 5 && translation.destinationSide == 3) ||
                        (translation.sourceSide == 2 && translation.destinationSide == 6) -> {
                    cubeLength - sourcePointWithRespectToRegion.y - 1 toip cubeLength - 1
                }

                (translation.sourceSide == 1 && translation.destinationSide == 6) -> {
                    cubeLength - 1 toip cubeLength - 1 - sourcePointWithRespectToRegion.y
                }
                (translation.sourceSide == 6 && translation.destinationSide == 1) -> {
                    cubeLength - 1 toip cubeLength - 1 - sourcePointWithRespectToRegion.y
                }

                (translation.sourceSide == 4 && translation.destinationSide == 6) -> {
                    cubeLength - sourcePointWithRespectToRegion.y - 1 toip 0
                }

                (translation.sourceSide == 6 && translation.destinationSide == 4) -> {
                    cubeLength - 1 toip cubeLength - sourcePointWithRespectToRegion.x - 1
                }

                else -> null
            }

            val destinationRegion = regions.first { it.id == translation.destinationSide }
            val finalPoint = if (pointRelativeToDestinationRegion == null) newPoint else
                destinationRegion.relativePointToWorldPoint(pointRelativeToDestinationRegion)
            val finaldirection = if (pointRelativeToDestinationRegion == null) direction else
                when (translation.rotation) {
                    Rotation.CLOCKWISE -> direction.getNextDirectionClockwise()
                    Rotation.COUNTER_CLOCKWISE -> direction.getNextDirectionCounterClockwise()
                    Rotation.NONE -> direction
                    Rotation.HALF_ROTATION -> direction.getNextDirectionClockwise().getNextDirectionClockwise()
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

    fun part2(input: List<String>): Unit {
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

        val cubeGrid = CubeGrid(grid, CREATE_TEST_REGION_FN)
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
        println("currPoint: $currPoint, currDirection: $currDirection, score: $score")
        val zip = path.zip(directions)
        println("zip")
        println(zip.joinToString("\n"))
    }

    val dayString = "day22"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //        part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    //                part1(input)
    part2(input)
}



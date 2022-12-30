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

//        println(currPoint)
//
//        val nextPointRight = grid.getNextPoint(currPoint, Direction.RIGHT)
//        val nextPointDown = grid.getNextPoint(currPoint, Direction.DOWN)
//        val nextPointLeft = grid.getNextPoint(currPoint, Direction.LEFT)
//        val nextPointUp = grid.getNextPoint(currPoint, Direction.UP)
//
//        println("nextPointRight: $nextPointRight")
//        println("nextPointDown: $nextPointDown")
//        println("nextPointLeft: $nextPointLeft")
//        println("nextPointUp: $nextPointUp")

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
                    repeat(numTimesToMove) {
                        currPoint = grid.getNextPoint(currPoint, currDirection)
                    }
                }
            }
        }

        val score = (1000 * currPoint.y.inc()) + 4 * currPoint.x.inc() + currDirection.ordinal
        println("currPoint: $currPoint, currDirection: $currDirection, score: $score")


    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day22"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
            part1(input)
    //        part2(input)
}



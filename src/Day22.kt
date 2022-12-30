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
    LEFT(-1 toip 0),
    UP(0 toip -1),
    RIGHT(1 toip 0),
    DOWN(0 toip 1);

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
) {

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

        val currPoint = 0 toip grid.getRow(0).indexOfFirst {
            it == '.'
        }
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
                    val num = numOrNull!!

                }
            }
        }

        println(currPoint)
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day22"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    //        part1(input)
    //        part2(input)
}



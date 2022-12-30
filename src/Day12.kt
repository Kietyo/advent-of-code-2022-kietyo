fun main() {
    val fn = fun Grid<Char>.(point: MutableIntPoint): List<MutableIntPoint> {
        val currChar = get(point)
        val nextChar = when (currChar) {
            'S' -> 'a'
            'z' -> 'E'
            else -> currChar + 1
        }
        val nextRange = when (currChar) {
            'S' -> 'a'..'a'
            'z' -> 'a'..'z'
            else -> 'a'..(currChar + 1)
        }
        val possibleStates = listOf(
            point.copy2(first = point.first + 1),
            point.copy2(first = point.first - 1),
            point.copy2(second = point.second + 1),
            point.copy2(second = point.second - 1),
        )
        return possibleStates.filter {
            getOrDefault(it) { '?' }.run {
                (this == currChar) || this == nextChar || (this in nextRange)
            }
        }
    }

    fun part1(input: List<String>): Unit {
        val grid = Grid(input.map { it.toCharArray().toTypedArray() })
        println(grid.data.joinToString("\n"))
        val startPoint: MutableIntPoint = grid.find('S')
        val endPoint: MutableIntPoint = grid.find('E')

        val result = grid.bfs(startPoint, fn)
        grid.forEach { x, y, value, gotNextRow ->
            if (gotNextRow) {
                println()
                print("${y.toString().padStart(3, '0')}: ")
            }
            if (value == 'l') {
                if (result.pointToMinDist.containsKey(x toip y)) {
                    print('X')
                } else {
                    print('O')
                }
            } else {
                print('O')
            }

        }
        println()

        println("startPoint: $startPoint, endPoint: $endPoint")
        println(result.pointToMinDist[endPoint])
    }

    fun part2(input: List<String>): Unit {
        val grid = Grid(input.map { it.toCharArray().toTypedArray() })
        println(grid.data.joinToString("\n"))
        val startPoint: MutableIntPoint = grid.find('S')
        val endPoint: MutableIntPoint = grid.find('E')

        val aPoints = mutableListOf<MutableIntPoint>()
        grid.forEach { x, y, value, gotNextRow ->
            if (value == 'a') aPoints.add(x toip y)
        }

        val dists = aPoints.map {
            val result = grid.bfs(it, fn)
            result.pointToMinDist[endPoint]
        }.filterNotNull().sorted()

        println(dists)
    }

    val dayString = "day12"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
//                part2(testInput)

    val input = readInput("${dayString}_input")
//        part1(input)
                part2(input)
}


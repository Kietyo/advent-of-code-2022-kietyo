typealias IntPoint = Pair<Int, Int>

class Grid<T : Any>(
    val data: List<List<T>>
) {

    operator fun get(point: IntPoint): T = get(point.first, point.second)
    operator fun get(x: Int, y: Int): T {
        return data[y][x]
    }

    fun getOrDefault(point: IntPoint, default: () -> T): T {
        return data.getOrNull(point.second)?.getOrNull(point.first)
            ?: default()
    }
}

fun main() {

    fun part1(input: List<String>): Unit {
        val grid = Grid(input.map { it.toList() })
        println(grid.data.joinToString("\n"))
        val startPoint: IntPoint = run {
            grid.data.forEachIndexed { y, chars ->
                chars.forEachIndexed { x, c ->
                    if (c == 'S') return@run x to y
                }
            }
            TODO()
        }

        fun nextStates(point: IntPoint, visited: Set<IntPoint>): List<Pair<Int, Int>> {
            val currChar = grid[point]
            val nextChar = if (currChar == 'S') 'a' else {
                currChar + 1
            }
            val possibleStates = listOf(
                point.copy(first = point.first + 1),
                point.copy(first = point.first - 1),
                point.copy(second = point.second + 1),
                point.copy(second = point.second - 1),
            )
            return possibleStates.filter { !visited.contains(it) && grid.getOrDefault(it) { '?' } == nextChar }
        }

        val cache = mutableMapOf<IntPoint, List<IntPoint>>()

        fun getShortestPath(point: IntPoint, visited: Set<IntPoint> = setOf()): List<IntPoint> {
            if (grid[point] == 'E') return listOf(point)
            if (point in cache) return cache[point]!!
            val newVisitedSet = visited + point
            val shortestNextState = nextStates(point, newVisitedSet).map {
                it to getShortestPath(it, newVisitedSet)
            }.filter {
                it.second.isNotEmpty()
            }.minByOrNull {
                it.second.size
            }

            if (shortestNextState == null) {
                cache[point] = emptyList()
                return emptyList()
            }

            require(shortestNextState.second.isNotEmpty())
            val shortestPath = listOf(point) + shortestNextState.second
            cache[point] = shortestPath
            return shortestPath
        }

        val shortestPath = getShortestPath(startPoint, setOf(startPoint))
        println(shortestPath)
        println(shortestPath.size)
    }

    fun part2(input: List<String>): Unit {
    }

    val dayString = "day12"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    part1(testInput)
    //            part2(testInput)

    val input = readInput("${dayString}_input")
    //    part1(input)
    //            part2(input)
}


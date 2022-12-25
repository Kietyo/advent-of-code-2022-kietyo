import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.abs

data class MutableIntPoint(
    var x: Int,
    var y: Int
): Comparable<MutableIntPoint> {
    constructor(pair: Pair<Int, Int>): this(pair.first, pair.second)
    fun copy2(first: Int = x,
              second: Int = y) = MutableIntPoint(first, second)

    val first get() = x
    val second get() = y

    val oneDown get() = MutableIntPoint(x, y + 1)
    val oneDownOneLeft get() = MutableIntPoint(x - 1, y + 1)
    val oneDownOneRight get() = MutableIntPoint(x + 1, y + 1)

    fun manhattanDistance(other: MutableIntPoint): Int {
        return manhattanDistance(other.x, other.y)
    }

    fun manhattanDistance(otherX: Int, otherY: Int): Int {
        return abs(x - otherX) + abs(y - otherY)
    }

    override fun compareTo(other: MutableIntPoint): Int {
        if (first == other.first) {
            return second.compareTo(other.second)
        }
        if (second == other.second) {
            return first.compareTo(other.first)
        }

        val xComp = first.compareTo(other.first)
        if (xComp == 0) {
            return second.compareTo(other.second)
        }
        return xComp
    }

}

class Grid<T : Any>(
    val data: List<List<T>>
) {
    val numRows: Int
        get() = data.size
    val numColumns: Int get() = data.first().size

    operator fun get(point: MutableIntPoint): T = get(point.first, point.second)
    operator fun get(x: Int, y: Int): T {
        return data[y][x]
    }

    fun getOrDefault(point: MutableIntPoint, default: () -> T): T {
        return data.getOrNull(point.second)?.getOrNull(point.first)
            ?: default()
    }

    fun forEach(fn: (x: Int, y: Int, value: T, gotNextRow: Boolean) -> Unit) {
        data.forEachIndexed { y, chars ->
            var isFirst = true
            chars.forEachIndexed { x, v ->
                fn(x, y, v, isFirst)
                isFirst = false
            }
        }
    }

    fun find(v: T): MutableIntPoint{
        data.forEachIndexed { y, chars ->
            chars.forEachIndexed { x, c ->
                if (c == v) return x toip y
            }
        }
        TODO()
    }

    data class DijkstraResult(
        val source: MutableIntPoint,
        val pointToMinDist: Map<MutableIntPoint, Int>,
        val pointToPrev: Map<MutableIntPoint, MutableIntPoint>
    )

    fun dijkstra(source: MutableIntPoint, nextStatesFn: Grid<T>.(point: MutableIntPoint) -> List<MutableIntPoint>): DijkstraResult {
        data class DNode(val point: MutableIntPoint, val distance: Int)

        val pointToMinLengthFromSource = mutableMapOf<MutableIntPoint, Int>()
        val pointToPrev = mutableMapOf<MutableIntPoint, MutableIntPoint>()

        val statesToExplore = PriorityQueue<DNode>(object : Comparator<DNode> {
            override fun compare(o1: DNode, o2: DNode): Int {
                return o1.distance.compareTo(o2.distance)
            }
        })

//        forEach { x, y, value, gotNextRow ->
//            if (x to y != source) {
//                statesToExplore.add(DNode(x to y, Int.MAX_VALUE))
//            }
//        }

        statesToExplore.add(DNode(source, 0))

        while (statesToExplore.isNotEmpty()) {
            val currMinNode = statesToExplore.poll()!!

            val nextStates = this.nextStatesFn(currMinNode.point)
            nextStates.forEach loop@{

                val currBest = pointToMinLengthFromSource.getOrDefault(it, Int.MAX_VALUE)
                val alt = currMinNode.distance + 1
                if (alt < currBest) {
                    pointToPrev[it] = currMinNode.point
                    pointToMinLengthFromSource[it] = alt
                    statesToExplore.add(DNode(it, alt))
                }
            }
        }

        println(pointToMinLengthFromSource)
        println(pointToPrev)
        forEach { x, y, value, gotNextRow ->
            if (gotNextRow) {
                println()
                print("${y.toString().padStart(3, '0')}: ")
            }
            if (pointToMinLengthFromSource.containsKey(x toip y)) {
                print('X')
            } else {
                print('O')
            }
        }
        println()
        return DijkstraResult(
            source, pointToMinLengthFromSource, pointToPrev
        )
    }

    fun bfs(source: MutableIntPoint, nextStatesFn: Grid<T>.(point: MutableIntPoint) -> List<MutableIntPoint>): DijkstraResult {
        val pointToMinLengthFromSource = mutableMapOf<MutableIntPoint, Int>()
        val pointToPrev = mutableMapOf<MutableIntPoint, MutableIntPoint>()

        val queue = LinkedList<MutableIntPoint>()
        queue.add(source)

        pointToMinLengthFromSource[source] = 0

        while (queue.isNotEmpty()) {
            val currPoint = queue.removeFirst()
            val currDist = pointToMinLengthFromSource[currPoint]!!
            val nextStates = this.nextStatesFn(currPoint)
            nextStates.forEach loop@{
                val currBest = pointToMinLengthFromSource.getOrDefault(it, Int.MAX_VALUE)
                val alt = currDist + 1
                if (alt < currBest) {
                    pointToPrev[it] = currPoint
                    pointToMinLengthFromSource[it] = alt
                    queue.add(it)
                }
            }
        }

        println(pointToMinLengthFromSource)
        println(pointToPrev)

        return DijkstraResult(
            source, pointToMinLengthFromSource, pointToPrev
        )
    }
}

infix fun Int.toip(y: Int) = MutableIntPoint(this to y)

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
        val grid = Grid(input.map { it.toList() })
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
        val grid = Grid(input.map { it.toList() })
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


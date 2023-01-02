import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.Comparator
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

class Grid<T : Any>(
    val data: List<Array<T>>,
    val nullData: T? = null
) {
    val maxRows = data.size
    val maxColumns = data.maxOf { it.size }

    init {
        println("maxRows: $maxRows, maxColumns: $maxColumns")
    }

    fun getString(): String {
        val sb = StringBuilder()
        data.forEach {
            sb.appendLine(it.joinToString(""))
        }
        return sb.toString()
    }

    fun print() {
        println(getString())
    }

    operator fun get(point: MutableIntPoint): T = get(point.first, point.second)
    operator fun get(x: Int, y: Int): T {
        return data[y][x]
    }
    fun getCyclicOrDefault(x: Int, y: Int, default: () -> T): T {
        val yNormalize = normalizeIndex(y, maxRows)
        val xNormalize = normalizeIndex(x, maxColumns)
        return getOrDefault(xNormalize, yNormalize, default)
    }

    fun getRow(y: Int): Array<T> {
        require(y in 0 until maxRows)
        return data[y]
    }

    fun getOrDefault(x: Int, y: Int, default: () -> T): T {
        return data.getOrNull(y)?.getOrNull(x)
            ?: default()
    }

    fun getOrDefault(point: MutableIntPoint, default: () -> T): T {
        return getOrDefault(point.x, point.y, default)
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

    fun find(v: T): MutableIntPoint {
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

interface IntPoint {
    val x: Int
    val y: Int

    operator fun plus(other: IntPoint) = MutableIntPoint(x + other.x, y + other.y)

    fun clone(): MutableIntPoint {
        return MutableIntPoint(x, y)
    }
}

data class MutableIntPoint(
    override var x: Int,
    override var y: Int
): Comparable<MutableIntPoint>, IntPoint {
    constructor(pair: Pair<Int, Int>): this(pair.first, pair.second)
    fun copy2(first: Int = x,
              second: Int = y) = MutableIntPoint(first, second)

    val first get() = x
    val second get() = y

    val oneDown get() = MutableIntPoint(x, y + 1)
    val oneDownOneLeft get() = MutableIntPoint(x - 1, y + 1)
    val oneDownOneRight get() = MutableIntPoint(x + 1, y + 1)

    fun inPlaceAdd(other: IntPoint) {
        x += other.x
        y += other.y
    }

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
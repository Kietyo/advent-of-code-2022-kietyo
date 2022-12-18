import kotlin.math.sign

class IntPointRange(p1: IntPoint, p2: IntPoint) : ClosedRange<IntPoint> {
    override val start: IntPoint
    override val endInclusive: IntPoint

    init {
        require(p1.x == p2.x || p1.y == p2.y)
        when {
            p1.x == p2.x -> {
                val topMostY = minOf(p1.y, p2.y)
                val bottomMostY = maxOf(p1.y, p2.y)
                start = IntPoint(p1.x, topMostY)
                endInclusive = IntPoint(p1.x, bottomMostY)
            }
            p1.y == p2.y -> {
                val leftMostX = minOf(p1.x, p2.x)
                val rightMostX = maxOf(p1.x, p2.x)
                start = IntPoint(leftMostX, p1.y)
                endInclusive = IntPoint(rightMostX, p1.y)
            }
            else -> TODO()
        }
    }

    val isXAligned get() = start.x == endInclusive.x
    val isYAligned get() = start.y == endInclusive.y

    override fun contains(value: IntPoint): Boolean {
        when {
            isXAligned -> {
                if (value.x != start.x) return false
                return value.y in start.y..endInclusive.y
            }
            isYAligned -> {
                if (value.y != start.y) return false
                return value.x in start.x..endInclusive.x
            }
        }
        return false
    }
}

fun main() {
    fun part1(input: List<String>): Unit {
        val pointSequences = input.map { line ->
            line.split(" -> ").map {
                it.split(",").run {
                    IntPoint(get(0).toInt() to get(1).toInt())
                }
            }.windowed(2)
        }
//        println(IntPoint(498 to 4) >= IntPoint(498 to 4) )
//        println(IntPoint(498 to 4) <= IntPoint(498 to 6) )

        println(IntPoint(498 to 3) in IntPoint(498, 6) iR IntPoint(498 to 4))
        println(IntPoint(498 to 4) in IntPoint(498, 6) iR IntPoint(498 to 4))
        println(IntPoint(498 to 5) in IntPoint(498, 6) iR IntPoint(498 to 4))
        println(IntPoint(498 to 6) in IntPoint(498, 6) iR IntPoint(498 to 4))
        println(IntPoint(498 to 7) in IntPoint(498, 6) iR IntPoint(498 to 4))

        println()
        println(IntPoint(498 to 3) in IntPoint(498 to 4) iR IntPoint(498, 6))
        println(IntPoint(498 to 4) in IntPoint(498 to 4) iR IntPoint(498, 6))
        println(IntPoint(498 to 5) in IntPoint(498 to 4) iR IntPoint(498, 6))
        println(IntPoint(498 to 6) in IntPoint(498 to 4) iR IntPoint(498, 6))
        println(IntPoint(498 to 7) in IntPoint(498 to 4) iR IntPoint(498, 6))

        println()
        println(IntPoint(495 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        println(IntPoint(496 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        println(IntPoint(497 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        println(IntPoint(498 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        println(IntPoint(499 to 6) in IntPoint(496 to 6) iR IntPoint(498 to 6))
        println(pointSequences)
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day14"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    part1(testInput)
    //                    part2(testInput)

    val input = readInput("${dayString}_input")
    //    part1(input)
//    part2(input)
}

private infix fun IntPoint.iR(intPoint: IntPoint) = IntPointRange(this, intPoint)


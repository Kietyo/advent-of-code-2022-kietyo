import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

fun main() {

    data class Point(var x: Int, var y: Int)
    class Game(
        numPoints: Int
    ) {
        val sqrt2 = sqrt(2.0)
        val tolerance = 0.0001
        val knots: List<Point> = MutableList(numPoints) {
            Point(0, 0)
        }

        fun dst(p1: Point, p2: Point): Double {
            val dst = sqrt(
                (p1.x - p2.x.toDouble()).pow(2) +
                        (p1.y - p2.y.toDouble()).pow(2)
            )
            if (dst in (sqrt2 - tolerance)..(sqrt2 + tolerance)) {
                return 1.0
            }
            return dst
        }

        fun moveHead(dir: String) {
            when (dir) {
                "R" -> knots[0].x += 1
                "L" -> knots[0].x -= 1
                "U" -> knots[0].y += 1
                "D" -> knots[0].y -= 1
            }
            knots.windowed(2).forEach {
                val head = it[0]
                val tail = it[1]
                val dst = dst(head, tail)
                if (dst > 1.0) {
                    val xDst = head.x - tail.x
                    val yDst = head.y - tail.y
                    tail.x += min(xDst.absoluteValue, 1) * xDst.sign
                    tail.y += min(yDst.absoluteValue, 1) * yDst.sign
                }
            }
        }
    }

    fun part1(input: List<String>): Unit {
        val game = Game(2)
        val tailPositions = mutableSetOf<Point>()
        input.forEach {
            val (dir, steps) = it.split(" ")
            val stepsAsInt = steps.toInt()
            repeat(stepsAsInt) {
                game.moveHead(dir)
                tailPositions.add(game.knots.last().copy())
            }
        }
        println(tailPositions.size)
    }

    fun part2(input: List<String>): Unit {
        val game = Game(10)
        val tailPositions = mutableSetOf<Point>()
        input.forEach {
            val (dir, steps) = it.split(" ")
            val stepsAsInt = steps.toInt()
            repeat(stepsAsInt) {
                game.moveHead(dir)
                tailPositions.add(game.knots.last().copy())
            }
        }
        println(tailPositions.size)
    }

    val dayString = "day9"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //    part1(testInput)
    //        part2(testInput)

    val input = readInput("${dayString}_input")
    part1(input)
    //        part2(input)
}

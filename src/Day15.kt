import kotlin.math.abs

fun main() {
    data class Sensor(
        val pos: MutableIntPoint,
        val manhattanDistance: Int
    ) {
        fun isWithinRange(other: MutableIntPoint): Boolean {
            return isWithinRange(other.x, other.y)
        }

        fun isWithinRange(x: Int, y: Int): Boolean {
            return pos.manhattanDistance(x, y) <= manhattanDistance
        }

        val xRange = (pos.x - manhattanDistance)..(pos.x + manhattanDistance)
        fun getYSensorRangeAtX(x: Int): IntRange? {
            if (x in xRange) {
                val xDst = abs(pos.x - x)
                return (pos.y - manhattanDistance + xDst)..(pos.y + manhattanDistance - xDst)
            }
            return null
        }
    }

    val regex =
        Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")

    fun part1(input: List<String>): Unit {

        var leftMostX = Int.MAX_VALUE
        var rightMostX = Int.MIN_VALUE
        val beaconPositions = mutableSetOf<MutableIntPoint>()
        val sensors = input.map {
            val (x1, y1, x2, y2) = regex.find(it)!!.run { groupValues.drop(1) }.toList()
            val sensorPos = x1.toInt() toip y1.toInt()
            val beaconPos = x2.toInt() toip y2.toInt()
            val dst = sensorPos.manhattanDistance(beaconPos)
            leftMostX = minOf(leftMostX, sensorPos.x - dst)
            rightMostX = maxOf(rightMostX, sensorPos.x + dst)
            beaconPositions.add(beaconPos)
            println(
                """
                p1: $sensorPos, p2: $beaconPos, dst: $dst
            """.trimIndent()
            )
            Sensor(sensorPos, dst)
        }
        val sensorPositions = sensors.map { it.pos }.toSet()

        println("leftMostX: $leftMostX, rightMostX: $rightMostX")

        val yLine = 2000000
        val count = (leftMostX..rightMostX).count { x ->
            val currPos = x toip yLine
            val withinRangeOfAtLeastOneSensor = sensors.any {
                it.isWithinRange(currPos)
            }
            if (withinRangeOfAtLeastOneSensor) {
                require(currPos !in sensorPositions)
                currPos !in beaconPositions
            } else {
                false
            }
        }
        println(count)

        //        println(split[8])
        //        println(split[9])
    }

    fun part2(input: List<String>): Unit {
        var leftMostX = Int.MAX_VALUE
        var rightMostX = Int.MIN_VALUE
        val beaconPositions = mutableSetOf<MutableIntPoint>()
        val sensors = input.map {
            val (x1, y1, x2, y2) = regex.find(it)!!.run { groupValues.drop(1) }.toList()
            val sensorPos = x1.toInt() toip y1.toInt()
            val beaconPos = x2.toInt() toip y2.toInt()
            val dst = sensorPos.manhattanDistance(beaconPos)
            leftMostX = minOf(leftMostX, sensorPos.x - dst)
            rightMostX = maxOf(rightMostX, sensorPos.x + dst)
            beaconPositions.add(beaconPos)
            println(
                """
                p1: $sensorPos, p2: $beaconPos, dst: $dst
            """.trimIndent()
            )
            Sensor(sensorPos, dst)
        }
        val sensorPositions = sensors.map { it.pos }.toSet()

        println("leftMostX: $leftMostX, rightMostX: $rightMostX")

        //        val limit = 20
        val limit = 4_000_000

        (0..limit).forEach { x ->
//            println("Finish x=$x")
            val mergedRanges = sensors.asSequence().mapNotNull { it.getYSensorRangeAtX(x) }.sortedBy {
                it.first
            }.reduce { acc, intRange ->
                //                println("acc: $acc, intRange: $intRange")
                if (intRange.first in acc) {
                    acc.first..maxOf(intRange.last, acc.last)
                } else if (acc.last + 1 == intRange.first) {
                    acc.first..maxOf(intRange.last, acc.last)
                } else {
                    acc
                }
            }
            if (0 in mergedRanges && limit in mergedRanges) {
                return@forEach
            }
            (0..limit).forEach { y ->
                val withinRangeOfAtLeastOneSensor = sensors.any {
                    it.isWithinRange(x, y)
                }
                if (!withinRangeOfAtLeastOneSensor) {
                    val currPos = x toip y

                    println("not within range of any sensors: $currPos")
                    println(x.toLong() * 4000000L + y.toLong())
                    return
                }
            }
        }

    }

    val dayString = "day15"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //        part1(testInput)
    part2(testInput)

    val input = readInput("${dayString}_input")
    // 5157464 is too low
    //    part1(input)
                part2(input)
//    println(2829680L * 4000000L + 3411840L)
}



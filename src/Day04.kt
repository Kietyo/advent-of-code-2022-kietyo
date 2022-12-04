
fun String.toClosedRange(): IntRange {
    val (first, second) = this.split("-").map { it.toInt() }
    return first..second
}

fun main() {
    fun part1(input: List<String>): Unit {
        var count = 0
        input.forEach { line ->
            val (first, second) = line.split(",").map { it.toClosedRange() }
            if (first inRange second || second inRange first) {
                count++
            }
        }
        println(count)
    }

    fun part2(input: List<String>): Unit {
        var count = 0
        input.forEach { line ->
            val (first, second) = line.split(",").map { it.toClosedRange() }
            val overlaps = first.intersect(second).isNotEmpty()
            println("first: $first, second: $second, overlaps: $overlaps")
//            val inRange = first inRange second || second inRange first
            if (overlaps) {
                count++
            }
        }
        println(count)
    }
    val dayString = "day4"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    part1(testInput)
    part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
    part2(input)
}

private infix fun IntRange.inRange(second: IntRange): Boolean {
    return this.first in second && this.last in second
}

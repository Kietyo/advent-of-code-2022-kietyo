import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        var highestSum = 0
        var currentSum = 0
        for (s in input) {
            if (s.isEmpty()) {
                highestSum = max(highestSum, currentSum)
                currentSum = 0
            } else {
                currentSum += s.toInt()
            }
        }
        return highestSum
    }

    fun part2(input: List<String>): Unit {
        val highestScores = mutableListOf<Int>()
        var currentSum = 0
        for (s in input) {
            if (s.isEmpty()) {
                highestScores.add(currentSum)
                currentSum = 0
            } else {
                currentSum += s.toInt()
            }
        }
        highestScores.sortDescending()
        println(highestScores.take(3).sum())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day1_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("day1_input")
    println(part1(input))
    println(part2(input))
}

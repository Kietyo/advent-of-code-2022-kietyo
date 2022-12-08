

fun main() {
    fun part1(input: List<String>): Unit {
        input.forEach finished@{ line ->
            line.withIndex().windowed(4).forEach { window ->
                if (window.map { it.value }.toSet().size == 4) {
                    println(window)
                    return@finished
                }
            }
        }
    }

    fun part2(input: List<String>): Unit {
        input.forEach finished@{ line ->
            line.withIndex().windowed(14).forEach { window ->
                if (window.map { it.value }.toSet().size == 14) {
                    println(window)
                    return@finished
                }
            }
        }
    }

    val dayString = "day6"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
    part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
    part2(input)
}

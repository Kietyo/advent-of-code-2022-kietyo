
fun Char.toPriority(): Int {
    if (this in 'a'..'z') {
        return this.code - 96
    } else if (this in 'A'..'Z') {
        return this.code - 65 + 27
    }
    TODO()
}

fun main() {
    fun part1(input: List<String>): Unit {
        var sum = 0
        input.forEach {line ->
            val halfSize = line.length / 2
            val (s1, s2) = line.chunked(halfSize).map { it.toSet() }
            val intersect = s1.intersect(s2)
            require(intersect.size == 1)
            sum += intersect.first().toPriority()
            println(s1.intersect(s2))
        }
//        println(input)
//        println('a'.code)
//        println('z'.code)
//        println('A'.code)
//        println('Z'.code)
//        println('a'.toPriority())
//        println('z'.toPriority())
//        println('A'.toPriority())
//        println('Z'.toPriority())
        println(sum)
    }

    fun part2(input: List<String>): Unit {
        val ans = input.chunked(3).sumOf { window ->
            window.map { it.toSet() }.reduce { acc, chars ->
                acc.intersect(chars)
            }.first().toPriority()
        }
        println(ans)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day3_test")
    part1(testInput)
    part2(testInput)

    val input = readInput("day3_input")
//    part1(input)
//    part2(input)
}

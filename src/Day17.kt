
fun main() {
    fun part1(input: List<String>): Unit {
        val line = input.first()

        println(line.split(" "))
    }

    fun part2(input: List<String>): Unit {


    }

    val dayString = "day17"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
        part1(testInput)
//    part2(testInput)

    val input = readInput("${dayString}_input")
    //        part1(input)
//        part2(input)
}



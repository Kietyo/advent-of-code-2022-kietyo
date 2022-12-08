

fun main() {
    fun part1(input: List<String>): Unit {

//        val stacks = listOf(
//            "ZN", "MCD", "P"
//        ).map { it.toMutableList() }
        val stacks = listOf(
            "BZT", "VHTDN", "BFMD", "TJGWVQL", "WDGPVFQM", "VZQGHFS", "ZSNRLTCW", "ZHWDJNRM", "MQLFDS"
        ).map { it.toMutableList() }
        input.forEach { line ->
            val matchResult = line.split(" ")
            val count = matchResult[1].toInt()
            val stackIndex1 = matchResult[3].toInt() - 1
            val stackIndex2 = matchResult[5].toInt() - 1
            repeat(count) {
                stacks[stackIndex2].add(stacks[stackIndex1].removeLast())
            }
        }

        println(stacks.map { it.last() }.joinToString(""))

        println(stacks)
    }

    fun part2(input: List<String>): Unit {
//        val stackString = """
//,D,
//N,C,
//Z,M,P
//        """.trimIndent()
        val stacks = listOf(
            "BZT", "VHTDN", "BFMD", "TJGWVQL", "WDGPVFQM", "VZQGHFS", "ZSNRLTCW", "ZHWDJNRM", "MQLFDS"
        ).map { it.toMutableList() }
        println(stacks)

        val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
        input.forEach {
            val matchResult = regex.matchEntire(it)
            val (_, count, stackIndex1, stackIndex2) = matchResult!!.groupValues.map { it.toIntOrNull() ?: 0 }
            println("count: $count, stackIndex1: $stackIndex1, stackIndex2: $stackIndex2")
            val tempStack = mutableListOf<Char>()
            repeat(count) {
                val e = stacks[stackIndex1 - 1].removeLast()
                tempStack.add(e)
            }
            repeat(count) {
                stacks[stackIndex2 - 1].add(tempStack.removeLast())
            }
        }

        println(stacks.map { it.last() }.joinToString(""))
    }

    val dayString = "day5"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
//    part2(testInput)

    val input = readInput("${dayString}_input")
    part1(input)
    part2(input)
}

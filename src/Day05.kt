

fun main() {
    fun part1(input: List<String>): Unit {
//        val stackString = """
//,D,
//N,C,
//Z,M,P
//        """.trimIndent()
        val stackString = """
,,,,M,,W,M,
,,,L,Q,S,C,R,
,,,Q,F,F,T,N,S
,N,,V,V,H,L,J,D
,D,D,W,P,G,R,D,F
T,T,M,G,G,Q,N,W,L
Z,H,F,J,D,Z,S,H,Q
B,V,B,T,W,V,Z,Z,M
        """.trimIndent()

        val split = stackString.split("\n").reversed().map { it.split(",") }
        val stacks: List<MutableList<String>> = mutableListOf<MutableList<String>>().run {
            repeat(split.first().size) {
                add(mutableListOf())
            }
            this
        }

        split.forEach {
            println(it)
            it.forEachIndexed { index, s ->
                if (s.isNotEmpty()) {
                    stacks[index].add(s)
                }
            }
        }
        println(stacks)

        val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
        input.forEach {
            val matchResult = regex.matchEntire(it)
            val (_, count, stackIndex1, stackIndex2) = matchResult!!.groupValues.map { it.toIntOrNull() ?: 0 }
            println("count: $count, stackIndex1: $stackIndex1, stackIndex2: $stackIndex2")
            repeat(count) {
                val e = stacks[stackIndex1 - 1].removeLast()
                stacks[stackIndex2 - 1].add(e)
            }
        }

        println(stacks.map { it.last() }.joinToString(""))

        println(split)
    }

    fun part2(input: List<String>): Unit {
//        val stackString = """
//,D,
//N,C,
//Z,M,P
//        """.trimIndent()
        val stackString = """
,,,,M,,W,M,
,,,L,Q,S,C,R,
,,,Q,F,F,T,N,S
,N,,V,V,H,L,J,D
,D,D,W,P,G,R,D,F
T,T,M,G,G,Q,N,W,L
Z,H,F,J,D,Z,S,H,Q
B,V,B,T,W,V,Z,Z,M
        """.trimIndent()

        val split = stackString.split("\n").reversed().map { it.split(",") }
        val stacks: List<MutableList<String>> = mutableListOf<MutableList<String>>().run {
            repeat(split.first().size) {
                add(mutableListOf())
            }
            this
        }

        split.forEach {
            println(it)
            it.forEachIndexed { index, s ->
                if (s.isNotEmpty()) {
                    stacks[index].add(s)
                }
            }
        }
        println(stacks)

        val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
        input.forEach {
            val matchResult = regex.matchEntire(it)
            val (_, count, stackIndex1, stackIndex2) = matchResult!!.groupValues.map { it.toIntOrNull() ?: 0 }
            println("count: $count, stackIndex1: $stackIndex1, stackIndex2: $stackIndex2")
            val tempStack = mutableListOf<String>()
            repeat(count) {
                val e = stacks[stackIndex1 - 1].removeLast()
                tempStack.add(e)
            }
            repeat(count) {
                stacks[stackIndex2 - 1].add(tempStack.removeLast())
            }
        }

        println(stacks.map { it.last() }.joinToString(""))

        println(split)
    }

    val dayString = "day5"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
//    part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
    part2(input)
}

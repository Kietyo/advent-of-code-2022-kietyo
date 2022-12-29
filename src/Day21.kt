import java.text.NumberFormat

fun main() {

    fun part1(input: List<String>): Unit {
        val monkeyMathWorld = mutableMapOf<String, () -> Long>()
        input.forEach { line ->
            val (monkeyName, calcString) = line.split(": ")
            val calcSplit = calcString.split(" ")
            when (calcSplit.size) {
                1 -> monkeyMathWorld[monkeyName] = { calcSplit[0].toLong() }
                3 -> {
                    val leftCalc = { monkeyMathWorld[calcSplit[0]]!!() }
                    val rightCalc = { monkeyMathWorld[calcSplit[2]]!!() }
                    monkeyMathWorld[monkeyName] = when (calcSplit[1]) {
                        "+" -> { { leftCalc() + rightCalc() } }
                        "-" -> { { leftCalc() - rightCalc() } }
                        "*" -> { { leftCalc() * rightCalc() } }
                        "/" -> { { leftCalc() / rightCalc() } }
                        else -> TODO()
                    }
                }
            }
        }
        println("root: " + monkeyMathWorld["root"]!!())
    }

    fun part2(input: List<String>): Unit {
        val monkeyMathWorld = mutableMapOf<String, () -> Long>()
        input.forEach {
            val line = it
            val (monkeyName, calcString) = line.split(": ")
            val calcSplit = calcString.split(" ")
            when (calcSplit.size) {
                1 -> monkeyMathWorld[monkeyName] = { calcSplit[0].toLong() }
                3 -> {
                    val leftCalc = { monkeyMathWorld[calcSplit[0]]!!() }
                    val rightCalc = { monkeyMathWorld[calcSplit[2]]!!() }
                    monkeyMathWorld[monkeyName] = when (calcSplit[1]) {
                        "+" -> { { leftCalc() + rightCalc() } }
                        "-" -> { { leftCalc() - rightCalc() } }
                        "*" -> { { leftCalc() * rightCalc() } }
                        "/" -> { { leftCalc() / rightCalc() } }
                        else -> TODO()
                    }
                }
            }
        }

        val rootLeft = "bjgs"
        val rootRight = "tjtt"
        var delta = 1000000000
        var currComp = monkeyMathWorld[rootLeft]!!().compareTo(monkeyMathWorld[rootRight]!!())
        while (true) {
            val leftRootCalc = monkeyMathWorld[rootLeft]!!()
            val rightRootCalc = monkeyMathWorld[rootRight]!!()
            val compareTo = leftRootCalc.compareTo(rightRootCalc)
            val humnCalc = monkeyMathWorld["humn"]!!()
            println(
                "leftRootCalc: ${
                    NumberFormat.getInstance().format(leftRootCalc)
                }, rightRootCalc: ${
                    NumberFormat.getInstance().format(rightRootCalc)
                }, humnCalc: $humnCalc, compareTo: $compareTo, delta: $delta"
            )
            if (leftRootCalc == rightRootCalc) break
            if (currComp != compareTo) {
                currComp = compareTo
                delta /= 10
            }
            monkeyMathWorld["humn"] = { humnCalc + (delta * compareTo) }
        }

    }

    val dayString = "day21"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test") //        part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input) // 3330805295851 too high
    // Actually answer is 3330805295850, likely due to integer division rounding.
        part2(input)
}



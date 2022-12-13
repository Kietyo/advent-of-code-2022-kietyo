import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

fun main() {

    fun part1(input: List<String>): Unit {
        var currCycle = 1
        var xRegister = 1
        val cyclesToRecord = setOf(
            20, 60, 100, 140, 180, 220
        )
        val cycleToSignal = mutableMapOf<Int, Int>()
        input.forEach {
            println(it)
            var numToAddToRegister = 0
            val numCyclesToIncrement = when {
                it == "noop" -> 1
                else -> {
                    val (_, num) = it.split(" ")
                    numToAddToRegister = num.toInt()
                    2
                }
            }
            repeat(numCyclesToIncrement) {
                currCycle++
                if (it == 1) {
                    xRegister += numToAddToRegister
                }
                if (cyclesToRecord.contains(currCycle)) {
                    println("currCycle: $currCycle, xRegister: $xRegister")
                    cycleToSignal[currCycle] = xRegister * currCycle
                }
            }
        }
        println(cycleToSignal)
        println(cycleToSignal.values.sum())
        println("total cycles: $currCycle")
    }

    fun part2(input: List<String>): Unit {
        var currCycle = 1
        var xRegister = 1
        val lines = mutableListOf<String>()
        var currentLine = ""
        input.forEach {
            println(it)
            var numToAddToRegister = 0
            val numCyclesToIncrement = when {
                it == "noop" -> 1
                else -> {
                    val (_, num) = it.split(" ")
                    numToAddToRegister = num.toInt()
                    2
                }
            }
            repeat(numCyclesToIncrement) {
                currCycle++
                if (it == 1) {
                    xRegister += numToAddToRegister
                }

                val currLineIndex = (currCycle - 1) % 40
                currentLine += if (currLineIndex in (xRegister-1)..(xRegister+1)) {
                    "#"
                } else { "." }
                if (currLineIndex % 40 == 0) {
                    lines.add(currentLine)
                    currentLine = ""
                }

            }
        }
        println(lines.joinToString("\n"))
        println(lines.map { it.length })
        println("total cycles: $currCycle")
    }

    val dayString = "day10"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//        part1(testInput)
            part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
//            part2(input)
    // RZEKEFHA
}

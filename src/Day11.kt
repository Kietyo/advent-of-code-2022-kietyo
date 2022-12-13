import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

fun main() {

    data class Monkey(
        val items: MutableList<BigInteger>,
        val op: (old: BigInteger) -> BigInteger,
        val testDivisibleNum: BigInteger,
        val testMonkeyTrue: Int,
        val testMonkeyFalse: Int,
        var numItemsInspected: Long = 0
    )

    fun parseMonkeys(input: List<String>): MutableList<Monkey> {
        println(input)
        val seq = input.iterator()
        val monkeys = mutableListOf<Monkey>()
        while (seq.hasNext()) {
            seq.next() // monkey line
            val items = seq.next().split(" ", limit = 5).last().split(", ").map { it.toBigInteger() }.toMutableList()
            println(items)
            val opSplit = seq.next().split(" ", limit = 4).last().split(" ")
            println(opSplit)
            val data = opSplit[4]
            val opFn = when (opSplit[3]) {
                "*" -> {it: BigInteger -> it * when (data) {
                    "old" -> it
                    else -> data.toBigInteger()
                } }
                "+" -> {it: BigInteger -> it + data.toBigInteger()}
                else -> TODO()
            }
            val testDivisibleNum = seq.next().split(" ", limit = 6).last().toBigInteger()
            val testMonkeyTrue = seq.next().split(" ").last().toInt()
            val testMonkeyFalse = seq.next().split(" ").last().toInt()
            val monkey = Monkey(items, opFn, testDivisibleNum, testMonkeyTrue, testMonkeyFalse)
            monkeys.add(monkey)
            println(monkey)
            seq.next() // Eat new line
        }
        println("Done parsing monkeys")
        return monkeys
    }

    fun part1(input: List<String>): Unit {
        val monkeys = parseMonkeys(input)

        fun runRound() {
            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    val worryLevelStep1 = monkey.op(it)
                    val worryLevelStep2 = (worryLevelStep1 / BigInteger("3"))
                    if (worryLevelStep2 % monkey.testDivisibleNum == BigInteger.ZERO) {
                        monkeys[monkey.testMonkeyTrue].items.add(worryLevelStep2)
                    } else {
                        monkeys[monkey.testMonkeyFalse].items.add(worryLevelStep2)
                    }
                    monkey.numItemsInspected++
                }
                monkey.items.clear()
            }
            println(monkeys.joinToString("\n"))
        }

        repeat(20) {
            runRound()
            println()
        }

        println(monkeys.map { it.numItemsInspected }.sorted().takeLast(2).reduce { acc, i -> acc * i })
    }

    fun part2(input: List<String>): Unit {
        val monkeys = parseMonkeys(input)
        val blah = monkeys.map { it.testDivisibleNum }.reduce { acc, bigInteger -> acc * bigInteger }

        fun runRound() {
            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    val worryLevelStep1 = monkey.op(it)
                    val worryLevelStep2 = worryLevelStep1 % blah
                    if (worryLevelStep2 % monkey.testDivisibleNum == BigInteger.ZERO) {
                        monkeys[monkey.testMonkeyTrue].items.add(worryLevelStep2)
                    } else {
                        monkeys[monkey.testMonkeyFalse].items.add(worryLevelStep2)
                    }
                    monkey.numItemsInspected++
                }
                monkey.items.clear()
            }
//            println(monkeys.joinToString("\n"))
        }

        repeat(10000) {
            runRound()
            println(it)
        }

        println(monkeys.map { it.numItemsInspected }.sorted().takeLast(2).reduce { acc, i -> acc * i })
    }

    val dayString = "day11"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//        part1(testInput)
//            part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
            part2(input)
}

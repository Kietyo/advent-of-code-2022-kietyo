import java.text.NumberFormat

fun main() {
    fun part1(input: List<String>): Unit {
        val monkeyMathWorld = mutableMapOf<
                String,
                    () -> Long>()

        input.forEach {
            val line = it
            val (monkeyName, calcString) = line.split(": ")
            val calcSplit = calcString.split(" ")
            println(monkeyName)
            println(calcSplit)
            when (calcSplit.size) {
                1 -> {
                    monkeyMathWorld[monkeyName] = { calcSplit[0].toLong() }
                }

                3 -> {
                    monkeyMathWorld[monkeyName] =
                        when (calcSplit[1]) {
                            "+" -> {
                                { monkeyMathWorld[calcSplit[0]]!!() + monkeyMathWorld[calcSplit[2]]!!() }
                            }

                            "-" -> {
                                { monkeyMathWorld[calcSplit[0]]!!() - monkeyMathWorld[calcSplit[2]]!!() }
                            }

                            "*" -> {
                                { monkeyMathWorld[calcSplit[0]]!!() * monkeyMathWorld[calcSplit[2]]!!() }
                            }

                            "/" -> {
                                { monkeyMathWorld[calcSplit[0]]!!() / monkeyMathWorld[calcSplit[2]]!!() }
                            }

                            else -> TODO()
                        }

                }
            }
        }

        println("root: " + monkeyMathWorld["root"]!!())

    }

    fun log(
        monkeyMathWorld: MutableMap<String, () -> Long>,
        calcSplit: List<String>
    ) {
        if (calcSplit.size < 3) return
        //        val (left, op, right) = calcSplit
        //        if (left == "humn" || right == "humn") {
        //            val leftCalc = monkeyMathWorld[left]!!()
        //            val rightCalc = monkeyMathWorld[right]!!()
        //            println(calcSplit)
        //            println("leftCalc: $leftCalc, rightCalc: $rightCalc")
        //        }
    }

    fun part2(input: List<String>): Unit {
        val monkeyMathWorld = mutableMapOf<
                String,
                    () -> Long>()

        val rootLeft = "bjgs"
        val rootRight = "tjtt"

        input.forEach {
            val line = it
            val (monkeyName, calcString) = line.split(": ")
            val calcSplit = calcString.split(" ")
            println(monkeyName)
            println(calcSplit)
            when (calcSplit.size) {
                1 -> {
                    monkeyMathWorld[monkeyName] = { calcSplit[0].toLong() }
                }

                3 -> {
                    if (monkeyName == "root") {
                        monkeyMathWorld[monkeyName] = {
                            if (monkeyMathWorld[calcSplit[0]]!!() == monkeyMathWorld[calcSplit[2]]!!()) 1L else 0L
                        }
                    } else {
                        monkeyMathWorld[monkeyName] =
                            when (calcSplit[1]) {
                                "+" -> {
                                    {
                                        log(monkeyMathWorld, calcSplit)
                                        monkeyMathWorld[calcSplit[0]]!!() + monkeyMathWorld[calcSplit[2]]!!()
                                    }
                                }

                                "-" -> {
                                    {
                                        log(monkeyMathWorld, calcSplit)
                                        monkeyMathWorld[calcSplit[0]]!!() - monkeyMathWorld[calcSplit[2]]!!()
                                    }
                                }

                                "*" -> {
                                    {
                                        log(monkeyMathWorld, calcSplit)
                                        monkeyMathWorld[calcSplit[0]]!!() * monkeyMathWorld[calcSplit[2]]!!()
                                    }
                                }

                                "/" -> {
                                    {
                                        log(monkeyMathWorld, calcSplit)
                                        monkeyMathWorld[calcSplit[0]]!!() / monkeyMathWorld[calcSplit[2]]!!()
                                    }
                                }

                                else -> TODO()
                            }
                    }
                }
            }
        }

                monkeyMathWorld["humn"] = { 3330805295850 }

        var delta = 1000000000
        var currComp = monkeyMathWorld[rootLeft]!!().compareTo(monkeyMathWorld[rootRight]!!())

        repeat(1) {
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
//            if (leftRootCalc == rightRootCalc) break
            if (currComp != compareTo) {
                currComp = compareTo
                delta /= 10
            }
            monkeyMathWorld["humn"] = { humnCalc + (delta * compareTo) }
        }

        //        println("root: " + monkeyMathWorld["root"]!!())
    }

    val dayString = "day21"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    //    part1(input)
    // 3330805295851 too high
    // Actually answer is 3330805295850, likely due to integer division rounding.
    part2(input)
}



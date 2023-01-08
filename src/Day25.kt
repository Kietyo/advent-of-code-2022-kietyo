import kotlin.math.absoluteValue

fun Long.pow(num: Int): Long {
    var curr = 1L
    repeat(num) {
        curr *= this
    }
    return curr
}

fun calculateMaxSnafu(n: Int): Long {
    var sum = 0L
    for (i in 0 until n) {
        val currPosition = i
        val fiveOrder = 5L.pow(currPosition)
        sum += fiveOrder * 2
    }
    return sum
}

fun calculateMinSnafu(n: Int): Long {
    var sum = 0L
    for (i in 0 until n) {
        val currPosition = i
        val fiveOrder = 5L.pow(currPosition)
        if (i == n - 1) {
            sum += fiveOrder
        } else {
            sum += fiveOrder * (-2)
        }
    }
    return sum
}

fun String.convertFromSnafu(): Long {
    val chars = this.toCharArray()
    println("Processing: " + this)
    println("chars.indices: " + chars.indices)
    var sum = 0L
    for (i in 0 until chars.size) {
        val currPosition = chars.size - i - 1
        val fiveOrder = 5L.pow(currPosition)
        val currChar = chars[i]
        val num = when {
            currChar == '-' -> -1
            currChar == '=' -> -2
            currChar == '0' -> 0
            currChar == '1' -> 1
            currChar == '2' -> 2
            else -> TODO("Unsupported char: $currChar")
        }.toLong()
        println("currChar:$currChar, currPosition: $currPosition, fiveOrder: $fiveOrder, calc: ${fiveOrder * num}")
        sum += fiveOrder * num
    }
    return sum
}

data class SnafuConfig(
    val n: Int,
    val minSnafu: Long,
    val maxSnafu: Long,
) {
    val fiverOrder get() = 5L.pow(n)
    val fiverOrderTimesTwo get() = 5L.pow(n) * 2
}

fun getSnafuConfigForNum(num: Long): SnafuConfig {
    var n = 0
    while (true) {
        val min = calculateMinSnafu(n)
        val max = calculateMaxSnafu(n)
        if (num >= min && num <= max) {
            val fiveOrder = 5L.pow(n)
            return SnafuConfig(n, min, max)
        }
        n++
    }
}

fun Long.convertToSnafu2(): String {
    val snafuConfig = getSnafuConfigForNum(this)
    var currentSnafuLength = snafuConfig.n - 1
    if (currentSnafuLength == -1) return "0"
    println("snafuLength: $currentSnafuLength")
    val sb = StringBuilder()
    var currNum = this

    while (currentSnafuLength >= 0) {
        val minSnafuAbove = calculateMinSnafu(currentSnafuLength + 1)
        val maxSnafuAbove = calculateMaxSnafu(currentSnafuLength + 1)

        val abs = currNum.absoluteValue

        val minSnafu = calculateMinSnafu(currentSnafuLength)
        val maxSnafu = calculateMaxSnafu(currentSnafuLength)

        val withinRange = abs == 0L || abs in minSnafuAbove..maxSnafuAbove || abs in minSnafu..maxSnafu
//        if (!withinRange) {
//
//        }

//        require(withinRange) {
//            "Error when processing: $this"
//        }

        val fiveOrder = 5L.pow(currentSnafuLength)
        val fiveOrderTimes2 = fiveOrder * 2
        val chosenNumber = when {
            !withinRange -> 0
            currNum < 0 -> {
                when {
                    abs >= fiveOrderTimes2 -> -2
                    (abs + maxSnafu) >= fiveOrderTimes2 -> -2
                    (abs + maxSnafu) >= fiveOrder -> -1
                    abs >= fiveOrder -> -1
                    else -> 0
                }
            }
            currNum > 0 -> when {
                currNum >= fiveOrderTimes2 -> 2
                (currNum + maxSnafu) >= fiveOrderTimes2 -> 2
                currNum >= fiveOrder -> 1
                (currNum + maxSnafu) >= fiveOrder -> 1
                else -> 0
            }
            else -> 0
        }

        when (chosenNumber) {
            2 -> sb.append('2')
            1 -> sb.append('1')
            0 -> sb.append('0')
            -1 -> sb.append('-')
            -2 -> sb.append('=')
            else -> TODO()
        }

        val currNumBefore = currNum

        currNum -= fiveOrder * chosenNumber
        println("currentSnafuLength: $currentSnafuLength, chosenNumber: $chosenNumber, currNumBefore: $currNumBefore, currNumAfter: $currNum")

        currentSnafuLength--
    }

    return sb.toString()
}

fun main() {
    fun part1(input: List<String>): Unit {
        val line = input.first()

        println(line)
        println(line.convertFromSnafu())

        val snafus = input.map { it.convertFromSnafu() }
        println(snafus.joinToString("\n"))
        println("sum:")
        println(snafus.sum())
        println(snafus.sum().convertToSnafu2())

        // 2=0=== is wrong

        println()
    }

    fun part2(input: List<String>): Unit {


    }

    val dayString = "day25"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//        part1(testInput)
//    part2(testInput)

    val input = readInput("${dayString}_input")
    // 33658310202841 - didn't work
//            part1(input)
//        part2(input)

    repeat(22) {
        println("n: $it")
        println("min snafu: " + calculateMinSnafu(it))
        println("max snafu: " + calculateMaxSnafu(it))
    }
//
    for (it in 122..256) {
        println("it: $it")
        println(it.toLong().convertToSnafu2())
        println()
    }

//    println("2=-01".convertFromSnafu())

    println(33658310202841.convertToSnafu2())
}



import java.lang.Exception
import kotlin.math.absoluteValue
import kotlin.math.sign

data class CircularArrayElement(
    var idx: Int,
    val data: Long
)

@JvmInline
value class CircularArray(
    val data: Array<CircularArrayElement>
) {
    private fun normalizeIndex(idx: Int): Int {
        val mod = idx % data.size
        return if (mod < 0) mod + data.size else mod
    }

    operator fun get(idx: Int) = data[normalizeIndex(idx)]

    fun swap(idx1: Int, idx2: Int) {
        val circularIdx1 = normalizeIndex(idx1)
        val circularIdx2 = normalizeIndex(idx2)
        try {
            val t = data[circularIdx1]

            data[circularIdx1] = data[circularIdx2]
            data[circularIdx1].idx = circularIdx1

            data[circularIdx2] = t
            t.idx = circularIdx2
        } catch (e: Exception) {
            println("Failed with: idx1: $idx1, idx2: $idx2")
            throw e
        }

    }
}

fun main() {
    fun part1(input: List<String>): Unit {
        val originalNums = input.map { it.toLong() }
        val originalNumsCircularArrayElements = originalNums.mapIndexed { index, i ->
            CircularArrayElement(index, i)
        }
        val circularArray = CircularArray(originalNumsCircularArrayElements.toTypedArray())
        println("size: ${originalNums.size}")
        println(circularArray)

        for (circularArrayElement in originalNumsCircularArrayElements) {
            val sign = circularArrayElement.data.sign
            var idx = circularArrayElement.idx

            //            println("$it: processing: $currNum, sign: $sign")

            repeat((circularArrayElement.data.absoluteValue % (originalNums.size * originalNums.size)).toInt()) {
                circularArray.swap(idx, idx + sign)
                idx += sign
            }
        }

        println(circularArray)

        val zeroIdx = originalNumsCircularArrayElements.first {
            it.data == 0L
        }.idx

        val nums = listOf(
            circularArray[zeroIdx + 1000],
            circularArray[zeroIdx + 2000],
            circularArray[zeroIdx + 3000],
        ).map { it.data }

        println(nums)
        println(nums.sum())

    }

    fun part2(input: List<String>): Unit {
        val originalNums = input.map { it.toLong() * 811589153L }
        val originalNumsCircularArrayElements = originalNums.mapIndexed { index, i ->
            CircularArrayElement(index, i)
        }
        val circularArray = CircularArray(originalNumsCircularArrayElements.toTypedArray())
        println("size: ${originalNums.size}")
        println(circularArray)

        repeat(10) {
            println("Finished $it")
            for (circularArrayElement in originalNumsCircularArrayElements) {
                val sign = circularArrayElement.data.sign
                var idx = circularArrayElement.idx

                //            println("$it: processing: $currNum, sign: $sign")

                repeat((circularArrayElement.data.absoluteValue %
                        (originalNums.size.dec() * originalNums.size)).toInt()) {
                    circularArray.swap(idx, idx + sign)
                    idx += sign
                }
            }
        }


        println(circularArray)

        val zeroIdx = originalNumsCircularArrayElements.first {
            it.data == 0L
        }.idx

        val nums = listOf(
            circularArray[zeroIdx + 1000],
            circularArray[zeroIdx + 2000],
            circularArray[zeroIdx + 3000],
        ).map { it.data }

        println(nums)
        println(nums.sum())

    }

    val dayString = "day20"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //        part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    // -597 is wrong
    // 7228 is right
    //            part1(input)
    part2(input)
}



import kotlin.math.sign

sealed class Packet {
    data class PList(
        val data: List<Packet>
    ) : Packet()

    data class PNumber(
        val value: Int
    ) : Packet()
}

sealed interface FinalToken {
    fun compareToo3(other: FinalToken): Int
}

sealed class Token {
    object LBracket : Token()
    object RBracket : Token()
    object Comma : Token()
    data class Number(val value: Int) : Token(), FinalToken {
        override fun compareToo3(other: FinalToken): Int {
            return when (other) {
                is Number -> value.compareTo(other.value)
                is TList -> TList(listOf(this)).compareToo3(other)
            }
        }
    }
    data class TList(val values: List<FinalToken>) : Token(), FinalToken {
        // Returns true if in the right order
        fun compareToo(other: TList): Boolean {
            val thisSequence = getSequence().toList().iterator()
            val otherSequence = other.getSequence().toList().iterator()
            while (thisSequence.hasNext() || otherSequence.hasNext()) {
                if (!otherSequence.hasNext()) return false
                if (!thisSequence.hasNext()) return true
                val thisE = thisSequence.next().value
                val otherE = otherSequence.next().value
                if (thisE < otherE) {
                    return true
                }
                if (thisE > otherE) {
                    return false
                }
            }
            return false
        }

        fun compareToo2(other: TList): Boolean {
            val thisItrStack = mutableListOf<ListIterator<FinalToken>>(
                values.listIterator()
            )
            val otherItrStack = mutableListOf<ListIterator<FinalToken>>(
                other.values.listIterator()
            )

            while (thisItrStack.isNotEmpty() || otherItrStack.isNotEmpty()) {
                var currThisItr = thisItrStack.removeLast()
                var currOtherItr = otherItrStack.removeLast()
                while (currThisItr.hasNext() || currOtherItr.hasNext()) {
                    if (!currOtherItr.hasNext()) return false
                    if (!currThisItr.hasNext()) return true
                    val thisE = currThisItr.next()
                    val otherE = currOtherItr.next()
                    when {
                        thisE is Number && otherE is Number -> {
                            if (thisE.value < otherE.value) {
                                return true
                            }
                            if (thisE.value > otherE.value) {
                                return false
                            }
                        }
                        thisE is TList && otherE is TList -> {
                            thisItrStack.add(currThisItr)
                            otherItrStack.add(currOtherItr)
                            currThisItr = thisE.values.listIterator()
                            currOtherItr = otherE.values.listIterator()
                        }
                        thisE is Number -> {
                            thisItrStack.add(currThisItr)
                            currThisItr = listOf(thisE).listIterator()
                            currOtherItr.previous()

                            thisItrStack.add(currThisItr)
                            otherItrStack.add(currOtherItr)
                            currThisItr = listOf(thisE).listIterator()
                            currOtherItr = (otherE as TList).values.listIterator()
                        }
                        otherE is Number -> {
                            thisItrStack.add(currThisItr)
                            otherItrStack.add(currOtherItr)
                            currThisItr = (thisE as TList).values.listIterator()
                            currOtherItr = listOf(otherE).listIterator()
                        }
                        else -> TODO()
                    }
                }
            }

            require(thisItrStack.isEmpty() && otherItrStack.isEmpty())
            return false
        }

        override fun compareToo3(other: FinalToken): Int {
            when (other) {
                is Number -> return compareToo3(TList(listOf(other)))
                is TList -> {
                    for (i in 0 until minOf(values.size, other.values.size)) {
                        val compared = values[i].compareToo3(other.values[i])
                        if (compared == -1) {
                            return -1
                        }
                        if (compared == 1) {
                            return 1
                        }
                    }
                    return (values.size - other.values.size).sign
                }
            }
        }

        fun getSequence(): Sequence<Number> {
            return sequence<Number> {
                values.forEach {
                    when (it) {
                        is Number -> yield(it)
                        is TList -> yieldAll(it.getSequence())
                    }
                }
            }
        }
    }
}

fun String.convertToToken(): Token.TList {
    val itr = toList().listIterator()
    val stack = mutableListOf<Token>()
    while (itr.hasNext()) {
        var currChar = itr.next()
        when (currChar) {
            '[' -> stack.add(Token.LBracket)
            in '0'..'9' -> {
                val currNumber = mutableListOf<Char>()
                while (currChar in '0'..'9') {
                    currNumber.add(currChar)
                    currChar = itr.next()
                }
                itr.previous()
                stack.add(Token.Number(currNumber.joinToString("").toInt()))
            }

            ']' -> {
                //                println("stack start: $stack")
                val currList = mutableListOf<FinalToken>()
                while (stack.last() != Token.LBracket) {
                    val token = stack.removeLast()
                    require(token is FinalToken)
                    currList.add(token)
                }
                stack.removeLast()
                currList.reverse()
                stack.add(Token.TList(currList))
                //                println("parsed list: $currList")
                //                println("stack finish: $stack")
            }

            ',' -> Unit
        }
    }
    require(stack.first() is Token.TList)
    return stack.first() as Token.TList
}

fun main() {
    fun part1(input: List<String>): Unit {
        val itr = input.iterator()
        var index = 0
        var sum = 0
        while (itr.hasNext()) {
            index++
            val firstLine = itr.next()
            val secondLine = itr.next()
            val first = firstLine.convertToToken()
            val second = secondLine.convertToToken()
            itr.next()
            val compare1 = first.compareToo2(second)
            val compare2 = first.compareToo3(second)
            println("index: $index, isInRightOrder: $compare2")
            println("first : $firstLine")
            println("second: $secondLine")
            println()
            if (compare2 == -1) {
                sum += index
            }
            if (compare1 && compare2 != -1) {
                TODO()
            }
        }
        println(sum)
    }

    fun part2(input: List<String>): Unit {
        val divider2 = Token.TList(listOf(Token.TList(listOf(Token.Number(2)))))
        val divider6 = Token.TList(listOf(Token.TList(listOf(Token.Number(6)))))
        val tokens = mutableListOf<Token.TList>(
            divider2,
            divider6
        )
        val itr = input.iterator()
        while (itr.hasNext()) {
            val first = itr.next().convertToToken()
            val second = itr.next().convertToToken()
            itr.next()
            tokens.add(first)
            tokens.add(second)
        }
        val sorted = tokens.sortedWith(object : Comparator<Token.TList> {
            override fun compare(o1: Token.TList, o2: Token.TList): Int {
                return o1.compareToo3(o2)
            }
        })
        println(sorted.joinToString("\n"))
        println(sorted.indexOf(divider2) + 1)
        println(sorted.indexOf(divider6) + 1)
    }

    val dayString = "day13"


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//        part1(testInput)
//                    part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
                    part2(input)
}


enum class Result {
    WIN,
    LOSE,
    DRAW
}

sealed interface Option {
    object Rock : Option {
        override val points: Int = 1
        override fun battle(other: Option): Result {
            return when (other) {
                Paper -> Result.LOSE
                Rock -> Result.DRAW
                Scissors -> Result.WIN
            }
        }
    }
    object Scissors : Option {
        override val points: Int = 3
        override fun battle(other: Option): Result {
            return when(other) {
                Paper -> Result.WIN
                Rock -> Result.LOSE
                Scissors -> Result.DRAW
            }
        }
    }

    object Paper : Option {
        override val points: Int = 2
        override fun battle(other: Option): Result {
            return when(other) {
                Paper -> Result.DRAW
                Rock -> Result.WIN
                Scissors -> Result.LOSE
            }
        }
    }

    fun battle(other: Option): Result
    fun getOtherOptionToGetResult(result: Result): Option {
        return ALL.first {
            it.battle(this) == result
        }
    }
    abstract val points: Int
    companion object {
        val ALL = listOf(Rock, Paper, Scissors)
    }
}

fun String.toOption(): Option {
    return when {
        this == "A" || this == "X" -> Option.Rock
        this == "B" || this == "Y"-> Option.Paper
        this == "C" || this == "Z"-> Option.Scissors
        else -> TODO("This string isn't supported: ${this}")
    }
}

fun String.toResult(): Result {
    return when {
        this == "X" -> Result.LOSE
        this == "Y"-> Result.DRAW
        this == "Z"-> Result.WIN
        else -> TODO("This string isn't supported: ${this}")
    }
}


fun main() {
    fun part1(input: List<String>): Unit {
        var myTotalScore = 0
        for (line in input) {
            val (otherOption, myOption) = line.split(" ").map { it.toOption() }
            println("otherOption: $otherOption, myOption: $myOption")
            val result = myOption.battle(otherOption)
            myTotalScore += when (result) {
                Result.WIN -> 6
                Result.LOSE -> 0
                Result.DRAW -> 3
            } + myOption.points
        }
        println(myTotalScore)
    }

    fun part2(input: List<String>): Unit {
        var myTotalScore = 0
        for (line in input) {
            val split = line.split(" ")
            val otherOption = split[0].toOption()
            val wantedResult = split[1].toResult()
            val myOption = otherOption.getOtherOptionToGetResult(wantedResult)
            println("otherOption: $otherOption, wantedResult: $wantedResult, myOption: $myOption")
            myTotalScore += when (wantedResult) {
                Result.WIN -> 6
                Result.LOSE -> 0
                Result.DRAW -> 3
            } + myOption.points
        }
        println(myTotalScore)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day2_test")
    part1(testInput)
    part2(testInput)

    val input = readInput("day2_input")
//    part1(input)
//    part2(input)
}

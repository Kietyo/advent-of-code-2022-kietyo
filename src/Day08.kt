import kotlin.math.max



fun main() {
    fun part1(input: List<String>): Unit {
        val height = input.size
        val width = input.first().length
        val grid = input.map { it.map { it.digitToInt() } }
        var numVisible = 0

        for (h in 0 until height) {
            for (w in 0 until width) {
                val curr = grid[h][w]
                if (w == 0) {
                    numVisible++
                } else if (h == 0) {
                    numVisible++
                } else if (w == width - 1) {
                    numVisible++
                } else if (h == height - 1) {
                    numVisible++
                } else {
                    val columnValues = grid.map { it[w] }
                    val leftVisible = grid[h].take(w).all { it < curr }
                    val rightVisible = grid[h].drop(w + 1).all { it < curr }
                    val topVisible = columnValues.take(h).all { it < curr }
                    val bottomVisible = columnValues.drop(h + 1).all { it < curr }
                    if (leftVisible || rightVisible || topVisible || bottomVisible) {
                        numVisible++
                    }
                }
            }
        }
        println(numVisible)
    }

    fun part2(input: List<String>): Unit {
        fun <T> List<T>.indexOfLastSequential(predicate: (T) -> Boolean): Int {
            val iterator = this.listIterator()
            while (iterator.hasNext()) {
                if (!predicate(iterator.next())) {
                    return iterator.previousIndex()
                }
            }
            return size - 1
        }

        val height = input.size
        val width = input.first().length
        var highestScenicScore = 0
        val grid = input.map { it.map { it.digitToInt() } }
        println(grid)

        for (h in 0 until height) {
            for (w in 0 until width) {
                val curr = grid[h][w]
                val columnValues = grid.map { it[w] }
                val leftIndex = grid[h].take(w).reversed().indexOfLastSequential {
                    it < curr
                }
                val rightIndex = grid[h].drop(w + 1).indexOfLastSequential {
                    it < curr
                }
                val topIndex = columnValues.take(h).reversed().indexOfLastSequential {
                    it < curr
                }
                val bottomIndex = columnValues.drop(h + 1).indexOfLastSequential {
                    it < curr
                }
                val indexes = listOf(leftIndex, rightIndex, topIndex, bottomIndex)
                val scenicScore = indexes.map {
                    it + 1
                }.reduce { acc, i ->
                    acc * i
                }
                highestScenicScore = max(scenicScore, highestScenicScore)
            }
        }
        println(highestScenicScore)
    }

    val dayString = "day8"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
        part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
        part2(input)
}

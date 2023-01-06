data class ProposedElfMovements(
    val targetedSpots: Map<IntPoint, Int>,
    val elfToMovement: Map<IntPoint, IntPoint>
) {
    val isEmpty get() = targetedSpots.isEmpty()
}

class ElfWorld(val elves: MutableMap<Int, MutableSet<Int>>) {

    val leftX get() = elves.filter { it.value.isNotEmpty() }.minOf { it.key }
    val rightX get() = elves.filter { it.value.isNotEmpty() }.maxOf { it.key }
    val topY get() = elves.filter { it.value.isNotEmpty() }.minOf { it.value.minOf { it } }
    val bottomY get() = elves.filter { it.value.isNotEmpty() }.maxOf { it.value.maxOf { it } }

    fun print() {
        for (y in topY..bottomY) {
            for (x in leftX..rightX) {
                if (existsElf(x, y)) print('#') else print('.')
            }
            println()
        }
    }

    val elfLocations get() = elves.flatMap {entry ->
        entry.value.map { entry.key toip it }
    }

    fun existsElf(x: Int, y: Int): Boolean {
        return elves[x]?.contains(y)
            ?: false
    }

    private fun existsElvesAround(x: Int, y: Int): Boolean {
        return existsElf(x - 1, y - 1) ||
                existsElf(x, y - 1) ||
                existsElf(x + 1, y - 1) ||

                existsElf(x - 1, y) ||
                existsElf(x + 1, y) ||

                existsElf(x - 1, y + 1) ||
                existsElf(x, y + 1) ||
                existsElf(x + 1, y + 1)
    }

    private fun isEmptyInDirection(x: Int, y: Int, direction: Direction): Boolean {
        return when (direction) {
            Direction.RIGHT -> !existsElf(x + 1, y - 1) &&
                    !existsElf(x + 1, y) &&
                    !existsElf(x + 1, y + 1)

            Direction.LEFT -> !existsElf(x - 1, y - 1) &&
                    !existsElf(x - 1, y) &&
                    !existsElf(x - 1, y + 1)

            Direction.DOWN -> !existsElf(x - 1, y + 1) &&
                    !existsElf(x, y + 1) &&
                    !existsElf(x + 1, y + 1)

            Direction.UP -> !existsElf(x - 1, y - 1) &&
                    !existsElf(x, y - 1) &&
                    !existsElf(x + 1, y - 1)
        }
    }

    val directionsList = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
    var directionsOffset = 0

    fun calculateProposedMovements(): ProposedElfMovements {
        val targetedSpots = mutableMapOf<IntPoint, Int>()
        val elfToMovement = mutableMapOf<IntPoint, IntPoint>()
        elves.forEach xLoop@{ x, ySet ->
            ySet.forEach yLoop@{y ->
                if (!existsElvesAround(x, y)) return@yLoop

                val proposedNextLocation: IntPoint = kotlin.run {
                    for (i in 0 until 4) {
                        val currDirection = directionsList.getCyclic(i + directionsOffset)
                        if (isEmptyInDirection(x, y, currDirection)) {
                            return@run x + currDirection.movementOffset.x toip y + currDirection.movementOffset.y
                        }
                    }
                    null
                } ?: return@yLoop

                targetedSpots.compute(proposedNextLocation) { currKey, currValue ->
                    (currValue ?: 0) + 1
                }
                elfToMovement[x toip y] = proposedNextLocation
            }
        }
        directionsOffset++
        return ProposedElfMovements(targetedSpots, elfToMovement)
    }

    fun executeProposal(proposal: ProposedElfMovements) {
        proposal.elfToMovement.forEach { (elf, target) ->
            val countOfTarget = proposal.targetedSpots[target] ?: 1

            // Cannot move to the target because there's at least 2 elves targeting it
            if (countOfTarget > 1) return@forEach

            elves[elf.x]!!.remove(elf.y)

            elves.compute(target.x) { currX, currValue ->
                val s = (currValue ?: mutableSetOf())
                s.add(target.y)
                s
            }
        }
    }
}



fun main() {
    fun part1(input: List<String>): Unit {
        println(input.joinToString("\n"))

        val grid = Grid(input.map { it.toCharArray().toTypedArray() })

        val elfMap = mutableMapOf<Int, MutableSet<Int>>()

        grid.elements().filter {
            it.value == '#'
        }.forEach {
            elfMap.compute(it.x) {currKey, currValue ->
                val currSet = (currValue ?: mutableSetOf())
                currSet.add(it.y)
                currSet
            }
        }

        val elfWorld = ElfWorld(elfMap)

        println("elf locations: " + elfWorld.elfLocations)

        elfWorld.print()

        var it = 0
        while (true) {
            println("itr: " + it++)
            val proposal = elfWorld.calculateProposedMovements()
//            println(proposal)

            if (proposal.isEmpty) break

            elfWorld.executeProposal(proposal)

//            println("elf locations: " + elfWorld.elfLocations)
//            elfWorld.print()
            println()
        }

        var count = 0
        for (x in elfWorld.leftX..elfWorld.rightX) {
            for (y in elfWorld.topY..elfWorld.bottomY) {
                if (!elfWorld.existsElf(x, y)) count++
            }
        }

        println("count: " + count)

        elfWorld.print()
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day23"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
            part1(input)
    //        part2(input)
}



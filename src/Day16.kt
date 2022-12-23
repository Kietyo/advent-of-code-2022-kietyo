enum class Turn {
    MINE,
    ELEPHANT
}

fun main() {
    data class ValveNode(
        val name: String,
        val flowRate: Int,
        val connectedValves: List<String>
    )

    fun String.toValveNode(): ValveNode {
        val split = this.split(" ", limit = 10)
        val name = split[1]
        val flowRate = split[4].dropLast(1).split("=")[1].toInt()
        val valves = split.last().split(", ")
        return ValveNode(name, flowRate, valves)
    }

    data class State(
        val myCurrentValvePosition: String,
        val elephantPosition: String,
        val turn: Turn,
        val minsLeft: Int,
        val openedValves: Set<String>
    )

    val TEMP_ELEPHANT_NAME = "ELEPHANT"

    val cache = mutableMapOf<State, Int>()

    data class OptimizationContext(
        val nameToValveNode: Map<String, ValveNode>
    ) {
        var currBestMax = 0

        fun optimize(state: State): Int {
            require(state.minsLeft >= 0)
            if (state.minsLeft == 0) {
                return 0
            }

            if (cache.size % 10000 == 0) {
                println("cache size: ${cache.size}, currBestMax: $currBestMax")
            }

            if (state in cache) return cache[state]!!

            val myCurrentValveNode = nameToValveNode[state.myCurrentValvePosition]!!
            val elephantValveNode = nameToValveNode[state.elephantPosition]!!

            val max = when (state.turn) {
                Turn.MINE -> if (state.myCurrentValvePosition in state.openedValves || myCurrentValveNode.flowRate == 0) 0 else optimize(
                    // Open this valve
                    State(
                        state.myCurrentValvePosition,
                        state.elephantPosition,
                        Turn.ELEPHANT,
                        state.minsLeft,
                        state.openedValves + state.myCurrentValvePosition,
                    )
                )

                Turn.ELEPHANT -> if (state.elephantPosition in state.openedValves || elephantValveNode.flowRate == 0) 0 else optimize(
                    // Open this valve
                    State(
                        state.myCurrentValvePosition,
                        state.elephantPosition,
                        Turn.MINE,
                        state.minsLeft - 1,
                        state.openedValves + state.elephantPosition
                    )
                )
            }

            val currFlowRateSum = if (state.turn == Turn.MINE) state.openedValves.sumOf {
                nameToValveNode[it]!!.flowRate
            } else 0

            val currentBestMaxBelow =  + when (state.turn) {
                Turn.MINE -> {
                    currFlowRateSum + maxOf(myCurrentValveNode.connectedValves.maxOf {
                        optimize(
                            State(
                                it,
                                state.elephantPosition,
                                Turn.ELEPHANT,
                                state.minsLeft,
                                state.openedValves
                            )
                        )
                    }, max)
                }

                Turn.ELEPHANT -> currFlowRateSum + maxOf(elephantValveNode.connectedValves.maxOf {
                    optimize(
                        State(
                            state.myCurrentValvePosition,
                            it,
                            Turn.MINE,
                            state.minsLeft - 1,
                            state.openedValves
                        )
                    )
                }, max)
            }

            if (state.turn == Turn.MINE) {
                cache[state] = currentBestMaxBelow
            }

            currBestMax = maxOf(currBestMax, currentBestMaxBelow)

            return currentBestMaxBelow
        }
    }

    fun part1(input: List<String>): Unit {
        val valveNodes = input.map { it.toValveNode() }
        val nameToValveNode = valveNodes.associate {
            it.name to it
        }.toMutableMap()
        nameToValveNode[TEMP_ELEPHANT_NAME] = ValveNode(
            TEMP_ELEPHANT_NAME, 0, listOf(TEMP_ELEPHANT_NAME)
        )

        println(valveNodes)
        println(valveNodes.joinToString("\n"))
        println(nameToValveNode)

        val opt = OptimizationContext(nameToValveNode)

        println(opt.optimize(State("AA", TEMP_ELEPHANT_NAME, Turn.MINE, 30, emptySet())))
    }

    fun part2(input: List<String>): Unit {
        val valveNodes = input.map { it.toValveNode() }
        val nameToValveNode = valveNodes.associate {
            it.name to it
        }

        println(valveNodes)
        println(valveNodes.joinToString("\n"))
        println(nameToValveNode)

        val opt = OptimizationContext(nameToValveNode)

        println(opt.optimize(State("AA", "AA", Turn.MINE, 26, emptySet())))
    }

    val dayString = "day16"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //    part1(testInput)
//            part2(testInput)

    val input = readInput("${dayString}_input")
    //            part1(input)
    part2(input)
}



import kotlin.system.exitProcess

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
        val nameToValveNode: Map<String, ValveNode>,
        val totalMinsAvailable: Int
    ) {
        var currBestMax = 0
        var cacheHits = 0
        var numStatesPruned = 0
        var prunedStateBcOfElephant = 0
        var prunedStateBcOfMe = 0

        fun optimizeMyTurn(state: State, oldTotalFlowReleased: Int): Int {
            require(state.turn == Turn.MINE)
            require(state.minsLeft >= 0)

            if (state.minsLeft == 0) {
                return 0
            }

            if (cache.size % 100000 == 0) {
                //                if (cache.size == 3000000) {
                //                    exitProcess(1)
                //                }
                println("cache size: ${cache.size}, cacheHits: $cacheHits, currBestMax: $currBestMax, numStatesPruned: $numStatesPruned, prunedStateBcOfElephant: $prunedStateBcOfElephant, prunedStateBcOfMe: $prunedStateBcOfMe")
            }

            if (state in cache) {
                cacheHits++
                return cache[state]!!
            }

            val myCurrentValveNode = nameToValveNode[state.myCurrentValvePosition]!!

            val currFlowRateSum = state.openedValves.sumOf {
                nameToValveNode[it]!!.flowRate
            }

            val toValveFlows = (nameToValveNode.keys - state.openedValves).asSequence().map {
                nameToValveNode[it]!!.flowRate
            }.sortedDescending()

            val extrapolatedMaxRemainingFlow = toValveFlows.take(state.minsLeft)
                .sum() * state.minsLeft / 1.5 + currFlowRateSum * state.minsLeft

            if (oldTotalFlowReleased + extrapolatedMaxRemainingFlow <= currBestMax) {
                numStatesPruned++
                return 0
            }

            val newTotalFlowReleased = oldTotalFlowReleased + currFlowRateSum

            val seq = sequence {
                yield {
                    if (state.myCurrentValvePosition in state.openedValves || myCurrentValveNode.flowRate == 0)
                        0
                    else
                        optimizeElephant(
                            // Open this valve
                            State(
                                state.myCurrentValvePosition,
                                state.elephantPosition,
                                Turn.ELEPHANT,
                                state.minsLeft,
                                state.openedValves + state.myCurrentValvePosition,
                            ), newTotalFlowReleased
                        )
                }
                if (myCurrentValveNode.connectedValves.size == 1) {
                    yield {
                        optimizeElephant(
                            State(
                                // Move to the next valve
                                myCurrentValveNode.connectedValves.first(),
                                state.elephantPosition,
                                Turn.ELEPHANT,
                                state.minsLeft,
                                state.openedValves
                            ), newTotalFlowReleased
                        )
                    }
                } else {
                    yieldAll(myCurrentValveNode.connectedValves.asSequence().filter {
                        val res = it != state.elephantPosition
                        if (!res) {
                            prunedStateBcOfElephant++
                        }
                        res
                    }.map {
                        {
                            optimizeElephant(
                                State(
                                    // Move to the next valve
                                    it,
                                    state.elephantPosition,
                                    Turn.ELEPHANT,
                                    state.minsLeft,
                                    state.openedValves
                                ), newTotalFlowReleased
                            )
                        }
                    })
                }

            }

            val currentBestMaxBelow =
                seq.fold(0) { acc, function ->
                    val m = maxOf(acc, currFlowRateSum + function())
                    currBestMax = maxOf(currBestMax, m + oldTotalFlowReleased)
                    m
                }

            cache[state] = currentBestMaxBelow
            return currentBestMaxBelow
        }

        fun optimizeElephant(state: State, numFlowReleased: Int): Int {
            require(state.turn == Turn.ELEPHANT)
            require(state.minsLeft >= 0)
            if (state.minsLeft == 0) {
                return 0
            }

            val elephantValveNode = nameToValveNode[state.elephantPosition]!!

            val max =
                if (state.elephantPosition in state.openedValves || elephantValveNode.flowRate == 0) 0 else optimizeMyTurn(
                    // Open this valve
                    State(
                        state.myCurrentValvePosition,
                        state.elephantPosition,
                        Turn.MINE,
                        state.minsLeft - 1,
                        state.openedValves + state.elephantPosition,
                    ), numFlowReleased
                )

            val currentBestMaxBelow =
                maxOf(if (elephantValveNode.connectedValves.size == 1) {
                    optimizeMyTurn(
                        State(
                            state.myCurrentValvePosition,
                            elephantValveNode.connectedValves.first(),
                            Turn.MINE,
                            state.minsLeft - 1,
                            state.openedValves
                        ), numFlowReleased
                    )
                } else {
                    elephantValveNode.connectedValves.asSequence().filter {
                        val res = it != state.myCurrentValvePosition
                        if (!res) {
                            prunedStateBcOfMe++
                        }
                        res
                    }.maxOf {
                        optimizeMyTurn(
                            State(
                                state.myCurrentValvePosition,
                                it,
                                Turn.MINE,
                                state.minsLeft - 1,
                                state.openedValves
                            ), numFlowReleased
                        )
                    }
                }, max)

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

        val opt = OptimizationContext(nameToValveNode, 30)

        println(
            opt.optimizeMyTurn(
                State("AA", TEMP_ELEPHANT_NAME, Turn.MINE, 30, emptySet()),
                0
            )
        )
    }

    fun part2(input: List<String>): Unit {
        val valveNodes = input.map { it.toValveNode() }
        val nameToValveNode = valveNodes.associate {
            it.name to it
        }

        println(valveNodes)
        println(valveNodes.joinToString("\n"))
        println(nameToValveNode)

        val opt = OptimizationContext(nameToValveNode, 26)

        println(opt.optimizeMyTurn(State("AA", "AA", Turn.MINE, 26, emptySet()), 0))
    }

    val dayString = "day16"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //            part1(testInput)
    //        part2(testInput)

    val input = readInput("${dayString}_input")
    //                part1(input)
    part2(input)
}



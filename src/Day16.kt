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
        val currentNode: String,
        val minsLeft: Int,
        val openedValves: Set<String>
    )

    val cache = mutableMapOf<State, Int>()

    data class OptimizationContext(
        val nameToValveNode: Map<String, ValveNode>
    ) {
        fun optimize(state: State): Int {
            require(state.minsLeft >= 0)
            if (state.minsLeft == 0) {
                return 0
            }

            if (state in cache) return cache[state]!!

            val currFlowRateSum = state.openedValves.sumOf {
                nameToValveNode[it]!!.flowRate
            }

            val currentValveNode = nameToValveNode[state.currentNode]!!

            val max = if (state.currentNode in state.openedValves || currentValveNode.flowRate == 0) 0 else optimize(
                // Open this valve
                State(
                    state.currentNode,
                    state.minsLeft - 1,
                    state.openedValves + state.currentNode
                )
            )

            val bestMax = currFlowRateSum + maxOf(currentValveNode.connectedValves.maxOf {
                optimize(State(it, state.minsLeft - 1, state.openedValves))
            }, max)
            cache[state] = bestMax

            return bestMax
        }
    }

    fun part1(input: List<String>): Unit {
        val valveNodes = input.map { it.toValveNode() }
        val nameToValveNode = valveNodes.associate {
            it.name to it
        }

        println(valveNodes)
        println(valveNodes.joinToString("\n"))
        println(nameToValveNode)

        val opt = OptimizationContext(nameToValveNode)

        println(opt.optimize(State("AA", 30, emptySet())))
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day16"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
            part1(input)
    //        part2(input)
}



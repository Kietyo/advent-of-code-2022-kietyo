import kotlin.time.Duration.Companion.milliseconds

data class RobotSpec(
    val robotTypeIdx: Int,
    val materialCosts: IntArray
) {
    fun canBuyWithCurrentMaterials(currMaterials: IntArray): Boolean {
        require(materialCosts.size == currMaterials.size)
        for (i in currMaterials.indices) {
            if (currMaterials[i] < materialCosts[i]) return false
        }
        return true
    }
}

sealed class GeoNextState {
    data class BuildRobot(val robotSpec: RobotSpec) : GeoNextState()
    data object Continue : GeoNextState()
}

const val ORE_IDX = 0
const val CLAY_IDX = 1
const val OBSIDIAN_IDX = 2
const val GEODE_IDX = 3
const val NUM_MATERIALS = 4

fun main() {

    val materialToIndex = listOf(
        "ore", "clay", "obsidian", "geode"
    )

    data class Blueprint(
        val id: Int,
        val robotSpecs: List<RobotSpec>
    ) {
        fun getAllSpecsAvailable(currMaterials: IntArray): List<RobotSpec> {
            return robotSpecs.filter {
                it.canBuyWithCurrentMaterials(currMaterials)
            }
        }
    }

    fun String.robotDescriptionToRobot(): RobotSpec {
        val rsp1 = this.split(" costs ")
        val (_, robotType, _) = rsp1.first().split(" ")

        val materialsCostArray = IntArray(NUM_MATERIALS) { 0 }

        rsp1[1].split(" and ").forEach {
            val (cost, materialType) = it.split(" ")
            materialsCostArray[materialToIndex.indexOf(materialType)] = cost.toInt()
        }

        val robotTypeIdx = materialToIndex.indexOf(robotType)
        require(robotTypeIdx >= 0)

        return RobotSpec(robotTypeIdx, materialsCostArray)
    }

    fun String.toBlueprint(): Blueprint {
        val split1 = this.split(": ")
        val split2 = split1[1].dropLast(1).split(". ")

        val blueprintId = split1.first().split(" ").last().toInt()

        println(split1)

        val robotDescription = split2.map { it.robotDescriptionToRobot() }

        println(blueprintId)
        println(robotDescription)
        return Blueprint(blueprintId, robotDescription)
    }

    data class GeoState(
        val currRobots: IntArray,
        val currMaterials: IntArray,
        val minsLeft: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GeoState

            if (!currRobots.contentEquals(other.currRobots)) return false
            if (!currMaterials.contentEquals(other.currMaterials)) return false
            if (minsLeft != other.minsLeft) return false

            return true
        }

        override fun hashCode(): Int {
            var result = currRobots.contentHashCode()
            result = 31 * result + currMaterials.contentHashCode()
            result = 31 * result + minsLeft
            return result
        }
    }

    class GeoSimulator(val blueprint: Blueprint) {

        val maxMaterialsNeeded = IntArray(NUM_MATERIALS).also {arr ->
            blueprint.robotSpecs.forEach {
                it.materialCosts.forEachIndexed { index, i ->
                    arr[index] = maxOf(arr[index], i)
                }
            }
        }

        init {
            println("maxMaterialsNeeded: ${maxMaterialsNeeded.toList()}")
        }

        var numStatesPruned = 0

        fun getNextStates(
            currMaterials: IntArray
        ): List<GeoNextState> {
            val nextStates = mutableListOf<GeoNextState>()
            blueprint.getAllSpecsAvailable(currMaterials).forEach {
                nextStates.add(GeoNextState.BuildRobot(it))
//                if (currMaterials[it.robotTypeIdx] <= maxMaterialsNeeded[it.robotTypeIdx] * 2) {
//                    nextStates.add(GeoNextState.BuildRobot(it))
//                } else {
//                    numStatesPruned++
//                }
            }

            if (nextStates.size != blueprint.robotSpecs.size) {
                numStatesPruned++
                nextStates.add(GeoNextState.Continue)
            }

            return nextStates
        }

        val cache = mutableMapOf<GeoState, Int>()
        val cacheSize get() = cache.size
        var cacheHits = 0
        var numStatesExplored = 0
        var bestGeoCount = 0

        fun simulate(
            geostate: GeoState
        ): Int {
            numStatesExplored++
            val currRobots = geostate.currRobots
            val currMaterials = geostate.currMaterials
            val minsLeft = geostate.minsLeft

            if (numStatesExplored % 100000 == 0) {
                println("numStatesExplored: $numStatesExplored, numStatesPruned: $numStatesPruned, cacheSize: $cacheSize, cacheHits: $cacheHits, bestGeoCount: $bestGeoCount")
            }

            if (minsLeft == 0) {
                val currGeos = currMaterials[GEODE_IDX]
                if (currGeos == 30) {
                    println("wtf")
                }
                bestGeoCount = maxOf(bestGeoCount, currGeos)
                return currGeos
            }

            if (geostate in cache) {
                cacheHits++
                return cache[geostate]!!
            }

            val nextStates = getNextStates(currMaterials)

            val maxGeos = nextStates.maxOf {
                when (it) {
                    is GeoNextState.BuildRobot -> {
                        val materialsAfterBuildingRobot = IntArray(4) { idx ->
                            val newMaterialCount = currMaterials[idx] - it.robotSpec.materialCosts[idx] + geostate.currRobots[idx]
                            require(newMaterialCount >= 0)
                            newMaterialCount
                        }
                        val newRobotMap = currRobots.copyOf()
                        newRobotMap[it.robotSpec.robotTypeIdx]++
                        simulate(GeoState(newRobotMap, materialsAfterBuildingRobot, minsLeft - 1))
                    }

                    GeoNextState.Continue -> {
                        val newMaterials = IntArray(4) { idx ->
                            currMaterials[idx] + geostate.currRobots[idx]
                        }
                        simulate(GeoState(currRobots, newMaterials, minsLeft - 1))
                    }
                }
            }

            cache[geostate] = maxGeos

            return maxGeos
        }
    }

    fun part1(input: List<String>): Unit {
        val startTime = System.currentTimeMillis()
        println("startTime: $startTime")
        val blueprints = input.map { it.toBlueprint() }

        println(blueprints.joinToString("\n"))

        val simulator = GeoSimulator(blueprints.first())
        val bestGeos = simulator.simulate(GeoState(
            IntArray(materialToIndex.size) { 0 }.also {
                it[ORE_IDX] = 1
            },
            IntArray(NUM_MATERIALS) { 0 },
            24
        )
        )

        val endTime = System.currentTimeMillis()
                println("bestGeos: $bestGeos")
        println((endTime - startTime).milliseconds)
    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day19"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    part1(testInput)
    //    part2(testInput)

    val input = readInput("${dayString}_input")
    //        part1(input)
    //        part2(input)
}



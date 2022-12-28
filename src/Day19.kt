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
            }.asReversed()
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
        val robotDescription = split2.map { it.robotDescriptionToRobot() }
        return Blueprint(blueprintId, robotDescription)
    }

    data class GeoState(
        val currRobots: IntArray,
        val currMaterials: IntArray,
        val minsLeft: Int
    ) {
        fun createCopy() = GeoState(currRobots.clone(), currMaterials.clone(), minsLeft
        )
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
            currMaterials: IntArray,
            minsLeft: Int
        ): List<GeoNextState> {
            val nextStates = mutableListOf<GeoNextState>()
            blueprint.getAllSpecsAvailable(currMaterials).forEach {
                nextStates.add(GeoNextState.BuildRobot(it))
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

            if (numStatesExplored % 1000000 == 0) {
                println("numStatesExplored: $numStatesExplored, numStatesPruned: $numStatesPruned, cacheSize: $cacheSize, cacheHits: $cacheHits, bestGeoCount: $bestGeoCount")
            }

            if (minsLeft == 0) {
                val currGeos = currMaterials[GEODE_IDX]
                bestGeoCount = maxOf(bestGeoCount, currGeos)
                return currGeos
            }

            if (minsLeft > 0) {
                val currGeos = currMaterials[GEODE_IDX]
                val numGeoRobots = currRobots[GEODE_IDX]
                val maxGeosRemaining = numGeoRobots * minsLeft + ((minsLeft * (minsLeft - 1)) / 2)
                if (currGeos + maxGeosRemaining <= bestGeoCount) {
                    numStatesPruned++
                    return bestGeoCount
                }
            }

            if (minsLeft == 2) {
                val currGeos = currMaterials[GEODE_IDX]
                val numGeoRobots = currRobots[GEODE_IDX]
                if (currGeos + numGeoRobots + (numGeoRobots + 1) + 1 <= bestGeoCount) {
                    numStatesPruned++
                    return bestGeoCount
                }
            }

            if (geostate in cache) {
                cacheHits++
                return cache[geostate]!!
            }

            val nextStates = getNextStates(currMaterials, minsLeft)

            for (i in currMaterials.indices) {
                currMaterials[i] += geostate.currRobots[i]
            }

            val maxGeos = nextStates.maxOf {
                when (it) {
                    is GeoNextState.BuildRobot -> {
                        for (i in currMaterials.indices) {
                            currMaterials[i] -= it.robotSpec.materialCosts[i]
                        }

                        currRobots[it.robotSpec.robotTypeIdx]++
                        val g = simulate(GeoState(currRobots, currMaterials, minsLeft - 1))
                        currRobots[it.robotSpec.robotTypeIdx]--

                        for (i in currMaterials.indices) {
                            currMaterials[i] += it.robotSpec.materialCosts[i]
                        }

                        g
                    }

                    GeoNextState.Continue -> {
                        simulate(GeoState(currRobots, currMaterials, minsLeft - 1))
                    }
                }
            }

            for (i in currMaterials.indices) {
                currMaterials[i] -= geostate.currRobots[i]
            }

            cache[geostate.createCopy()] = maxGeos

            return maxGeos
        }
    }

    fun part1(input: List<String>): Unit {
        val startTime = System.currentTimeMillis()
        println("startTime: $startTime")
        val blueprints = input.map { it.toBlueprint() }

        println(blueprints.joinToString("\n"))

        val blueprintToBestGeos = blueprints.map {
            val simulator = GeoSimulator(it)
            val bestGeos = simulator.simulate(GeoState(
                IntArray(materialToIndex.size) { 0 }.also {
                    it[ORE_IDX] = 1
                },
                IntArray(NUM_MATERIALS) { 0 },
                24)
            )
            it to bestGeos
        }

        val sumQualityScores = blueprintToBestGeos.sumOf {
            it.first.id * it.second
        }

        val endTime = System.currentTimeMillis()
                println("sumQualityScores: $sumQualityScores")
        println((endTime - startTime).milliseconds)
    }

    fun part2(input: List<String>): Unit {
        val startTime = System.currentTimeMillis()
        println("startTime: $startTime")
        val blueprints = input.map { it.toBlueprint() }

        println(blueprints.joinToString("\n"))

//        val simulator = GeoSimulator(blueprints[1])
//        val bestGeos = simulator.simulate(GeoState(
//            IntArray(materialToIndex.size) { 0 }.also {
//                it[ORE_IDX] = 1
//            },
//            IntArray(NUM_MATERIALS) { 0 },
//            32)
//        )

        val product = blueprints.take(3).map {
            val simulator = GeoSimulator(it)
            val bestGeos = simulator.simulate(GeoState(
                IntArray(materialToIndex.size) { 0 }.also {
                    it[ORE_IDX] = 1
                },
                IntArray(NUM_MATERIALS) { 0 },
                32)
            )
            bestGeos
        }.fold(1) { acc, i ->
            acc * i
        }

        val endTime = System.currentTimeMillis()

        println("product: $product")
        println((endTime - startTime).milliseconds)
    }

    val dayString = "day19"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
        part2(testInput)

    val input = readInput("${dayString}_input")
//            part1(input)
//            part2(input)
}



import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

data class RobotSpec(
    val robotTypeIdx: Int,
    val oreCost: Short,
    val clayCost: Short,
    val obsidianCost: Short,
) {
    fun canBuyWithCurrentMaterials(
        ore: Short,
        clay: Short,
        obsidian: Short,
        geode: Short
    ): Boolean {
        return !(ore < oreCost ||
                clay < clayCost ||
                obsidian < obsidianCost)
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

const val SHORT_ZERO = 0.toShort()
const val SHORT_ONE = 1.toShort()

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> List<T>.parallelMap(transform: (T) -> R): List<R> {
    return runBlocking {
        val jobs = this@parallelMap.map {
            async {
                transform(it)
            }
        }

        jobs.joinAll()

        return@runBlocking jobs.map { it.getCompleted() }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {

    val materialToIndex = listOf(
        "ore", "clay", "obsidian", "geode"
    )

    data class Blueprint(
        val id: Int,
        val robotSpecs: List<RobotSpec>
    ) {
        fun getAllSpecsAvailable(
            ore: Short,
            clay: Short,
            obsidian: Short,
            geode: Short,
        ): List<RobotSpec> {
            return robotSpecs.filter {
                it.canBuyWithCurrentMaterials(ore, clay, obsidian, geode)
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

        return RobotSpec(
            robotTypeIdx,
            materialsCostArray[ORE_IDX].toShort(),
            materialsCostArray[CLAY_IDX].toShort(),
            materialsCostArray[OBSIDIAN_IDX].toShort(),
        )
    }

    fun String.toBlueprint(): Blueprint {
        val split1 = this.split(": ")
        val split2 = split1[1].dropLast(1).split(". ")
        val blueprintId = split1.first().split(" ").last().toInt()
        val robotDescription = split2.map { it.robotDescriptionToRobot() }
        return Blueprint(blueprintId, robotDescription)
    }

    data class GeoState(
        val oreRobots: Short,
        val clayRobots: Short,
        val obsidianRobots: Short,
        val geodeRobots: Short,
        val ore: Short,
        val clay: Short,
        val obsidian: Short,
        val geode: Short,
        val minsLeft: Int
    )

    class GeoSimulator(val blueprint: Blueprint) {

        val maxOreNeeded = blueprint.robotSpecs.maxOf { it.oreCost }
        val maxClayNeeded = blueprint.robotSpecs.maxOf { it.clayCost }
        val maxObsidianNeeded = blueprint.robotSpecs.maxOf { it.obsidianCost }

        var numStatesPruned = 0

        fun getNextStates(
            currentState: GeoState
        ): List<GeoNextState> {
            val nextStates = mutableListOf<GeoNextState>()
            blueprint.getAllSpecsAvailable(
                currentState.ore,
                currentState.clay,
                currentState.obsidian,
                currentState.geode
            ).forEach {
                if (it.robotTypeIdx == ORE_IDX && currentState.oreRobots < maxOreNeeded) {
                    nextStates.add(GeoNextState.BuildRobot(it))
                }
                if (it.robotTypeIdx == CLAY_IDX && currentState.clayRobots < maxClayNeeded) {
                    nextStates.add(GeoNextState.BuildRobot(it))
                }
                if (it.robotTypeIdx == OBSIDIAN_IDX && currentState.obsidianRobots < maxObsidianNeeded) {
                    nextStates.add(GeoNextState.BuildRobot(it))
                }
                if (it.robotTypeIdx == GEODE_IDX) {
                    nextStates.add(GeoNextState.BuildRobot(it))
                }
            }

            if (nextStates.size != blueprint.robotSpecs.size) {
                numStatesPruned++
                nextStates.add(GeoNextState.Continue)
            }

            return nextStates
        }

        val cache = mutableMapOf<GeoState, Short>()
        val cacheSize get() = cache.size
        var cacheHits = 0
        var numStatesExplored = 0
        var bestGeoCount: Short = 0

        fun simulate(
            geostate: GeoState
        ): Short {
            numStatesExplored++
            val minsLeft = geostate.minsLeft

            if (numStatesExplored % 1000000 == 0) {
                println("numStatesExplored: $numStatesExplored, numStatesPruned: $numStatesPruned, cacheSize: $cacheSize, cacheHits: $cacheHits, bestGeoCount: $bestGeoCount")
            }

            if (minsLeft == 0) {
                bestGeoCount = maxOf(bestGeoCount, geostate.geode)
                return geostate.geode
            }

            if (minsLeft > 0) {
                val numGeoRobots = geostate.geodeRobots
                val maxGeosRemaining = numGeoRobots * minsLeft + ((minsLeft * (minsLeft - 1)) / 2)
                if (geostate.geode + maxGeosRemaining <= bestGeoCount) {
                    numStatesPruned++
                    return bestGeoCount
                }
            }

            if (geostate in cache) {
                cacheHits++
                return cache[geostate]!!
            }

            val nextStates = getNextStates(geostate)

            val newOre = (geostate.ore + geostate.oreRobots).toShort()
            val newClay = (geostate.clay + geostate.clayRobots).toShort()
            val newObsidian = (geostate.obsidian + geostate.obsidianRobots).toShort()
            val newGeode = (geostate.geode + geostate.geodeRobots).toShort()

            val maxGeos = nextStates.maxOf {
                when (it) {
                    is GeoNextState.BuildRobot -> {
                        val g = simulate(
                            GeoState(
                                if (it.robotSpec.robotTypeIdx == ORE_IDX) geostate.oreRobots.inc() else geostate.oreRobots,
                                if (it.robotSpec.robotTypeIdx == CLAY_IDX) geostate.clayRobots.inc() else geostate.clayRobots,
                                if (it.robotSpec.robotTypeIdx == OBSIDIAN_IDX) geostate.obsidianRobots.inc() else geostate.obsidianRobots,
                                if (it.robotSpec.robotTypeIdx == GEODE_IDX) geostate.geodeRobots.inc() else geostate.geodeRobots,
                                (newOre - it.robotSpec.oreCost).toShort(),
                                (newClay - it.robotSpec.clayCost).toShort(),
                                (newObsidian - it.robotSpec.obsidianCost).toShort(),
                                newGeode,
                                minsLeft - 1
                            )
                        )
                        g
                    }

                    GeoNextState.Continue -> {
                        simulate(
                            GeoState(
                                geostate.oreRobots,
                                geostate.clayRobots,
                                geostate.obsidianRobots,
                                geostate.geodeRobots,
                                newOre,
                                newClay,
                                newObsidian,
                                newGeode, minsLeft - 1
                            )
                        )
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

        val sumQualityScores = blueprints.parallelMap {
            val blueprint = it
            val simulator = GeoSimulator(blueprint)
            val bestGeos = simulator.simulate(
                GeoState(
                    1,
                    0,
                    0,
                    0,
                    0, 0, 0, 0,
                    24
                )
            )
            blueprint to bestGeos
        }.sumOf {
            it.first.id * it.second
        }

        val endTime = System.currentTimeMillis()
        println("sumQualityScores: $sumQualityScores")
        println((endTime - startTime).milliseconds)

        //        val blueprintToBestGeos = blueprints.map {
        //            val simulator = GeoSimulator(it)
        //            val bestGeos = simulator.simulate(
        //                GeoState(
        //                    1,
        //                    0,
        //                    0,
        //                    0,
        //                    0, 0, 0, 0,
        //                    24
        //                )
        //            )
        //            it to bestGeos
        //        }
        //
        //        val sumQualityScores = blueprintToBestGeos.sumOf {
        //            it.first.id * it.second
        //        }

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

        val product = blueprints.take(3).parallelMap {
            println(it)
            val simulator = GeoSimulator(it)
            val bestGeos = simulator.simulate(
                GeoState(
                    1,
                    0,
                    0,
                    0,
                    0, 0, 0, 0,
                    32
                )
            )
            println()
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
    //        part1(testInput)
    //        part2(testInput)

    val input = readInput("${dayString}_input")
//    part1(input)
        part2(input)
}



data class Tuple(
    val x: Int,
    val y: Int,
    val z: Int
)

typealias Cube = Tuple;

fun main() {

    val offsets = listOf(
        Tuple(1, 0, 0),
        Tuple(-1, 0, 0),
        Tuple(0, 1, 0),
        Tuple(0, -1, 0),
        Tuple(0, 0, 1),
        Tuple(0, 0, -1),
    )

    class CubeWorld(val cubes: List<Cube>) {

        val minX = cubes.minOf { it.x } - 1
        val maxX = cubes.maxOf { it.x } + 1

        val minY = cubes.minOf { it.y } - 1
        val maxY = cubes.maxOf { it.y } + 1

        val minZ = cubes.minOf { it.z } - 1
        val maxZ = cubes.maxOf { it.z } + 1

        val numTotalExposedSides
            get() = cubes.sumOf {
                countNumExposedSides(it.x, it.y, it.z)
            }

        fun existsCubeAt(x: Int, y: Int, z: Int): Boolean {
            return cubes.any {
                it.x == x && it.y == y && it.z == z
            }
        }

        fun countNumExposedSides(x: Int, y: Int, z: Int): Int {
            return offsets.count { offset ->
                !existsCubeAt(
                    x + offset.x,
                    y + offset.y,
                    z + offset.z,
                )
            }
        }

        fun isInsideWorld(x: Int, y: Int, z: Int): Boolean {
            return x in minX..maxX &&
                    y in minY..maxY &&
                    z in minZ..maxZ
        }

        val exposedToOutsideCache = mutableSetOf<Cube>()

        fun isAirCubeExposedToOutside(x: Int, y: Int, z: Int): Boolean {
            if (existsCubeAt(x, y, z)) return false

            return offsets.any {
                var multiplier = 1
                while (true) {
                    val currAirCubeX = x + it.x * multiplier
                    val currAirCubeY = y + it.y * multiplier
                    val currAirCubeZ = z + it.z * multiplier
                    if (Cube(currAirCubeX, currAirCubeY, currAirCubeZ) in exposedToOutsideCache) {
                        // Is connected to another air cube that is exposed to the outside
                        return true
                    }
                    if (!isInsideWorld(currAirCubeX, currAirCubeY, currAirCubeZ)) {
                        // Is exposed on this side
                        return true
                    }
                    if (existsCubeAt(currAirCubeX, currAirCubeY, currAirCubeZ)) {
                        // Enclosed on this side
                        return@any false
                    }
                    multiplier++
                }
                TODO()
            }
        }

        init {
            var prevCacheSize = exposedToOutsideCache.size

            // Keep on building the cache until we found out all cubes that
            // are exposed to the outside.
            while (true) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        for (z in minZ..maxZ) {
                            if (isAirCubeExposedToOutside(x, y, z)) {
                                exposedToOutsideCache.add(Cube(x, y, z))
                            }
                        }
                    }
                }
                val newCacheSize = exposedToOutsideCache.size
                if (prevCacheSize == newCacheSize) {
                    break
                } else {
                    prevCacheSize = newCacheSize
                }
            }

            println("""
                minX: $minX
                maxX: $maxX
                
                minY: $minY
                maxY: $maxY
                
                minZ: $minZ
                maxZ: $maxZ
            """.trimIndent())
            println("exposedToOutsideCache")
            println(exposedToOutsideCache.size)
        }

        fun isEnclosedAirCube(x: Int, y: Int, z: Int): Boolean {
            if (existsCubeAt(x, y, z)) return false

            if (isAirCubeExposedToOutside(x, y, z)) return false

            return offsets.all {
                var multiplier = 1
                while (true) {
                    val currOffsetX = x + it.x * multiplier
                    val currOffsetY = y + it.y * multiplier
                    val currOffsetZ = z + it.z * multiplier
                    if (!isInsideWorld(currOffsetX, currOffsetY, currOffsetZ)) {
                        // Is exposed on this side
                        return@all false
                    }
                    if (existsCubeAt(currOffsetX, currOffsetY, currOffsetZ)) {
                        // Enclosed on this side
                        return@all true
                    }
                    multiplier++
                }
                TODO()
            }
        }

        fun getEnclosedAirCubes(): List<Cube> {
            val enclosedAirCubes = mutableListOf<Cube>()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        if (isEnclosedAirCube(x, y, z)) {
                            enclosedAirCubes.add(Cube(x, y, z))
                        }
                    }
                }
            }
            return enclosedAirCubes
        }

        fun countExteriorSurfaceArea(): Int {
            val numTotalExposedSides = numTotalExposedSides
            val enclosedAirCubes = getEnclosedAirCubes()

            val numEnclosedSides = enclosedAirCubes.sumOf {
                val numExposedSides = countNumExposedSides(it.x, it.y, it.z)
                6 - numExposedSides
            }
            return numTotalExposedSides - numEnclosedSides
        }
    }

    fun part1(input: List<String>): Unit {
        val cubes = input.map {
            val (x, y, z) = it.split(",").map { it.toInt() }
            Cube(x, y, z)
        }

        val cubeWorld = CubeWorld(cubes)

        println("Cubes:")
        println(cubes.joinToString("\n"))

//        println(cubeWorld.numTotalExposedSides)
        println(cubeWorld.countExteriorSurfaceArea())

    }

    fun part2(input: List<String>): Unit {

    }

    val dayString = "day18"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//            part1(testInput)
//        part2(testInput)

    val input = readInput("${dayString}_input")

    // Part 2: Tried out 2494. answer too low
    part1(input)
    //        part2(input)
}



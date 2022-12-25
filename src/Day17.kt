import kotlin.system.exitProcess

enum class BlockTypeEnum(
    val blockingOffsets: List<MutableIntPoint>,
    val lowestYOffset: Int
) {
    HORIZONTAL_LINE(
        listOf(0 toip 0, 1 toip 0, 2 toip 0, 3 toip 0),
        0
    ),
    PLUS(
        listOf(
            1 toip 0,
            0 toip -1, 1 toip -1, 2 toip -1,
            1 toip -2
        ), -2
    ),
    REVERSE_L(
        listOf(
            2 toip 0,
            2 toip -1,
            2 toip -2, 1 toip -2, 0 toip -2
        ), -2
    ),
    VERTICAL_LINE(
        listOf(
            0 toip 0, 0 toip -1, 0 toip -2, 0 toip -3
        ), -3
    ),
    SQUARE(
        listOf(
            0 toip 0, 1 toip 0,
            0 toip -1, 1 toip -1,
        ), -1
    ),
    CHAMBER_WIDTH_BLOCK(
        listOf(
            0 toip 0,
            1 toip 0,
            2 toip 0,
            3 toip 0,
            4 toip 0,
            5 toip 0,
            6 toip 0
        ), 0
    );

    fun getBlockType(): BlockType {
        return when (this) {
            HORIZONTAL_LINE -> BlockType.HorizontalLine(MutableIntPoint(0, 0))
            PLUS -> BlockType.Plus(MutableIntPoint(0, 0))
            REVERSE_L -> BlockType.ReverseL(MutableIntPoint(0, 0))
            VERTICAL_LINE -> BlockType.VerticalLine(MutableIntPoint(0, 0))
            SQUARE -> BlockType.Square(MutableIntPoint(0, 0))
            CHAMBER_WIDTH_BLOCK -> BlockType.ChamberWidthBlock(MutableIntPoint(0, 0))
        }
    }
}

sealed class BlockType {
    abstract val pos: MutableIntPoint
    abstract val width: Int
    abstract val height: Int
    val blockingOffsets: List<MutableIntPoint>
        get() = when (this) {
            is HorizontalLine -> BlockTypeEnum.HORIZONTAL_LINE.blockingOffsets
            is Plus -> BlockTypeEnum.PLUS.blockingOffsets
            is ReverseL -> BlockTypeEnum.REVERSE_L.blockingOffsets
            is Square -> BlockTypeEnum.SQUARE.blockingOffsets
            is VerticalLine -> BlockTypeEnum.VERTICAL_LINE.blockingOffsets
            is ChamberWidthBlock -> BlockTypeEnum.CHAMBER_WIDTH_BLOCK.blockingOffsets
        }

    data class HorizontalLine(override val pos: MutableIntPoint) : BlockType() {
        override val width = 4
        override val height = 1
    }

    data class Plus(override val pos: MutableIntPoint) : BlockType() {
        override val width = 3
        override val height = 3
    }

    data class ReverseL(override val pos: MutableIntPoint) : BlockType() {
        override val width = 3
        override val height = 3
    }

    data class VerticalLine(override val pos: MutableIntPoint) : BlockType() {
        override val width = 1
        override val height = 4
    }

    data class Square(override val pos: MutableIntPoint) : BlockType() {
        override val width = 2
        override val height = 2
    }

    data class ChamberWidthBlock(override val pos: MutableIntPoint) : BlockType() {
        override val width = CHAMBER_WIDTH
        override val height = 1
    }

    fun blockingPointAny(block: (x: Int, y: Int) -> Boolean): Boolean {
        return blockingOffsets.any {
            block(pos.x + it.x, pos.y + it.y)
        }
    }

    val blockingPointsSeq
        get() = blockingOffsets.asSequence().map {
            MutableIntPoint(pos.x + it.x, pos.y + it.y)
        }.toSet()

    // Does this block collide with the other?
    fun collides(other: BlockType): Boolean {
        return blockingPointAny { x1, y1 ->
            other.collidesWithUnitBlock(x1, y1)
        }
    }

    fun collidesWithUnitBlock(otherX: Int, otherY: Int): Boolean {
        return blockingPointAny { x, y ->
            x == otherX && y == otherY
        }
    }

    val lowestYOffset: Int
        get() = when (this) {
            is HorizontalLine -> BlockTypeEnum.HORIZONTAL_LINE.lowestYOffset
            is Plus -> BlockTypeEnum.PLUS.lowestYOffset
            is ReverseL -> BlockTypeEnum.REVERSE_L.lowestYOffset
            is Square -> BlockTypeEnum.SQUARE.lowestYOffset
            is VerticalLine -> BlockTypeEnum.VERTICAL_LINE.lowestYOffset
            is ChamberWidthBlock -> BlockTypeEnum.CHAMBER_WIDTH_BLOCK.lowestYOffset
        }

    val lowestYPosition
        get() = pos.y + lowestYOffset
}

enum class Jet {
    LEFT, RIGHT
}

const val CHAMBER_WIDTH = 7

fun main() {
    data class SimulatorState(
        val blockTypeIdx: Int,
        val jetPosition: Int
    )

    data class SimulatorResult(
        val numBlocksDropped: Int,
        val heightAdded: Int,
        val nextBlockTypeIdx: Int,
        val nextJetPosition: Int
    )

    data class Simulator(
        val jetPattern: String,
        val droppedBlocks: MutableList<BlockType> = mutableListOf()
    ) {
        var currJetPosition = 0

        var highestY = 0

        fun nextJetPosition(): Jet {
            val char = jetPattern[currJetPosition]
            currJetPosition = (currJetPosition + 1) % jetPattern.length
            return if (char == '<') Jet.LEFT else Jet.RIGHT
        }

        fun blockCollides(block: BlockType): Boolean {
            return droppedBlocks.any { it.collides(block) }
        }

        fun isYCompletelyFilled(y: Int): Boolean {
            if (y < 0) return false
            return (0 until CHAMBER_WIDTH).all { x ->
                droppedBlocks.any {
                    it.collidesWithUnitBlock(x, y)
                }
            }
        }

        fun attemptToMoveLeft(block: BlockType) {
            val prevX = block.pos.x
            block.pos.x = maxOf(block.pos.x - 1, 0)
            if (prevX == block.pos.x) {
                // Did not change x pos
                return
            }
            if (blockCollides(block)) {
                // Revert changes
                block.pos.x++
            }
        }

        fun attemptToMoveRight(block: BlockType) {
            val prevX = block.pos.x
            block.pos.x = minOf(block.pos.x + 1, CHAMBER_WIDTH - block.width)
            if (prevX == block.pos.x) {
                // Did not change x pos
                return
            }
            if (blockCollides(block)) {
                // Revert changes
                block.pos.x--
            }
        }

        fun attemptToMoveDown(block: BlockType): Boolean {
            if (block.lowestYPosition <= 0) {
                return false
            }
            block.pos.y--
            return if (blockCollides(block)) {
                block.pos.y++
                false
            } else {
                true
            }
        }

        fun addDroppedBlock(blockType: BlockType) {
            highestY = maxOf(highestY, blockType.pos.y + 1)
            droppedBlocks.add(blockType)
        }

        fun dropBlock(block: BlockType) {
            block.pos.y = highestY + 3 + block.height - 1
            block.pos.x = 2

            while (true) {
                when (nextJetPosition()) {
                    Jet.LEFT -> attemptToMoveLeft(block)
                    Jet.RIGHT -> attemptToMoveRight(block)
                }
                if (!attemptToMoveDown(block)) {
                    break
                }
            }

            addDroppedBlock(block)
        }
    }

    val blockTypeOrder = listOf(
        BlockTypeEnum.HORIZONTAL_LINE,
        BlockTypeEnum.PLUS,
        BlockTypeEnum.REVERSE_L,
        BlockTypeEnum.VERTICAL_LINE,
        BlockTypeEnum.SQUARE
    )

    fun part1(input: List<String>): Unit {
        println(input)
        val simulator = Simulator(input.first())
        var idx = 0

        repeat(2022) { simRunNum ->
            println(simRunNum)
            val currBlock = blockTypeOrder[idx].getBlockType()
            simulator.dropBlock(currBlock)
            idx = (idx + 1) % 5

            val highestY = simulator.highestY
            if (simRunNum > 0) {
                repeat(8) {
                    if (simulator.isYCompletelyFilled(highestY - it)) {
                        println("completely filled at: ${highestY - it}")
                    }
                }
            }
        }
        println(simulator)
        println(simulator.highestY)
    }

    fun calculateSimulatorStates(jetPattern: String): MutableList<Pair<SimulatorState, SimulatorResult>> {
        val simulator = Simulator(jetPattern)


        val savedStates = mutableListOf<Pair<SimulatorState, SimulatorResult>>()

        var blockTypeIndex = 0

        var currentState = SimulatorState(blockTypeIndex, simulator.currJetPosition)
        var numBlocksDropped = 0

        var prevHighestY = 0

        repeat(20220000) { simRunNum ->
            numBlocksDropped++
            println(simRunNum)
            val currBlock = blockTypeOrder[blockTypeIndex].getBlockType()
            simulator.dropBlock(currBlock)
            blockTypeIndex = (blockTypeIndex + 1) % 5

            val highestY = simulator.highestY - 1
            if (simRunNum > 0) {
                if (simulator.isYCompletelyFilled(highestY)) {
                    println("completely filled at: ${highestY}")
                    val simulatorResult = SimulatorResult(
                        numBlocksDropped,
                        highestY - prevHighestY,
                        blockTypeIndex, simulator.currJetPosition
                    )
                    savedStates.add(currentState to simulatorResult)

                    // Reset
                    prevHighestY = highestY
                    currentState = SimulatorState(blockTypeIndex, simulator.currJetPosition)
                    numBlocksDropped = 0

                    val seenStateBefore = savedStates.any {
                        it.first.blockTypeIdx == blockTypeIndex && it.first.jetPosition == simulator.currJetPosition
                    }
                    if (seenStateBefore) {
                        return savedStates
                    }
                }
            }
        }

        TODO()
    }

    fun calculateHeightUsingStates(
        numBlocksToDrop: Long,
        jetPattern: String,
        states: Map<SimulatorState, SimulatorResult>) {

        val simulator = Simulator(jetPattern)

        var blockTypeIdx = 0

        var numBlocksDropped = 0

        var currHeight = 0L

        while (true) {
            println("blockTypeIdx: $blockTypeIdx, simulator.currJetPosition: ${simulator.currJetPosition}")
            val result = states[SimulatorState(blockTypeIdx, simulator.currJetPosition)]!!
            if (numBlocksDropped + result.numBlocksDropped > numBlocksToDrop) {
                while (numBlocksDropped < numBlocksToDrop) {
                    val currBlock = blockTypeOrder[blockTypeIdx].getBlockType()
                    simulator.dropBlock(currBlock)
                    blockTypeIdx = (blockTypeIdx + 1) % 5

                    numBlocksDropped++
                }



                println("""
                    currHeight: $currHeight,
                    simulator.highestY: ${simulator.highestY}
                """.trimIndent())
                break
            } else {
                currHeight += result.heightAdded
                numBlocksDropped += result.numBlocksDropped
                blockTypeIdx = result.nextBlockTypeIdx
                simulator.currJetPosition = result.nextJetPosition
            }
        }
    }

    fun part2(input: List<String>): Unit {
        println(input)

        val jetPattern = input.first()

//        val simulatorStates = calculateSimulatorStates(jetPattern)
        val simulatorStates = mapOf(
            SimulatorState(0, 0) to SimulatorResult(numBlocksDropped=926, heightAdded=1491, nextBlockTypeIdx=1, nextJetPosition=5307),
            SimulatorState(1, 5307) to SimulatorResult(numBlocksDropped=1695, heightAdded=2671, nextBlockTypeIdx=1, nextJetPosition=5307),
        )

        println((1000000000000L - 926))
        println((1000000000000L - 926) / 1695)

        val numRepeated = (1000000000000L - 926) / 1695
        val heightToAdd = numRepeated * 2671
        val remainingBlocksToAdd = 1000000000000L - 926 - numRepeated * 1695

        println(remainingBlocksToAdd)

        val simulator = Simulator(jetPattern)
        simulator.currJetPosition = 5307
        var blockTypeIdx = 1

        repeat(remainingBlocksToAdd.toInt()) {
            val currBlock = blockTypeOrder[blockTypeIdx].getBlockType()
            simulator.dropBlock(currBlock)
            blockTypeIdx = (blockTypeIdx + 1) % 5

        }

        println(simulator.highestY.toLong() + 1491 + heightToAdd)


//        calculateHeightUsingStates(1000000000000, jetPattern, simulatorStates)

//        println(simulatorStates)

    }

    val dayString = "day17"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
    //    part1(testInput)
//        part2(testInput)

    val input = readInput("${dayString}_input")
    //    part1(input)
    part2(input)
}



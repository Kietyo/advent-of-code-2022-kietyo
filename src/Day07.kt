import kotlin.math.min

sealed class Node {
    data class DirectoryNode(
        val parentDirectoryNode: DirectoryNode?,
        val directoryName: String,
        // Directory Name -> Directory Node
        val folderNodes: MutableMap<String, DirectoryNode> = mutableMapOf(),
        // File Name -> File Node
        val fileNodes: MutableMap<String, FileNode> = mutableMapOf()
    ) : Node() {
        fun calculatePart1(): Long {
            val myTotalSize = totalSize()
            val childTotalSizes = folderNodes.asSequence().sumOf {
                it.value.calculatePart1()
            }
            return childTotalSizes + if (myTotalSize <= 100000) myTotalSize else 0
        }

        fun totalSize(): Long {
            return fileNodes.asSequence().sumOf { it.value.fileSize } +
                    folderNodes.asSequence().sumOf { it.value.totalSize() }
        }

        fun print(currLevel: Int) {
            val indentationStr = "\t".repeat(currLevel)
            val fileIndentationStr = "\t".repeat(currLevel + 1)
            println("$indentationStr- Folder: $directoryName (totalSize=${totalSize()})")
            folderNodes.forEach {
                it.value.print(currLevel + 1)
            }
            fileNodes.forEach {
                println("$fileIndentationStr- File: ${it.value}")
            }
        }
        val totalSpaceAvailable = 70000000
        val wantedUnusedSpace = 30000000

        fun calculatePart2(totalUsed: Long, minDirectorySizeToRemove: Long): Long {
            var newMin = minDirectorySizeToRemove
            val thisTotalSize = totalSize()
            val availableSpaceIfRemoved = (totalSpaceAvailable - (totalUsed - thisTotalSize))
            if (availableSpaceIfRemoved >= wantedUnusedSpace) {
                newMin = min(thisTotalSize, minDirectorySizeToRemove)
            }

            if (folderNodes.isEmpty()) return newMin

            return folderNodes.asSequence().minOf {
                it.value.calculatePart2(totalUsed, newMin)
            }
        }
    }

    data class FileNode(
        val fileName: String,
        val fileSize: Long
    ) : Node() {
    }
}

fun main() {

    fun parseRootNode(lines: List<String>): Node.DirectoryNode {
        val rootNode = Node.DirectoryNode(null, "root")
        var currNode = rootNode
        val itr = lines.listIterator()
        while (itr.hasNext()) {
            val it = itr.next()
            println(it)
            when {
                it.startsWith("$") -> {
                    val split = it.split(" ")
                    val cmd = split[1]
                    when (cmd) {
                        "cd" -> {
                            val input = split[2]
                            if (input == "/") {
                                currNode = rootNode
                            } else if (input == "..") {
                                currNode = currNode.parentDirectoryNode!!
                            } else {
                                currNode = currNode.folderNodes[input]!!
                            }
                        }
                        "ls" -> {
                            while (itr.hasNext()) {
                                val line = itr.next()
                                when {
                                    line.startsWith("$") -> {
                                        itr.previous()
                                        break
                                    }

                                    line.startsWith("dir") -> {
                                        val (_, dirName) = line.split(" ")
                                        require(!currNode.folderNodes.containsKey(dirName))
                                        currNode.folderNodes.putIfAbsent(
                                            dirName,
                                            Node.DirectoryNode(currNode, dirName)
                                        )
                                    }
                                    else -> {
                                        // This is a file node
                                        val (fileSize, fileName) = line.split(" ")
                                        require(!currNode.fileNodes.containsKey(fileName))
                                        currNode.fileNodes.putIfAbsent(fileName,
                                            Node.FileNode(fileName, fileSize.toLong()))
                                    }
                                }
                                if (line.startsWith("$")) {
                                    itr.previous()
                                    break
                                }
                            }
                        }

                        else -> TODO("Unsupported cmd: $cmd")
                    }
                }
                else -> TODO()
            }
        }
        rootNode.print(0)
        return rootNode
    }

    fun part1(lines: List<String>): Unit {
        val rootNode = parseRootNode(lines)
        println(rootNode.calculatePart1())
    }

    fun part2(lines: List<String>): Unit {
        val rootNode = parseRootNode(lines)
        val totalSize = rootNode.totalSize()
        println(rootNode.calculatePart2(totalSize, totalSize))
    }

    val dayString = "day7"

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${dayString}_test")
//    part1(testInput)
//        part2(testInput)

    val input = readInput("${dayString}_input")
//        part1(input)
        part2(input)
}

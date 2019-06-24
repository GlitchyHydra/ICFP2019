import data.*
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

fun main(args: Array<String>) {
    val mapList = LinkedList<ParsedMap>()
    for (i in 1..150) {
        val fileName = String.format("tasks/prob-%03d.desc", i)
        File(fileName).readText().split("#").let { parts ->
            mapList.add(
                ParsedMap(
                    parseMap(parts[0]),
                    parseStart(parts[1]),
                    parseObstacles(parts[2]),
                    parseBoosters(parts[3])
                ).apply {
                    maxX = this.vertices.maxBy { it.x }!!.x
                    maxY = this.vertices.maxBy { it.y }!!.y
                }
            )
        }
    }
    for (i in 1..150) {
        val map = mapList[i - 1]
        val bot = Bot(map.start, map.mapInMatrix, map.numberOfUnpainted)
        val fileName = String.format("solutions/prob-%03d.sol", i)
        println(fileName)
        val solution = bot.buildPath()
        File(fileName).writeText(solution)
    }
}

private fun Array<IntArray>.printMatrix() {
    for (i in this.size - 1 downTo 0) {
        for (j in 0 until this[0].size) {
            print("${this[i][j]} ")
        }
        println()
    }
}

private fun parseMap(input: String): List<Square> =
    parseSquares(input)

private fun parseStart(input: String): Square =
    parseSquare(input)

private fun parseObstacles(input: String): List<Obstacle> =
    if (input.isEmpty()) emptyList() else input.split(";").map { Obstacle(parseSquares(it)) }

private fun parseBoosters(input: String): List<Booster> =
    if (input.isEmpty()) emptyList() else input.split(";").map { Booster(parseSquare(it), parseBoosterType(it[0])) }

/**
 * @param input - поступает что то вроде этого (2,5),(5,5),(25,69) - потом это разбивается на "2,5),"
 */
private fun parseSquares(input: String) =
    input.split("(")
        .filter { it != "" }
        .map { parseSquare(it) }

private fun parseSquare(input: String) =
    Regex("""\d+""")
        .findAll(input)
        .map { it.value }
        .toList()
        .let { Square(it[0].toInt(), it[1].toInt()) }

fun parseBoosterType(char: Char) = when (char.toLowerCase()) {
    'b' -> BoosterType.MANIPULATOR
    'f' -> BoosterType.WHEELS
    'l' -> BoosterType.DRILL
    'x' -> BoosterType.UNKNOWN
    else -> BoosterType.UNKNOWN
}

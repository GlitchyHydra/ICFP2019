package data

data class Square(val x: Int, val y: Int)

data class Obstacles(val listOfVertices: List<Map>)

fun Obstacles.removeFromMap(map: Map) {
    for (k in 0 until listOfVertices.size) {
        val obstacle = this.listOfVertices[k]
        val top = obstacle.top
        val bottom = obstacle.bottom
        val left = obstacle.left
        val right = obstacle.right
        for (i in 0 until top - bottom) {
            for (j in left..right) {
                if (obstacle.isPointInsidePolygon(Square(j, i)) == 1)
                    map.mapInMatrix[i][j] = 0
            }
        }
    }
}

class Map(
    val vertices: List<Square>,
    val left: Int,
    val right: Int,
    val top: Int,
    val bottom: Int
) {
    private val xSize = right - 1
    private val ySize = top - 1
    val mapInMatrix = Array(top - bottom) { i -> (left..right)
       .map { j -> isPointInsidePolygon(Square(j, i)) }.toIntArray() }
    var countOfPoints = xSize * ySize

    data class NotMarkedSquares(val listOfSquares: List<Square>)
data class Booster(val square: Square, val type: BoosterType)

data class Obstacle(val squares: List<Square>)

data class ParsedMap(
    val vertices: List<Square>,
    val start: Square,
    val obstacles: List<Obstacle>,
    val boosters: List<Booster>,
    var maxX: Int = 0,
    var maxY: Int = 0
)

data class Map(
    val vertices: List<Square>,
    val left: Int,
    val right: Int,
    val top: Int,
    val bottom: Int
) {

    val mapInMatrix = Array<IntArray>(top) { i -> (left..right).map { it -> it }.toIntArray() }

    data class NotMarkedSquares(val listOfSquares: List<Square>)
}

enum class BoosterType(str: String) {
    MANIPULATOR("B"),
    WHEELS("F"),
    DRILL("L"),
    UNKNOWN("X");
}
    fun isPointInsidePolygon(point: Square): Int {
        var (x, y) = point
        var j = vertices.size - 1
        var c = false
        for (i in 0 until vertices.size) {
            val (xi, yi) = vertices[i]
            val (xj, yj) = vertices[j]
            if ((((yi <= y) && (y < yj)) || ((yj <= y) && (y < yi))) &&
                (x > (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                c = !c
            }
            j = i
        }
        return if (c) 1 else 0
    }

}



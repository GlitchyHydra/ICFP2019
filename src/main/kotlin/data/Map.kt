package data

data class Square(val x: Int, val y: Int)

data class Booster(val square: Square, val type: BoosterType)

data class Obstacle(val squares: List<Square>)

data class ParsedMap(
    val vertices: List<Square>,
    val start: Square,
    val obstacles: List<Obstacle>,
    val boosters: List<Booster>,
    var maxX: Int = 0,
    var maxY: Int = 0
) {
    val mapInMatrix: Array<IntArray> get()
    = Array(maxY) { i -> (0..maxX).map { j-> isPointInsideMap(Square(j ,i)) }.toIntArray() }
}

fun ParsedMap.isPointInsideMap(point: Square): Int {
    for (booster in boosters) {
        if (booster.square == point)
            return booster.type.ordinal + 3
    }
    if (isPointInsidePolygon(point, vertices)) {
        for (obstacle in obstacles) {
            if (isPointInsidePolygon(point, obstacle.squares))
                return 0
        }
        return 1
    }
    else return 0
}

fun isPointInsidePolygon(point: Square, vertices: List<Square>): Boolean {
    val (x, y) = point
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
    return c
}

enum class BoosterType(str: String) {
    MANIPULATOR("B"),
    WHEELS("F"),
    DRILL("L"),
    UNKNOWN("X");
}

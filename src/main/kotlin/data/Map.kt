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

/**
 * Check if on vertical border of map and in
 * bot pos before last or first square on this
 * border, if so, paint all by this way
 */
/*fun Map.checkOnBorderVert(botPosition: Square): Boolean {
    return if (botPosition.x != this.leftBottom.x) false
    else {
        botPosition.y == leftBottom.y + 1 || botPosition.y == leftTop.y - 1
    }
}
*/

fun getListOfPaintedSquares(): Nothing = TODO()

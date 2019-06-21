package Data

data class Square(val x: Int, val y: Int)

data class Obstacle(val listOfPoints: List<Square>)

data class Map(val points: List<Square>,
               val left: Int,
               val right: Int,
               val top: Int,
               val bottom: Int) {

    data class NotMarkedSquares(val listOfSquares: List<Square>)

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

fun Gener
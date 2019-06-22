package Data

import Data.Bot.Direction.*
import java.lang.StringBuilder
import kotlin.math.abs

data class Bot constructor(var position: Square) {

    private var manipulaltors = arrayListOf(
        Square(position.x + 1, position.y + 1),
        Square(position.x + 1, position.y),
        Square(position.x + 1, position.y - 1)
    )
    private var manipulatorsCount = 3

    private val matrix = arrayOf<Array<Int>>(arrayOf())
    private val colored = matrix

    constructor(list: List<String>) : this(Square(list[0].toInt(), list[1].toInt()))

    private fun move(steps: Int, horizontal: Boolean): String {
        if (steps == 0) return ""
        val direction: String
        if (horizontal) {
            position = Square(position.x, position.y + steps)
            if (steps > 0) {
                direction = "D"
                colorMove(RIGHT, abs(steps))
            } else {
                direction = "A"
                colorMove(LEFT, abs(steps))
            }
        } else {
            position = Square(position.x + steps, position.y)
            if (steps > 0) {
                direction = "W"
                colorMove(UP, abs(steps))
            } else {
                direction = "S"
                colorMove(DOWN, abs(steps))
            }

        }

        return direction.repeat(abs(steps))
    }

    private fun color(x: Int, y: Int) {
        this.colored[x][y] = 0
        for (index in 0..manipulatorsCount){
            val man = manipulaltors[index]
            this.colored[man.x][man.y] = 0
        }
    }

    private fun colorMove(direction: Direction, steps: Int) {
        for (i in 0..steps) {
            when (direction) {
                LEFT -> {
                    color(position.x - i, position.y)
                }
                RIGHT -> {
                    color(position.x + i, position.y)
                }
                DOWN -> {
                    color(position.x, position.y - i)
                }
                UP -> {
                    color(position.x, position.y + i)
                }
                ANY -> TODO()
            }
        }
    }

    /**
     * Rotate bot clockwise or counterclockwise depends on @param
     * @param left rotate parameter
     */
    fun rotate(left: Boolean): String {
        if (left) {
            for (index in 0..manipulatorsCount) {
                manipulaltors[index].clockwise()
            }
            return "E"
        } else {
            for (index in 0..manipulatorsCount) {
                manipulaltors[index].counterClockwise()
            }
            return "Q"
        }
    }

    private fun Square.clockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x + (movingSquareY - position.y)
        val newY = position.y - (movingSquareX - position.x)
        return Square(newX, newY)
    }

    private fun Square.counterClockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x - (movingSquareY - position.y)
        val newY = position.y + (movingSquareX - position.x)
        return Square(newX, newY)
    }

    fun attachManipulator(): Nothing = TODO()
    fun attachWheels(): Nothing = TODO()
    fun startDrill(): Nothing = TODO()

// val pointsCount

    enum class Direction(index: Int?) {
        LEFT(0),
        RIGHT(1),
        DOWN(2),
        UP(3),
        ANY(null)
    }

    private fun countDistancesToBorders(): Array<Int> {
        val distances = arrayOf(0, 0, 0, 0) // l, r, d, u
        val borders = arrayOf(false, false, false, false) // l, r, d, u
        val x = position.x
        val y = position.y
        var index = 0
        while (!borders.all { it }) {
            if (matrix[x + index][y] > 0 && !borders[3]) {
                distances[3]++
            } else
                borders[3] = true
            if (matrix[x - index][y] > 0 && !borders[2])
                distances[2]++
            else
                borders[2] = true
            if (matrix[x][y + index] > 0 && !borders[1])
                distances[1]++
            else
                borders[1] = true
            if (matrix[x][y - index] > 0 && !borders[0])
                distances[0]++
            else
                borders[0] = true
            index++
        }
        return distances
    }

    private fun findFarestDistance(distances: Array<Int>): Direction = when (distances.indexOf(distances.max())) {
        0 -> LEFT
        1 -> RIGHT
        2 -> DOWN
        3 -> UP
        else -> ANY
    }

    fun pathToBlank() {

    }

    fun buildPath(): String {
        val path = StringBuilder()
        var distances = countDistancesToBorders()
        var direction = findFarestDistance(distances)
        path.append(
            when (direction) {
                LEFT, RIGHT -> move(distances[1] - distances[0], true)
                UP, DOWN -> move(distances[3] - distances[2], false)
                ANY -> ""
            }
        )
        var n = 0
        while (n != 0) {

        }
        return path.toString()
    }
}
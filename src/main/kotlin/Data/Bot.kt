package Data

import Data.Bot.Direction.*
import java.lang.StringBuilder
import kotlin.math.abs

data class Bot (var position: Square) {

    private var manipulaltors = arrayListOf(
        Square(position.x + 1, position.y + 1),
        Square(position.x + 1, position.y),
        Square(position.x + 1, position.y - 1)
    )
    private var manipulatorsCount = 3
    private var cource = RIGHT
    private val matrix = arrayOf(arrayOf<Int>())
    private val colored = matrix
    private var coloredCount = 0
    private var path = StringBuilder()

    constructor(list: List<String>) : this(Square(list[0].toInt(), list[1].toInt()))

    enum class Direction(val value: Int?) {
        LEFT(0),
        UP(1),
        RIGHT(2),
        DOWN(3),
        ANY(null);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }

    // Перемещение бота
    private fun move(steps: Int, horizontal: Boolean) {
        if (steps == 0) return
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

        path.append(direction.repeat(abs(steps)))
    }

    private fun moveLeft() = move(-1, true)

    private fun moveRight() = move(1, true)

    private fun moveUp() = move(1, false)

    private fun moveDown() = move(-1, false)

    // Закрашивание полей бота и егоо манипулятороово
    private fun color(x: Int, y: Int) {
        if (colored[x][y] != 0) coloredCount++
        colored[x][y] = 0
        for (index in 0..manipulatorsCount) {
            val man = manipulaltors[index]
            if (colored[man.x][man.y] != 0) coloredCount++
            colored[man.x][man.y] = 0
        }
    }

    // Закрашивание полей по движению
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
                ANY -> TODO() // do nothing
            }
        }
    }

    // Поворот бота
    fun rotate(clockwise: Boolean) {
        path.append(
            if (clockwise) {
                for (index in 0..manipulatorsCount) {
                    manipulaltors[index].clockwise()
                }
                "E"
            } else {
                for (index in 0..manipulatorsCount) {
                    manipulaltors[index].counterClockwise()
                }
                "Q"
            }
        )
    }

    private fun Square.clockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x + (movingSquareY - position.y)
        val newY = position.y - (movingSquareX - position.x)
        cource = Direction.fromInt((cource.value!! + 1) % 4)
        color(newX, newY)
        return Square(newX, newY)
    }

    private fun Square.counterClockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x - (movingSquareY - position.y)
        val newY = position.y + (movingSquareX - position.x)
        cource = Direction.fromInt((cource.value!! - 1) % 4)
        color(newX, newY)
        return Square(newX, newY)
    }

    // Выравнивание бота отнсительно направления хода
    private fun normalizeAngular(direction: Direction) {
        if (cource.ordinal == direction.ordinal) return
        val diff = cource.ordinal - direction.ordinal
        when (abs(diff)) {
            3, 1 -> rotate(diff < 0)
            2 -> {
                rotate(true)
                rotate(true)
            }
        }
    }

    fun attachManipulator(): Nothing = TODO()
    fun attachWheels(): Nothing = TODO()
    fun startDrill(): Nothing = TODO()
    fun createClone(): Nothing = TODO()

    // Расчет расстояния от бота до границ
    // Не изменяется матрица = нет учета закрашеных ячеек
    private fun countDistancesToBorders(): Array<Int> {
        val distances = arrayOf(0, 0, 0, 0) // l, u, r, d
        val borders = arrayOf(false, false, false, false) // l, u, r, d
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

    // Нахождение наидлинейшего пути относительно бота
    private fun findFarDistance(distances: Array<Int>): Direction = when (distances.max()) {
        0 -> ANY
        else -> Direction.fromInt(distances.indexOf(distances.max()))
    }

    // При случае, когда бот окружен закрашенными ячейками(Direction = ANY) находить путь до пустых ячеек
    fun goToBlank() {

    }

    // Построение пути и вывод строоки
    fun buildPath() : String {
        var distances = countDistancesToBorders()
        var direction = findFarDistance(distances)
        // Нормализация относительно выбранного пути действия для максимальногоо заполнения поля
        path.append(
            when (direction) {
                LEFT, RIGHT -> move(distances[1] - distances[0], true)
                UP, DOWN -> move(distances[3] - distances[2], false)
                ANY -> TODO()
            }
        )
        while (coloredCount != 0) {
            // REFORMAT
            when (direction) {
                UP -> {
                    if (matrix[position.x].size >= position.y + 2) {
                        if (matrix[position.x][position.y + 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveUp()
                    }
                }
                RIGHT -> {
                    if (matrix.size >= position.x + 2) {
                        if (matrix[position.x + 2][position.y] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveRight()
                    }
                }
                DOWN -> {
                    if (position.y - 2 >= 0) {
                        if (matrix[position.x][position.y - 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveDown()
                    }
                }
                LEFT -> {
                    if (position.x - 2 >= 0) {
                        if (matrix[position.x - 2][position.y] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveLeft()
                    }
                }
                ANY -> goToBlank()
            }
            normalizeAngular(direction)
        }
        return path.toString()
    }
}
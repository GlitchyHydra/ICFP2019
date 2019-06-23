package data

import data.Bot.Direction.*
import shortestPath
import unrollPath
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

data class Bot(var position: Square, val matrix: Array<IntArray>, val blankCount: Int) {
    /*
    0 - obstacle
    1 - blank
    2 - colored
    3.. - boosters
     */
    private var manipulators = arrayListOf(
        Square(position.x + 1, position.y + 1),
        Square(position.x + 1, position.y),
        Square(position.x + 1, position.y - 1)
    )
    private var course = RIGHT
    private var coloredCount = 0
    private var path = StringBuilder()

    enum class Direction(val value: Int) {
        LEFT(0),
        UP(1),
        RIGHT(2),
        DOWN(3),
        ANY(-1);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }

    // Перемещение бота
    private fun move(steps: Int, horizontal: Boolean) {
        if (steps == 0) return
        val direction: String
        if (horizontal) {
            if (steps > 0) {
                direction = "D"
                normalizeAngular(RIGHT)
                colorMove(RIGHT, abs(steps))
            } else {
                direction = "A"
                normalizeAngular(LEFT)
                colorMove(LEFT, abs(steps))
            }
            position = Square(position.x + steps, position.y)
            moveManipulators(steps, 0)
        } else {
            if (steps > 0) {
                direction = "W"
                normalizeAngular(UP)
                colorMove(UP, abs(steps))
            } else {
                direction = "S"
                course = DOWN
                normalizeAngular(DOWN)
                colorMove(DOWN, abs(steps))
            }
            position = Square(position.x, position.y + steps)
            moveManipulators(0, steps)
        }

        path.append(direction.repeat(abs(steps)))
    }

    private fun moveLeft() = move(-1, true)

    private fun moveRight() = move(1, true)

    private fun moveUp() = move(1, false)

    private fun moveDown() = move(-1, false)

    private fun moveManipulators(stepX: Int, stepY: Int) {
        for (index in 0 until manipulators.size) {
            val man = manipulators[index]
            manipulators[index] = Square(man.x + stepX, man.y + stepY)
        }
    }

    // Закрашивание полей бота и его манипуляторов
    private fun color(x: Int, y: Int) {
        if (matrix.size > y && x >= 0 && y >= 0 && matrix[y].size > x) {
            if (matrix[y][x] != 0) {
                coloredCount++
                matrix[y][x] = 2
            }
        }
        for (man in manipulators) {
            if (man.y in 0 until matrix.size && man.x in 0 until matrix[man.y].size) {
                if (matrix[man.y][man.x] != 0) {
                    coloredCount++
                    matrix[man.y][man.x] = 2
                }
            }
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
                for (index in 0 until manipulators.size) {
                    val man = manipulators[index]
                    color(man.x, man.y)
                    manipulators[index] = man.clockwise()
                }
                course = Direction.fromInt((course.value + 1) % 4)
                "E"
            } else {
                for (index in 0 until manipulators.size) {
                    val man = manipulators[index]
                    color(man.x, man.y)
                    manipulators[index] = man.counterClockwise()
                }
                course = Direction.fromInt((course.value - 1) % 4)
                "Q"
            }
        )
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

    // Выравнивание бота отнсительно направления хода
    private fun normalizeAngular(direction: Direction) {
        if (direction.value == -1 || course.value == direction.value) return
        val diff = course.value - direction.value
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
        val colored = arrayOf(0, 0, 0, 0)
        val borders = arrayOf(false, false, false, false) // l, u, r, d
        val x = position.x
        val y = position.y
        var index = 1
        while (!borders.all { it }) {
            if (matrix[y].size > x + index) { // R
                if (matrix[y][x + index] > 0 && !borders[2]) {
                    distances[2]++
                    if (matrix[y][x + index] == 2) colored[2]++
                } else
                    borders[2] = true
            } else
                borders[2] = true
            if (x - index >= 0) { // L
                if (matrix[y][x - index] > 0 && !borders[0]) {
                    distances[0]++
                    if (matrix[y][x - index] == 2) colored[0]++
                } else
                    borders[0] = true
            } else
                borders[0] = true
            if (matrix.size > y + index) { // U
                if (matrix[y + index][x] > 0 && !borders[1]) {
                    distances[1]++
                    if (matrix[y + index][x] == 2) colored[1]++
                } else
                    borders[1] = true
            } else
                borders[1] = true
            if (y - index >= 0) { // D
                if (matrix[y - index][x] > 0 && !borders[3]) {
                    distances[3]++
                    if (matrix[y - index][x] == 2) colored[3]++
                } else
                    borders[3] = true
            } else
                borders[3] = true
            index++
        }
        colored.forEachIndexed { ind, it ->
            if (it == distances[ind])
                distances[ind] = 0
        }
        return distances
    }

    // Нахождение наидлинейшего пути относительно бота
    private fun findFarDistance(distances: Array<Int>): Direction =
        when (distances.max()) {
            0 -> ANY
            else -> Direction.fromInt(distances.indexOf(distances.max()))
        }

    // При случае, когда бот окружен закрашенными ячейками(Direction = ANY) находить путь до пустых ячеек
    private fun goToBlank(): Boolean {
        val blank = bfs()
        if (blank.x == -1 && blank.y == -1) return false
        for (square in findPathToBlank(blank)) {
            val diffX = square.x - position.x
            val diffY = square.y - position.y
            when {
                diffX == 0 -> {
                    when {
                        diffY > 0 -> moveUp()
                        diffY < 0 -> moveDown()
                    }
                }
                diffY == 0 -> {
                    when {
                        diffX > 0 -> moveRight()
                        diffX < 0 -> moveLeft()
                    }
                }
            }
        }
        return true
    }

    fun findPathToBlank(goal: Square) =
        shortestPath(this.position, matrix).unrollPath(goal)

    // Поиск незакрашенной точки
    private fun bfs(): Square {
        val visited: Array<BooleanArray> = Array(matrix.size) { BooleanArray(matrix[0].size) { false } }
        val queue: Queue<Square> = ArrayDeque()
        queue.add(position)
        visited[position.y][position.x] =
            true // Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1
        while (!queue.isEmpty()) {
            val el = queue.poll()
            val adjacent = findAdjacent(el)
            for (adjEl in adjacent) {
                if (matrix[adjEl.y][adjEl.x] == 1) return Square(adjEl.x, adjEl.y)
                if (!visited[adjEl.y][adjEl.x]) {
                    visited[adjEl.y][adjEl.x] = true
                    queue.add(adjEl)
                }
            }
        }
        return Square(-1, -1)
    }

    // Поиск соседей
    private fun findAdjacent(point: Square): ArrayList<Square> {
        val adjacent: ArrayList<Square> = arrayListOf()
        if (matrix.size - 1 > point.y + 1 && matrix[point.y + 1][point.x] > 0)
            adjacent.add(Square(point.x, point.y + 1))
        if (point.y - 1 >= 0 && matrix[point.y - 1][point.x] > 0)
            adjacent.add(Square(point.x, point.y - 1))
        if (matrix[point.y].size - 1 > point.x + 1 && matrix[point.y][point.x + 1] > 0)
            adjacent.add(Square(point.x + 1, point.y))
        if (point.x - 1 >= 0 && matrix[point.y][point.x - 1] > 0)
            adjacent.add(Square(point.x - 1, point.y))
        return adjacent
    }

    // Нормализация относительно выбранного пути действия для максимальногоо заполнения поля
    private fun normalizePosition(direction: Direction, distances: Array<Int>) {
        val diffX = (distances[0] - distances[2]) / 2
        val diffY = (distances[1] - distances[3]) / 2
        when (direction) {
            LEFT, RIGHT -> move(diffY, false)
            UP, DOWN -> move(diffX, true)
            ANY -> TODO()
        }

    }

    // Построение пути и вывод строоки
    fun buildPath(): String {
        var stop = true
        var distances = countDistancesToBorders()
        var direction = findFarDistance(distances)
        normalizePosition(direction, distances)
        while (stop) {
            // REFORMAT
            when (direction) {
                UP -> {
                    moveUp()
                    if (matrix.size - 1 >= position.y + 2) {
                        if (matrix[position.y + 2][position.x] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        }
                    } else {
                        distances = countDistancesToBorders()
                        direction = findFarDistance(distances)
                    }
                }
                RIGHT -> {
                    moveRight()
                    if (matrix[position.y].size - 1 > position.x + 2) {
                        if (matrix[position.y][position.x + 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        }
                    } else {
                        distances = countDistancesToBorders()
                        direction = findFarDistance(distances)
                    }
                }
                DOWN -> {
                    moveDown()
                    if (position.y - 2 >= 0) {
                        if (matrix[position.y - 2][position.x] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        }
                    } else {
                        distances = countDistancesToBorders()
                        direction = findFarDistance(distances)
                    }
                }
                LEFT -> {
                    moveLeft()
                    if (position.x - 2 >= 0) {
                        if (matrix[position.y][position.x - 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        }
                    } else {
                        distances = countDistancesToBorders()
                        direction = findFarDistance(distances)
                    }
                }
                ANY -> {
                    stop = goToBlank()
                    distances = countDistancesToBorders()
                    direction = findFarDistance(distances)
                }

            }
            normalizeAngular(direction)
        }
        return path.toString()
    }

    private fun printMatrix(direction: Direction) {
        for (row in matrix.size - 1 downTo 0) {
            for (column in matrix[row])
                print("$column ")
            println()

        }
        println("+++++++MOVE:$direction")
    }
}
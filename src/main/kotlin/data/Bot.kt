package data

import data.Bot.Direction.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

data class Bot(var position: Square, val matrix: Array<Array<Int>>, val blankCount: Int) {
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

    // Закрашивание полей бота и его манипулятороово
    private fun color(x: Int, y: Int) {
        if (matrix[y][x] != 0) {
            coloredCount++
            matrix[y][x] = 2
        }
        for (man in manipulators) {
            if (matrix[man.y][man.x] != 0) {
                coloredCount++
                matrix[man.y][man.x] = 2
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
                for (man in manipulators) {
                    man.clockwise()
                }
                "E"
            } else {
                for (man in manipulators) {
                    man.counterClockwise()
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
        course = Direction.fromInt((course.value + 1) % 4)
        color(newX, newY)
        return Square(newX, newY)
    }

    private fun Square.counterClockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x - (movingSquareY - position.y)
        val newY = position.y + (movingSquareX - position.x)
        course = Direction.fromInt((course.value - 1) % 4)
        color(newX, newY)
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
        var index = 0
        while (!borders.all { it }) {
            if (matrix[y][x + index] > 0 && !borders[3]) {
                distances[3]++
                if (matrix[y][x + index] == 2) colored[3]++
            } else
                borders[3] = true
            if (matrix[y][x - index] > 0 && !borders[2]) {
                distances[2]++
                if (matrix[y][x - index] == 2) colored[2]++
            } else
                borders[2] = true
            if (matrix[y + index][x] > 0 && !borders[1]) {
                distances[1]++
                if (matrix[y + index][x] == 2) colored[1]++
            } else
                borders[1] = true
            if (matrix[y - index][x] > 0 && !borders[0]) {
                distances[0]++
                if (matrix[y - index][x] == 2) colored[0]++
            } else
                borders[0] = true
            index++
        }
        colored.forEachIndexed { _, it ->
            if (it == distances[index])
                distances[index] = 0
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
    private fun goToBlank() {
        val blank = bfs()
        if (blank.x == -1 && blank.y == -1) TODO() // ???
        for (square in findPathToBlank(blank)) {
            val diffX = square.x - position.x
            val diffY = square.y - position.y
            when {
                diffX == 0 ->{
                    when{
                        diffY > 0 -> moveUp()
                        diffY < 0 -> moveDown()
                    }
                }
                diffY == 0 -> {
                    when{
                        diffX > 0 -> moveRight()
                        diffX < 0 -> moveLeft()
                    }
                }
            }
        }
    }

    // Поиск пути от позиции до точки (A* algorithm)
    private fun findPathToBlank(blank: Square): ArrayList<Square> {
        val pathToBlank: ArrayList<Square> = arrayListOf()

        return pathToBlank
    }

    // Поиск незакрашенной точки
    private fun bfs(): Square {
        val visited: Array<BooleanArray> = arrayOf(booleanArrayOf())
        val queue: Queue<Square> = ArrayDeque()
        queue.add(position)
        visited[position.y][position.x] = true
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
        if (matrix.size >= point.y + 1 && matrix[point.y + 1][point.x] > 0)
            adjacent.add(Square(point.y + 1, point.x))
        if (point.y - 1 >= 0 && matrix[point.y - 1][point.x] > 0)
            adjacent.add(Square(point.y - 1, point.x))
        if (matrix[point.y].size >= point.x + 1 && matrix[point.y][point.x + 1] > 0)
            adjacent.add(Square(point.y, point.x + 1))
        if (point.x - 1 >= 0 && matrix[point.y][point.x - 1] > 0)
            adjacent.add(Square(point.y, point.x - 1))
        return adjacent
    }

    // Нормализация относительно выбранного пути действия для максимальногоо заполнения поля
    private fun normalizePosition(direction: Direction, distances: Array<Int>) {
        path.append(
            when (direction) {
                LEFT, RIGHT -> move(distances[1] - distances[0], true)
                UP, DOWN -> move(distances[3] - distances[2], false)
                ANY -> TODO()
            }
        )
    }

    // Построение пути и вывод строоки
    fun buildPath(): String {
        var distances = countDistancesToBorders()
        var direction = findFarDistance(distances)
        normalizePosition(direction, distances)
        while (coloredCount != blankCount) {
            // REFORMAT
            when (direction) {
                UP -> {
                    if (matrix[position.y].size >= position.x + 2) {
                        if (matrix[position.y + 2][position.x] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveUp()
                    }
                }
                RIGHT -> {
                    if (matrix.size >= position.y + 2) {
                        if (matrix[position.y][position.x + 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveRight()
                    }
                }
                DOWN -> {
                    if (position.x - 2 >= 0) {
                        if (matrix[position.y - 2][position.x] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveDown()
                    }
                }
                LEFT -> {
                    if (position.y - 2 >= 0) {
                        if (matrix[position.y][position.x - 2] == 0) {
                            distances = countDistancesToBorders()
                            direction = findFarDistance(distances)
                        } else
                            moveLeft()
                    }
                }
                ANY -> {
                    goToBlank()
                    distances = countDistancesToBorders()
                    direction = findFarDistance(distances)
                }
            }
            normalizeAngular(direction)
        }
        return path.toString()
    }
}
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

    fun checkDifference(all: Int, painted: Int): Int = if (all - painted == 0) 0 else all

    fun countDistances(square: Square, offset: Square): Int {
        val (xOffset, yOffset) = offset
        var (x, y) = square
        var painted = 0
        var all = 0
        while (y + yOffset in 0 until matrix.size && x + xOffset in 0 until matrix[0].size) {
            y += yOffset
            x += xOffset
            when {
                matrix[y][x] == 1 -> all++
                matrix[y][x] == 2 -> {
                    painted++
                    all++
                }
                matrix[y][x] == 0 -> {
                    return checkDifference(all, painted)
                }
            }
        }
        return checkDifference(all, painted)
    }

    // Расчет расстояния от бота до границ
    // Не изменяется матрица = нет учета закрашеных ячеек
    private fun countDistancesToBorders(): Array<Int> {
        val distances = arrayOf(0, 0, 0, 0) // l, u, r, d
        var index = 0
        for (i in -1..1) {
            for (j in 1 downTo -1) {
                if (abs(i + j) == 1) {
                    distances[index] = countDistances(position, Square(i, j))
                    when (index) {
                        1 -> index = 3
                        3 -> index = 2
                        else -> index++
                    }
                }
            }
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
        visited[position.y][position.x] = true
        while (!queue.isEmpty()) {
            val el = queue.poll()
            val adjacent = findAdjacent(el)
            for (adjEl in adjacent) {
                if (matrix[adjEl.y][adjEl.x] == 1) {
                    return Square(adjEl.x, adjEl.y)
                }
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
        val (x, y) = point
        for (i in x - 1..x + 1) {
            for (j in y - 1..y + 1) {
                if ((x == i || y == j) && point != Square(i, j)) {
                    if (j in 0 until matrix.size && i in 0 until matrix[0].size)
                        adjacent.add(Square(i, j))
                }
            }
        }
        return adjacent
    }

    // Нормализация относительно выбранного пути действия для максимальногоо заполнения поля
    private fun normalizePosition(direction: Direction, distances: Array<Int>) {
        val diffX = abs(distances[0] - distances[2]) / 2
        val diffY = abs(distances[1] - distances[3]) / 2
        when (direction) {
            LEFT, RIGHT -> move(diffY, false)
            UP, DOWN -> move(diffX, true)
            ANY -> TODO()
        }

    }

    fun canReach(botBody: Square, target: Square): Boolean {
        //поиск всех клеток, которые пересекает линия, проходящая от центра botBody до центра target
        val intersectedSquares = intersectedSquares(botBody, target)

        //проверка, есть ли в пересеченных клетках препятсвие
        intersectedSquares.forEach {
            if (matrix[it.x][it.y] == 0)
                return false
        }
        return true
    }

    fun intersectedSquares(botBody: Square, target: Square): List<Square> {
        //составляется полный список клеток в прямоугольнике с диагональю botBody - target
        var fullList = mutableListOf<Square>()
        val rangeX = if (botBody.x <= target.x) botBody.x..target.x else botBody.x downTo target.x
        val rangeY = if (botBody.y <= target.y) botBody.y..target.y else botBody.y downTo target.y
        for (x in (rangeX))
            for (y in (rangeY)) {
                fullList.add(Square(x, y))
            }

        val middleX = (botBody.x + target.x).toFloat() / 2
        val middleY = (botBody.y + target.y).toFloat() / 2

        //отсеиваются из полного списка не подходящие клетки
        return if (botBody.x <= target.x && botBody.y <= target.y)
            fullList.filter { (it.x <= middleX) && (it.y <= middleY) || (it.x >= middleX) && (it.y >= middleY) }
        else if (botBody.x <= target.x && botBody.y >= target.y)
            fullList.filter { (it.x <= middleX) && (it.y >= middleY) || (it.x >= middleX) && (it.y <= middleY) }
        else if (botBody.x >= target.x && botBody.y <= target.y)
            fullList.filter { (it.x >= middleX) && (it.y <= middleY) || (it.x <= middleX) && (it.y >= middleY) }
        else
            fullList.filter { (it.x >= middleX) && (it.y >= middleY) || (it.x <= middleX) && (it.y <= middleY) }
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
                    if (matrix.size - 1 >= position.y + 1) {
                        if (matrix[position.y + 1][position.x] == 0) {
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
                    if (matrix[position.y].size - 1 > position.x + 1) {
                        if (matrix[position.y][position.x + 1] == 0) {
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
                    if (position.y >= 1 && position.x >= 0) {
                        if (matrix[position.y - 1][position.x] == 0) {
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
                    if (position.x - 1 >= 0) {
                        if (matrix[position.y][position.x - 1] == 0) {
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

}
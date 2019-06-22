package data

data class Bot private constructor(var position: Square) {

    private var manipulatorLeft = Square(position.x + 1, position.y + 1)
    private var manipulatorMiddle  = Square(position.x + 1, position.y)
    private var manipulatorRight  = Square(position.x + 1, position.y - 1)
    private var manipulatorsCount = 3

    constructor(list: List<String>) : this(Square(list[0].toInt(), list[1].toInt()))

    fun moveRight(map: Map): String {
        val newX = position.x + 1
        if (newX in map.left..map.right)
            position = Square(newX, position.y)
        return "D"
    }

    fun moveLeft(map: Map): String {
        val newX = position.x - 1
        if (newX in map.left..map.right)
            position = Square(newX, position.y)
        return "A"
    }

    fun moveUp(map: Map): String {
        val newY = position.y + 1
        if (newY in map.left..map.right)
            position = Square(position.x, newY)
        return "W"
    }

    fun moveDown(map: Map): String {
        val newY = position.x - 1
        if (newY in map.left..map.right)
            position = Square(position.x, newY)
        return "S"
    }

    /**
     * по часовой
     */
    fun rotateE(): String {
        manipulatorLeft = manipulatorLeft.clockwise()
        manipulatorMiddle = manipulatorMiddle.clockwise()
        manipulatorRight = manipulatorRight.clockwise()
        return "E"
    }

    private fun Square.clockwise(): Square {
        val movingSquareX = this.x
        val movingSquareY = this.y
        val newX = position.x + (movingSquareY - position.y)
        val newY = position.y - (movingSquareX - position.x)
        return Square(newX, newY)
    }

    /**
     * против часовой
     */
    fun rotateQ(): String {
        manipulatorLeft = manipulatorLeft.counterClockwise()
        manipulatorMiddle = manipulatorMiddle.counterClockwise()
        manipulatorRight = manipulatorRight.counterClockwise()
        return "Q"
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
}
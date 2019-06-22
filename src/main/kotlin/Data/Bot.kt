package Data

data class Bot(var position: Square) {

    private val manipulator1 get() = Square(position.x + 1, position.y + 1)
    private val manipulator2 get() = Square(position.x + 1, position.y)
    private val manipulator3 get() = Square(position.x + 1, position.y - 1)
    private var manipulatorsCount = 3

    fun moveRight(map: Map) {
        val newX = position.x + 1
        if (newX in map.left..map.right)
        position = Square(newX, position.y)
    }

    fun moveLeft(map: Map) {
        val newX = position.x - 1
        if (newX in map.left..map.right)
            position = Square(newX, position.y)
    }

    fun moveUp(map: Map) {
        val newY = position.y + 1
        if (newY in map.left..map.right)
            position = Square(position.x, newY)
    }

    fun moveDown(map: Map) {
        val newY = position.x - 1
        if (newY in map.left..map.right)
            position = Square(position.x, newY)
    }

    /**
     * по часовой
     */
    fun rotateE() {

    }

    /**
     * против часовой
     */
    fun rotateQ() {

    }

    fun attachManipulator(): Nothing = TODO()
    fun attachWheels(): Nothing = TODO()
    fun startDrill(): Nothing = TODO()
}
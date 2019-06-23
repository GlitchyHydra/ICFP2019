import data.Square
import java.util.*

class SquareInfo(
    val vertex: Square,
    val distance: Int,
    val prev: Square?
) : Comparable<SquareInfo> {
    override fun compareTo(other: SquareInfo): Int {
        return distance.compareTo(other.distance)
    }
}

fun Square.getNeighbors(map: Array<IntArray>): List<Square> {
    val (x, y) = this
    val adj = mutableListOf<Square>()
    for (i in x - 1..x + 1) {
        for (j in y - 1..y + 1) {
            if ((x == i || y == j) && this != Square(i, j)) {
                if (j in 0..map.size && i in 0..map[j].size)
                    adj.add(Square(x, y))
            }
        }
    }
    return adj
}

fun shortestPath(from: Square, map: Array<IntArray>): Map<Square, SquareInfo> {
    val info = mutableMapOf<Square, SquareInfo>()
    for (i in 0 until map.size) {
        for (j in 0 until map[i].size) {
            val square = Square(j, i)
            info[square] = SquareInfo(square, Int.MAX_VALUE, null)
        }
    }
    val fromInfo = SquareInfo(from, 0, null)
    val queue = PriorityQueue<SquareInfo>()
    queue.add(fromInfo)
    info[from] = fromInfo
    while (queue.isNotEmpty()) {
        val currentInfo = queue.poll()
        val currentVertex = currentInfo.vertex
        for (vertex in currentVertex.getNeighbors(map)) {
            val weight = 1
            if (weight != null) {
                val newDistance = info[currentVertex]!!.distance + weight
                if (info[vertex]!!.distance > newDistance) {
                    val newInfo = SquareInfo(vertex, newDistance, currentVertex)
                    queue.add(newInfo)
                    info[vertex] = newInfo
                }
            }
        }
    }
    return info
}

fun Map<Square, SquareInfo>.unrollPath(to: Square): List<Square> {
    val result = mutableListOf<Square>()
    var current: Square? = to
    while (current != null) {
        result += current
        current = this[current]?.prev
    }
    result.reverse()
    return result
}
import Data.Square
import Data.Map
import java.io.File

fun main(args: Array<String>) {
    for (i in 1..150) {
        val fileName = "tasks/prob-${numberToString(i)}.desc"
        val fileList = File(fileName).readLines()
            .fold("") {prev, next -> prev + next}.split("#")
        println(fileList[0])
        val map = separatePairs(fileList[0])
        //answer
        //next
        println(map.top)
    }

}


fun separatePairs(str: String): Map {
    var foldedList = str
    foldedList = foldedList.replace(")", "")
    foldedList = foldedList.replace("(", "")
    val listOfCoord = foldedList.split(",")
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    val mapList = mutableListOf<Square>()
    var i = 1
    while(i < listOfCoord.size) {
        val x = listOfCoord[i - 1].toInt()
        val y = listOfCoord[i].toInt()
        if (x < minX) minX = x
        if (x > maxX) maxX = x
        if (y < minY) minY = y
        if (y > maxY) maxY = y
        mapList.add(Square(x, y))
        i += 2
    }
    return Map(mapList as List<Square>, minX, maxX, maxY, minY)
}

fun getMapInfo(fileList: List<String>): String {
    val str = StringBuilder()
    for (i in 0..fileList.lastIndex) {
        val check = fileList[i].contains("#")
        val line = if (!check) fileList[i]
        else fileList[i].split("#")[0]
        str.append(line)
        if (check) break
    }
    return str.toString()
}

fun numberToString(number: Int) = when {
        number < 10 -> "00$number"
        number in 10..99 -> "0$number"
        else -> "$number"
    }

package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Grid
import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.surroundingPoints
import com.denormans.adventofcode.utils.toGrid
import com.denormans.adventofcode.utils.withCount

fun main() {
  val grid = loadStrings(15, forTest = false).map { it.map { it.toString().toInt() } }.toGrid()
  grid.displayGrid("")

  problemOne(grid)
  problemTwo(grid)
}

private fun problemOne(grid: Grid<Int>) {
  val pathToEnd = grid.findPathToEnd()
  println("Path to end: $pathToEnd")

  println("Problem 1: ${pathToEnd.cost}")
}

private fun problemTwo(grid: Grid<Int>) {

  println("Problem 2:")
}

private data class Path(val currentPoint: Point, val pointCost: Int, val parentPath: Path? = null) : Comparable<Path> {
  val cost: Int by lazy { (parentPath?.cost ?: 0) + pointCost }
  val points: List<Point> by lazy { (parentPath?.points ?: emptyList()) + currentPoint }
  val isCycle
    get() = points.withCount().filterValues { it > 1 }.isNotEmpty()

  private val hashCode: Int by lazy { 13 * (parentPath?.hashCode ?: 0) + currentPoint.hashCode() }

  override fun toString() = points.joinToString(" ")

  override fun equals(other: Any?) = other is Path && points == other.points

  override fun hashCode() = hashCode

  override fun compareTo(other: Path): Int {
    return when {
      cost < other.cost -> -1
      cost > other.cost -> 1
      currentPoint.distanceFromOriginSquared > other.currentPoint.distanceFromOriginSquared -> -1
      currentPoint.distanceFromOriginSquared < other.currentPoint.distanceFromOriginSquared -> 1
      points.size < other.points.size -> -1
      points.size > other.points.size -> 1
      else -> points.reversed().zip(other.points.reversed()).fold(0) { acc, (p1, p2) ->
        when {
          acc != 0 -> acc
          p1.distanceFromOriginSquared < p2.distanceFromOriginSquared -> 1
          p1.distanceFromOriginSquared > p2.distanceFromOriginSquared -> -1
          else -> 0
        }
      }
    }
  }
}

private fun Grid<Int>.findPathToEnd(): Path {
  val start = 0 by 0
  val end = maxPoint

  val seenPaths = mutableSetOf<Path>()
  val pathsToEnd = sortedSetOf(Path(start, 0))

  var count = 0
  while (pathsToEnd.isNotEmpty()) {
    val lowestCostPath = pathsToEnd.first()

    count += 1
    if (count % 200 == 0) {
      println("Lowest cost path of ${pathsToEnd.size} (${lowestCostPath.cost}): $lowestCostPath")
    }

    require(pathsToEnd.remove(lowestCostPath)) { "PathToEnd not found: $lowestCostPath" }

    for (newPath in findNextPaths(lowestCostPath)) {
      if (newPath.currentPoint == end) {
        // must be the shortest path
        return newPath
      }

      if (seenPaths.add(newPath)) {
        pathsToEnd.add(newPath)
      }
    }
  }

  throw Exception("No path found")
}

private fun Grid<Int>.findNextPaths(currentPath: Path) =
  currentPath.currentPoint.surroundingPoints(maxPoint)
    .map { toPath(currentPath, it) }
    .filterNot { it.isCycle }

private fun Grid<Int>.toPath(currentPath: Path, newPoint: Point) =
  Path(newPoint, this[newPoint], currentPath)

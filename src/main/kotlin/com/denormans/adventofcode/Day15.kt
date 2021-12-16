package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Grid
import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.surroundingPoints
import com.denormans.adventofcode.utils.toGrid
import com.denormans.adventofcode.utils.withCount
import java.lang.Integer.min
import kotlin.random.Random

fun main() {
  val grid = loadStrings(15, forTest = false).map { it.map { it.toString().toInt() } }.toGrid()
  grid.displayGrid("")

  problemOne(grid)
  problemTwo(grid)
}

private fun problemOne(grid: Grid<Int>) {
  val pathToEnd = grid.findPathToEnd()
  println("Path to end: $pathToEnd")
//  val cost = pathToEnd.subList(1, pathToEnd.size).sumOf { grid[it] }
  val cost = pathToEnd.cost

  println("Problem 1: $cost")
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

  fun hasPoint(point: Point): Boolean = currentPoint == point || (parentPath?.hasPoint(point) ?: false)

  override fun toString() = "$cost: " + points.joinToString(" ")

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

private fun Grid<Int>.findShortestPathToEnd(): List<Point> {
  val startingPoint = 0 by 0
  val end = maxPoint

  var bestCost = Int.MAX_VALUE
  fun findShortestPathToEnd(currentPoint: Point, currentCost: Int): Pair<List<Point>, Int>? {
    if (currentPoint == end) {
      bestCost = min(bestCost, currentCost)
      println("best: $bestCost")
      return listOf(currentPoint) to currentCost
    }

//    if (Random.nextInt() % 1000 == 0) {
//      println("findShortestPath: $path")
//    }

    val (path, cost) = currentPoint.surroundingPoints(maxPoint)
      .filter { newPoint -> newPoint.x >= currentPoint.x && newPoint.y >= currentPoint.y }
//      .filterNot { newPoint -> path.hasPoint(newPoint) }
      .map { newPoint -> newPoint to currentCost + this[newPoint] }
      .filter { (_, newCost) -> newCost < bestCost }
      .map { (newPoint, newCost) -> findShortestPathToEnd(newPoint, newCost) }
      .filterNotNull()
      .minByOrNull { (_, cost) -> cost } ?: return null

    return path + currentPoint to cost
  }

  return findShortestPathToEnd(startingPoint, 0)?.first ?: throw IllegalStateException("No path found!")
}

private fun Grid<Int>.findPathToEnd(): Path {
  val start = 0 by 0
  val end = maxPoint

  val seenPaths = mutableSetOf<Path>()
  val pathsToEnd = sortedSetOf(Path(start, 0))

  while (pathsToEnd.isNotEmpty()) {
    val lowestCostPath = pathsToEnd.first()

    if (Random.nextInt() % 10000 == 0) {
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
    .filterNot { currentPath.hasPoint(it) }
    .map { toPath(currentPath, it) }

private fun Grid<Int>.toPath(currentPath: Path, newPoint: Point) =
  Path(newPoint, this[newPoint], currentPath)

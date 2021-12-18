package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Grid
import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.surroundingPoints
import com.denormans.adventofcode.utils.toGrid
import com.denormans.adventofcode.utils.withCount
import java.lang.Math.min
import kotlin.random.Random

fun main() {
  val grid = loadStrings(15, forTest = true).map { it.map { it.toString().toInt() } }.toGrid()
  grid.displayGrid("")
//  grid.chunked(10).values.forEachIndexed { rowIndex, row ->
//    row.forEachIndexed { chunkIndex, chunk ->
//      println("$rowIndex:$chunkIndex")
//      chunk.displayGrid("")
//    }
//  }

  problemOne(grid)
  problemTwo(grid)
}

private fun problemOne(grid: Grid<Int>) {
  val pathToEnd = grid.findShortestPathToEnd()
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

  fun tail() = copy(parentPath = null)

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

private fun Grid<Int>.findShortestPathToEnd(): Path {
  val startingPoint = 0 by 0
  val end = maxPoint

  val bestPathToEndByStartingPoint = mutableMapOf<Point, Path>()

  var bestCost = Int.MAX_VALUE
  fun findShortestPathToEnd(forwardPath: Path): Path? {
    val forwardPathCurrentPoint = forwardPath.currentPoint
    if (forwardPathCurrentPoint == end) {
      bestCost = min(bestCost, forwardPath.cost)
      println("best: $bestCost")
      return forwardPath
    }

    if (forwardPathCurrentPoint in bestPathToEndByStartingPoint) {
      return bestPathToEndByStartingPoint.getValue(forwardPathCurrentPoint)
    }

//    if (Random.nextInt() % 100 == 0) {
//      println("findShortestPath: $forwardPath")
//    }

    val newPathToEnd = forwardPathCurrentPoint.surroundingPoints(maxPoint)
//      .filterNot { newPoint -> forwardPath.hasPoint(newPoint) }
      .sortedBy { newPoint -> -newPoint.distanceFromOriginSquared }
      .map { newPoint -> Path(newPoint, this[newPoint], forwardPath) }
      .filter { newForwardPath -> newForwardPath.cost < bestCost }
      .map { newForwardPath -> findShortestPathToEnd(newForwardPath) }
      .filterNotNull()
      .minByOrNull { newPathToEnd -> newPathToEnd.cost } ?: return null

    val newPathToEnd2 = newPathToEnd.copy(parentPath = forwardPath.tail())

    if (newPathToEnd2.currentPoint in bestPathToEndByStartingPoint) {
      println("Already had path to end for point: ${newPathToEnd2.currentPoint}")
      return bestPathToEndByStartingPoint.getValue(newPathToEnd2.currentPoint)
    } else {
      println("Best path to end: $newPathToEnd2")
      bestPathToEndByStartingPoint[newPathToEnd2.currentPoint] = newPathToEnd2
    }

    return newPathToEnd2
  }

  return findShortestPathToEnd(Path(startingPoint, 0)) ?: throw IllegalStateException("No path found!")
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
    .map { newPoint -> toPath(currentPath, newPoint) }

private fun Grid<Int>.toPath(currentPath: Path, newPoint: Point) =
  Path(newPoint, this[newPoint], currentPath)

private fun Grid<Int>.findPathToEnd2(): Path {
  val startingPoint = 0 by 0
  val end = maxPoint

  val endPath = Path(end, this[end])
  val bestPathToEndByStartingPoint = mutableMapOf(end to endPath)

  fun findBestPathToEnd(currentPoint: Point): Path? {
    if (currentPoint in bestPathToEndByStartingPoint) {
      return bestPathToEndByStartingPoint.getValue(currentPoint)
    }

    var bestPath: Path? = null
    currentPoint.surroundingPoints(this.maxPoint).sortedBy { -it.stepsFromOrigin }.forEach { p ->
      val pPath = findBestPathToEnd(p)
      if (pPath != null) {
        val newPath = pPath.copy(parentPath = Path(currentPoint, this[p]))
        if (bestPath == null || newPath.cost < bestPath!!.cost) {
          bestPath = newPath
        }
      }
    }

    if (bestPath != null) {
      bestPathToEndByStartingPoint[currentPoint] = bestPath!!
    }

    return bestPath
  }

//  val nextPoints = sortedMapOf(Comparator { p1, p2 ->
//    when {
//      p1.stepsFromOrigin < p2.stepsFromOrigin -> 1
//      p1.stepsFromOrigin > p2.stepsFromOrigin -> -1
//      p1 < p2 -> 1
//      p1 > p2 -> -1
//      else -> 0
//    }
//  }, end to Path(end, this[end]))

  TODO("Not implemented yet")
}

//private fun Grid<Int>.chunked(into: Int): Grid<Grid<Int>> =
//  values.chunked(into)
//    .map { rowChunk ->
//      rowChunk.indices.map { index -> rowChunk.map { it[index] }.chunked(into)
//    }.toGrid()

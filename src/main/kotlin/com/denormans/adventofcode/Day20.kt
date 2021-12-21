package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Grid
import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.toGrid

fun main() {
  val values = loadStrings(20, forTest = false)

  val algorithm = values.first()
  val grid = values.subList(2, values.size).map { it.toCharArray().toList() }.toGrid()

//  grid.displayGrid("")

  problemOne(algorithm, grid)
  problemTwo(algorithm, grid)
}

private fun problemOne(algorithm: String, grid: Grid<Char>) {
  val gridPoints = grid.filterPoints { it == '#' }
//  println("gridPoints: $gridPoints")

//  gridPoints.toGrid().mapValues { if (it) { '#' } else { '.' } }.displayGrid("")

  val enhanced = gridPoints.enhance(algorithm, 20).enhance(algorithm, 1)
//  println("enhanced: $enhanced")

//  enhanced.toGrid().mapValues { if (it) { '#' } else { '.' } }.displayGrid("")

  println("Problem 1: ${enhanced.size}")
}

private fun problemTwo(algorithm: String, grid: Grid<Char>) {
  val gridPoints = grid.filterPoints { it == '#' }

//  gridPoints.toGrid().mapValues { if (it) { '#' } else { '.' } }.displayGrid("")

  var enhanced = gridPoints.enhance(algorithm, 100)
  repeat(49) {
    enhanced = enhanced.enhance(algorithm, 1)
  }

  enhanced.toGrid().mapValues { if (it) { '#' } else { '.' } }.displayGrid("")

  println("Problem 2: ${enhanced.size}")
}

private fun Set<Point>.enhance(algorithm: String, increaseBy: Int = 1): Set<Point> {
  val minX = minOf { it.x } - increaseBy
  val maxX = maxOf { it.x } + increaseBy
  val minY = minOf { it.y } - increaseBy
  val maxY = maxOf { it.y } + increaseBy

  return (minX..maxX).flatMap { x ->
    (minY..maxY).map { y ->
      (x by y) to algorithm[(x by y).getAlgorithmIndex(this)]
    }
  }.toMap().filterValues { it == '#' }.keys
}

private fun Point.getAlgorithmIndex(points: Set<Point>) = getAlgorithmIndexPoints()
  .mapIndexed { index, point -> if (point in points) { 1 shl (9 - index - 1) } else { 0 } }.sum()

private fun Point.getAlgorithmIndexPoints() =
  listOf(x-1 by y-1, x by y-1, x+1 by y-1, x-1 by y, x by y, x+1 by y, x-1 by y+1, x by y+1, x+1 by y+1)

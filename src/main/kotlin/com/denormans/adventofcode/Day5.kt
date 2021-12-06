package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount

fun main() {
  val values = loadStrings(5, forTest = false)

  val lines = values.map {
    val (p1, p2) = it.split(" -> ")
    VentLine(Point.parse(p1), Point.parse(p2))
  }

  problemOne(lines)
  problemTwo(lines)
}

data class VentLine(val p1: Point, val p2: Point) {
  val pointsHV by lazy { p1.interpolate(p2, includeDiagonal = false) }
  val points by lazy { p1.interpolate(p2) }
}

private fun problemOne(lines: List<VentLine>) {
  lines.map { it.pointsHV }.println()

  val allPoints = lines.map { it.pointsHV }.flatten()
  displayGrid(allPoints)

  val allPointsWithCount = allPoints.withCount()
  val badPoints = allPointsWithCount.filterValues { it > 1 }

  println("Problem 1: $badPoints (${badPoints.size})")
}

private fun problemTwo(lines: List<VentLine>) {
  lines.map { it.points }.println()

  val allPoints = lines.map { it.points }.flatten()
  displayGrid(allPoints)

  val allPointsWithCount = allPoints.withCount()
  val badPoints = allPointsWithCount.filterValues { it > 1 }

  println("Problem 2: $badPoints (${badPoints.size})")
}

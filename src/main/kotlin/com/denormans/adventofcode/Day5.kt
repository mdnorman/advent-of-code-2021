package com.denormans.adventofcode

import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount

fun main() {
  val values = loadStrings(5, forTest = true)

  val lines = values.map {
    val (x1, y1, x2, y2) = it.split(" -> ").joinToString(",").split(",").map { it.toInt() }
    VentLine(x1, x2, y1, y2)
  }

  problemOne(lines)
  problemTwo(lines)
}

data class VentLine(val x1: Int, val x2: Int, val y1: Int, val y2: Int) {
  val pointsHV by lazy {
    if (x1 == x2) {
      if (y1 < y2) {
        (y1..y2).map { x1 by it }
      } else {
        (y2..y1).map { x1 by it }
      }
    } else if (y1 == y2) {
      if (x1 < x2) {
        (x1..x2).map { it by y1 }
      } else {
        (x2..x1).map { it by y1 }
      }
    } else {
      emptyList()
    }
  }

  val points by lazy {
    if (x1 == x2) {
      if (y1 < y2) {
        (y1..y2).map { x1 by it }
      } else {
        (y2..y1).map { x1 by it }
      }
    } else if (y1 == y2) {
      if (x1 < x2) {
        (x1..x2).map { it by y1 }
      } else {
        (x2..x1).map { it by y1 }
      }
    } else {
      if (x1 < x2) {
        val num = x2-x1
        (0..num).map {
          if (y1 < y2) {
            (x1+it) by (y1+it)
          } else {
            (x1+it) by (y1-it)
          }
        }
      } else {
        val num = x1-x2
        (0..num).map {
          if (y1 < y2) {
            (x1-it) by (y1+it)
          } else {
            (x1-it) by (y1-it)
          }
        }
      }
    }
  }
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

package com.denormans.adventofcode

fun main() {
  val values = loadStrings(5, forTest = false)

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
        (y1..y2).map { x1 to it }
      } else {
        (y2..y1).map { x1 to it }
      }
    } else if (y1 == y2) {
      if (x1 < x2) {
        (x1..x2).map { it to y1 }
      } else {
        (x2..x1).map { it to y1 }
      }
    } else {
      emptyList()
    }
  }

  val points by lazy {
    if (x1 == x2) {
      if (y1 < y2) {
        (y1..y2).map { x1 to it }
      } else {
        (y2..y1).map { x1 to it }
      }
    } else if (y1 == y2) {
      if (x1 < x2) {
        (x1..x2).map { it to y1 }
      } else {
        (x2..x1).map { it to y1 }
      }
    } else {
      if (x1 < x2) {
        val num = x2-x1
        (0..num).map {
          if (y1 < y2) {
            (x1+it) to (y1+it)
          } else {
            (x1+it) to (y1-it)
          }
        }
      } else {
        val num = x1-x2
        (0..num).map {
          if (y1 < y2) {
            (x1-it) to (y1+it)
          } else {
            (x1-it) to (y1-it)
          }
        }
      }
    }
  }
}

private fun problemOne(lines: List<VentLine>) {
  lines.forEach {
    println(it.pointsHV)
  }

  val allPoints = lines.map { it.pointsHV }.flatten()
  val allPointsSet = mutableSetOf<Pair<Int, Int>>()
  val counted = mutableSetOf<Pair<Int, Int>>()
  allPoints.forEach { point ->
    if (!allPointsSet.add(point)) {
      counted.add(point)
      println("added $point")
    }
  }

  println("Problem 1: ${counted.size}")
}

private fun problemTwo(lines: List<VentLine>) {
  lines.forEach {
    println(it.points)
  }

  val allPoints = lines.map { it.points }.flatten()
  val allPointsSet = mutableSetOf<Pair<Int, Int>>()
  val counted = mutableSetOf<Pair<Int, Int>>()
  allPoints.forEach { point ->
    if (!allPointsSet.add(point)) {
      counted.add(point)
      println("added $point")
    }
  }

  println("Problem 2: ${counted.size}")
}

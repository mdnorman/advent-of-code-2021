package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(9, forTest = false)

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(values: List<String>) {
  val lowPoints = findLowPoints(values)

  val result = lowPoints.map { values[it.x][it.y].toString().toInt() + 1 }.sum()

  println("Problem 1: $result")
}

private fun findLowPoints(values: List<String>): List<Point> {
  val lowPoints = mutableListOf<Point>()
  for (i in values.indices) {
    for (j in values[0].indices) {
      val value = values[i][j]
      var result = true
      if (i > 0) {
        result = result && value < values[i - 1][j]
      }
      if (j > 0) {
        result = result && value < values[i][j - 1]
      }
      if (i < values.lastIndex) {
        result = result && value < values[i + 1][j]
      }
      if (j < values[i].lastIndex) {
        result = result && value < values[i][j + 1]
      }

      if (result) {
        lowPoints.add(i by j)
      }
    }
  }
  return lowPoints
}

private fun problemTwo(values: List<String>) {
  val lowPoints = findLowPoints(values)

  val basinSizes = lowPoints.map { basinSize(values, it) }.sorted().reversed()

  val (a, b, c) = basinSizes

  println("Problem 2: ${a*b*c}")
}

private fun basinSize(values: List<String>, p: Point, foundPoints: MutableSet<Point> = mutableSetOf()): Int {
  if (!foundPoints.add(p)) {
    return 0
  }

  if (values[p.x][p.y] == '9') {
    return 0
  }

  var size = 1
  if (p.x > 0) {
    size += basinSize(values, Point(p.x-1, p.y), foundPoints)
  }
  if (p.y > 0) {
    size += basinSize(values, Point(p.x, p.y-1), foundPoints)
  }
  if (p.x < values.lastIndex) {
    size += basinSize(values, Point(p.x+1, p.y), foundPoints)
  }
  if (p.y < values[0].lastIndex) {
    size += basinSize(values, Point(p.x, p.y+1), foundPoints)
  }

  return size
}

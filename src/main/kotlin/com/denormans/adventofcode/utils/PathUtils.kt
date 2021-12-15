package com.denormans.adventofcode.utils

data class Grid<T>(val values: List<List<T>>) {
  val maxPoint by lazy { values.maxPoint() }

  operator fun get(point: Point) = values.getAt(point)
}

fun <T> List<List<T>>.toGrid() = Grid<T>(this)

fun <T> List<List<T>>.maxPoint() =
  lastIndex by if (this.isNotEmpty()) { this[0].lastIndex } else { 0 }

fun Point.surroundingPoints(max: Point, min: Point = 0 by 0, includeDiagonal: Boolean = false): List<Point> {
  val points = mutableListOf<Point>()

  if (x > min.x) {
    if (includeDiagonal && y > min.y) {
      points.add(x - 1 by y - 1)
    }
    points.add(x - 1 by y)
    if (includeDiagonal && y < max.y) {
      points.add(x - 1 by y + 1)
    }
  }

  if (y > min.y) {
    points.add(x by y - 1)
  }
  if (y < max.y) {
    points.add(x by y + 1)
  }

  if (x < max.x) {
    if (includeDiagonal && y > min.y) {
      points.add(x + 1 by y - 1)
    }
    points.add(x + 1 by y)
    if (includeDiagonal && y < max.y) {
      points.add(x + 1 by y + 1)
    }
  }

  return points
}

fun <T> List<List<T>>.getAt(point: Point) = this[point.x][point.y]

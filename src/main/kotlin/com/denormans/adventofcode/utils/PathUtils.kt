package com.denormans.adventofcode.utils

data class Grid<T>(val values: List<List<T>>) {
  val maxPoint by lazy { values.maxPoint() }

  operator fun get(point: Point) = values[point.y][point.x]

  val pointMap by lazy { mapValuesIndexed { x, y, value -> x by y to value }.values.flatten().toMap() }

  fun filterPoints(predicate: (T) -> Boolean) = pointMap.filterValues { value -> predicate(value) }.keys

  fun <R> mapValues(transform: (value: T) -> R): Grid<R> = values
    .map { row ->
      row.map { value -> transform(value) }
    }.toGrid()

  fun <R> mapValuesIndexed(transform: (x: Int, y: Int, value: T) -> R): Grid<R> = values
    .mapIndexed { y, row ->
      row.mapIndexed { x, value -> transform(x, y, value) }
    }.toGrid()
}

fun <T> List<List<T>>.toGrid() = Grid(this)

fun Set<Point>.toGrid(): Grid<Boolean> {
  val minX = minOf { it.x }
  val maxX = maxOf { it.x }
  val minY = minOf { it.y }
  val maxY = maxOf { it.y }

  return (minY..maxY).map { row ->
    (minX..maxX).map { column ->
      column by row in this
    }
  }.toGrid()
}

fun <T> List<List<T>>.maxPoint() =
  lastIndex by if (this.isNotEmpty()) {
    this[0].lastIndex
  } else {
    0
  }

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


package com.denormans.adventofcode.utils

fun <T> Collection<T>.println() = forEach { println(it) }

fun <T> Grid<T>.displayGrid(separator: String = " ") = displayGrid(values, separator)

fun <T> displayGrid(values: List<List<T>>, separator: String = " ") {
  values.forEach { row ->
    row.forEach { value ->
      print("$value$separator")
    }
    println()
  }
}

fun displayGrid(points: Collection<Point>, toPoint: Point = Point(points.maxOf { it.x }, points.maxOf { it.y })) {
  val pointsWithCount = points.withCount()

  (0..toPoint.y).forEach { y ->
    (0..toPoint.x).forEach { x ->
      print(pointsWithCount[Point(x, y)] ?: ".")
    }
    println()
  }
}

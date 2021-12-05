package com.denormans.adventofcode.utils

fun <T> Collection<T>.println() = forEach { println(it) }

fun <T> displayGrid(values: List<List<T>>) {
  values.forEach { row ->
    row.forEach {
      print("$it ")
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

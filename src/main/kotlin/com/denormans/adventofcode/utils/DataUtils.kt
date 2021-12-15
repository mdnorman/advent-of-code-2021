package com.denormans.adventofcode.utils

import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) : Comparable<Point> {
  val distanceFromOrigin by lazy { sqrt(distanceFromOriginSquared.toDouble()) }
  val distanceFromOriginSquared by lazy { x * x + y * y }

  fun interpolate(other: Point, includeDiagonal: Boolean = true): List<Point> {
    val (x1, y1) = this
    val (x2, y2) = other

    return if (x1 == x2) {
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
    } else if (includeDiagonal) {
      if (x1 < x2) {
        val num = x2 - x1
        (0..num).map {
          if (y1 < y2) {
            (x1 + it) by (y1 + it)
          } else {
            (x1 + it) by (y1 - it)
          }
        }
      } else {
        val num = x1 - x2
        (0..num).map {
          if (y1 < y2) {
            (x1 - it) by (y1 + it)
          } else {
            (x1 - it) by (y1 - it)
          }
        }
      }
    } else {
      emptyList()
    }
  }

  override fun toString() = "($x,$y)"

  override fun compareTo(other: Point): Int {
    if (x == other.x) {
      return y - other.y
    }

    return x - other.x
  }

  companion object {
    fun parse(text: String, separator: Char = ',') = text.split(separator).toPoint()

    private fun List<String>.toPoint() = Point(this[0].toInt(), this[1].toInt())
  }
}

class PointFromOriginComparator : Comparator<Point> {
  override fun compare(o1: Point, o2: Point): Int {
    return o1.distanceFromOriginSquared - o2.distanceFromOriginSquared
  }
}

infix fun Int.by(y: Int) = Point(this, y)

val sevenSegmentNumbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

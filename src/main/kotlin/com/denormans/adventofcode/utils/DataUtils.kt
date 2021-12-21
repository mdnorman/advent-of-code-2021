package com.denormans.adventofcode.utils

import java.lang.Integer.max
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun Pair<Int, Int>.toRange() = first.rangeTo(second)

data class Point(val x: Int, val y: Int) : Comparable<Point> {
  val distanceFromOrigin by lazy { sqrt(distanceFromOriginSquared.toDouble()) }
  val distanceFromOriginSquared by lazy { x * x + y * y }
  val stepsFromOrigin by lazy { x + y }
  val diagonalStepsFromOrigin by lazy { max(x, y) }
  val manhattanDistanceFromOrigin by lazy { manhattanDistanceFrom(Point(0, 0)) }

  fun manhattanDistanceFrom(other: Point) = (x - other.x).absoluteValue + (y - other.y).absoluteValue

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

  override fun compareTo(other: Point) = when {
      x != other.x -> x - other.x
      else -> y - other.y
  }

  operator fun plus(point: Point) = Point(x + point.x, y + point.y)

  operator fun minus(point: Point) = Point(x - point.x, y - point.y)

  operator fun unaryMinus() = Point(-x, -y)

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

data class TriplePoint(val x: Int, val y: Int, val z: Int) : Comparable<TriplePoint> {
  val distanceFromOrigin by lazy { sqrt(distanceFromOriginSquared.toDouble()) }
  val distanceFromOriginSquared by lazy { x * x + y * y + z * z }

  val manhattanDistanceFromOrigin by lazy { manhattanDistanceFrom(TriplePoint(0, 0, 0)) }

  fun manhattanDistanceFrom(other: TriplePoint) = (x - other.x).absoluteValue + (y - other.y).absoluteValue + (z - other.z).absoluteValue

  fun rotateAroundX() = TriplePoint(x, -z, y)
  fun rotateAroundY() = TriplePoint(-z, y, x)
  fun rotateAroundZ() = TriplePoint(-y, x, z)

  fun withOrientation(orientation: Orientation) = orientation.orient(this)

  override fun toString() = "($x,$y,$z)"

  override fun compareTo(other: TriplePoint) = when {
    x != other.x -> x - other.x
    y != other.y -> y - other.y
    else -> z - other.z
  }

  operator fun plus(point: TriplePoint) = TriplePoint(x + point.x, y + point.y, z + point.z)

  operator fun minus(point: TriplePoint) = TriplePoint(x - point.x, y - point.y, z - point.z)

  operator fun unaryMinus() = TriplePoint(-x, -y, -z)
}

val sevenSegmentNumbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

data class Orientation(private val x: Int, private val y: Int, private val z: Int) {
  fun orient(point: TriplePoint): TriplePoint = TriplePoint(point.getPart(x), point.getPart(y), point.getPart(z))

  companion object {
    val all by lazy {
      val orientations = mutableSetOf<Orientation>()
      var rotatedAroundX = TriplePoint(1, 2, 3)
      for (i in 1..4) {
        rotatedAroundX = rotatedAroundX.rotateAroundX()
        orientations.add(rotatedAroundX.toOrientation())

        var rotatedAroundY = rotatedAroundX.rotateAroundY()
        for (j in 1..4) {
          rotatedAroundY = rotatedAroundY.rotateAroundY()
          orientations.add(rotatedAroundY.toOrientation())

          var rotatedAroundZ = rotatedAroundY.rotateAroundZ()
          for (k in 1..4) {
            rotatedAroundZ = rotatedAroundZ.rotateAroundY()
            orientations.add(rotatedAroundZ.toOrientation())
          }
        }
      }
      orientations
    }

    private fun TriplePoint.toOrientation() = Orientation(x, y, z)

    private fun TriplePoint.getPart(orientationPart: Int): Int = when (orientationPart) {
      1 -> x
      -1 -> -x
      2 -> y
      -2 -> -y
      3 -> z
      -3 -> -z
      else -> error("Invalid orientation part: $orientationPart")
    }
  }
}

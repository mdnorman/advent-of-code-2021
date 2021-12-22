package com.denormans.adventofcode.utils

import java.lang.Integer.max
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun Pair<Int, Int>.toRange() = first.rangeTo(second)

infix fun <A, B, C> A.to(pair: Pair<B, C>) = Triple(this, pair.first, pair.second)

infix fun <A, B, C> Pair<A, B>.to(third: C) = Triple(first, second, third)

infix fun Int.by(y: Int) = Point(this, y)

infix fun Int.by(p: Point) = TriplePoint(this, p.x, p.y)

infix fun Point.by(z: Int) = TriplePoint(x, y, z)

data class Point(val x: Int, val y: Int) : Comparable<Point> {
  val distanceFromOrigin by lazy { sqrt(distanceFromOriginSquared.toDouble()) }
  val distanceFromOriginSquared by lazy { x * x + y * y }
  val diagonalStepsFromOrigin by lazy { max(x, y) }
  val manhattanDistanceFromOrigin by lazy { manhattanDistanceFrom(0 by 0) }

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

  operator fun plus(point: Point) = x + point.x by y + point.y

  operator fun minus(point: Point) = x - point.x by y - point.y

  operator fun unaryMinus() = -x by -y

  companion object {
    fun parse(text: String, separator: Char = ',') = text.split(separator).toPoint()

    private fun List<String>.toPoint() = this[0].toInt() by this[1].toInt()
  }
}

class PointFromOriginComparator : Comparator<Point> {
  override fun compare(o1: Point, o2: Point): Int {
    return o1.distanceFromOriginSquared - o2.distanceFromOriginSquared
  }
}

data class TriplePoint(val x: Int, val y: Int, val z: Int) : Comparable<TriplePoint> {
  val distanceFromOrigin by lazy { sqrt(distanceFromOriginSquared.toDouble()) }
  val distanceFromOriginSquared by lazy { x * x + y * y + z * z }

  val manhattanDistanceFromOrigin by lazy { manhattanDistanceFrom(0 by 0 by 0) }

  fun manhattanDistanceFrom(other: TriplePoint) = (x - other.x).absoluteValue + (y - other.y).absoluteValue + (z - other.z).absoluteValue

  fun rotateAroundX() = x by -z by y
  fun rotateAroundY() = -z by y by x
  fun rotateAroundZ() = -y by x by z

  fun withOrientation(orientation: Orientation) = orientation.orient(this)

  override fun toString() = "($x,$y,$z)"

  override fun compareTo(other: TriplePoint) = when {
    x != other.x -> x - other.x
    y != other.y -> y - other.y
    else -> z - other.z
  }

  operator fun plus(point: TriplePoint) = x + point.x by y + point.y by z + point.z

  operator fun minus(point: TriplePoint) = x - point.x by y - point.y by z - point.z

  operator fun unaryMinus() = -x by -y by -z

  companion object {
    fun parse(text: String, separator: Char = ',') = text.split(separator).toPoint()

    private fun List<String>.toPoint() = this[0].toInt() by this[1].toInt() by this[2].toInt()
  }
}

val sevenSegmentNumbers = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

data class Orientation(private val x: Int, private val y: Int, private val z: Int) {
  fun orient(point: TriplePoint): TriplePoint = TriplePoint(point.getPart(x), point.getPart(y), point.getPart(z))

  override fun toString() = "(${partToString(x)},${partToString(y)},${partToString(z)})"

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

    private fun partToString(orientationPart: Int) = when (orientationPart) {
      1 -> "+x"
      -1 -> "-x"
      2 -> "+y"
      -2 -> "-y"
      3 -> "+z"
      -3 -> "-z"
      else -> error("Invalid orientation part: $orientationPart")
    }
  }
}

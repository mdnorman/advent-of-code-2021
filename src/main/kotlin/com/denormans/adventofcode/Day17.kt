package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.loadString
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.toPair
import com.denormans.adventofcode.utils.toRange

fun main() {
  val (xRange, yRange) = loadString(17, forTest = false)
    .substringAfter("target area: ")
    .split(", ")
    .map { parts -> parts.split("=").last().split("..").map { it.toInt() }.sorted().toPair().toRange() }
  val target = Target(xRange, yRange)
  println(target)

  problemOne(target)
  problemTwo(target)
}

private fun problemOne(target: Target) {
  val testsThatHitTarget = testsThatHitTarget(target)

//  println("testsHitsTarget")
  testsThatHitTarget.println()

  val testsWithHeights = testsThatHitTarget.map { it to it.maxHeight() }
  testsWithHeights.println()

  val maxHeightTest = testsWithHeights.maxByOrNull { it.second } ?: throw IllegalStateException("No valid tests")
  println("maxHeightTest: $maxHeightTest")

  println("Problem 1: ${maxHeightTest.second}")
}

private fun xThatHitTarget(target: Target): List<Int> {
  val xTarget = Target(target.x, Int.MIN_VALUE..Int.MAX_VALUE)
  return (1..target.maxX)
    .map { veloX -> TestValue(0 by 0, veloX by 0) }
    .filter { testValue -> testValue.hitsTarget(xTarget) }
    .map { it.velocity.x }
}

private fun testsThatHitTarget(target: Target) = xThatHitTarget(target).flatMap { veloX ->
//  println("testing veloX: $veloX")
  (-1000..1000)
    .map { veloY -> veloY } /*.also { println("testing veloY: $veloY")} }*/
    .map { veloY -> TestValue(0 by 0, veloX by veloY) }
    .filter { testValue -> testValue.hitsTarget(target) }
}

private fun problemTwo(target: Target) {
  val testsThatHitTarget = testsThatHitTarget(target)

  println("testsHitsTarget")
  testsThatHitTarget.println()

  println("Problem 2: ${testsThatHitTarget.size}")
}

private data class TestValue(val point: Point, val velocity: Point) {
  fun step() = TestValue(point + velocity, Point((velocity.x - 1).coerceAtLeast(0), velocity.y - 1))

  fun maxHeight(): Int {
    val nextPoint = step()
    return if (nextPoint.point.y > point.y) {
      nextPoint.maxHeight()
    } else {
      point.y
    }
  }

  fun hitsTarget(target: Target): Boolean = when {
    point in target -> true
    velocity.x == 0 && point.x !in target.x -> false
    velocity.x >= 0 && point.x > target.maxX -> false
    velocity.y <= 0 && point.y < target.minY -> false
    else -> {
//      println("$this did not hit $target")
      step().hitsTarget(target)
    }
  }
}

private data class Target(val x: IntRange, val y: IntRange) {
  val minX
    get() = x.first

  val maxX
    get() = x.last

  val minY
    get() = y.first

  val maxY
    get() = y.last

  operator fun contains(point: Point) = point.x in x && point.y in y
}

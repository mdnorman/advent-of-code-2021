package com.denormans.adventofcode

import com.denormans.adventofcode.utils.TriplePoint
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println

fun main() {
  val values = loadStrings(22, forTest = true).map {
    val (on, ranges) = it.split(" ")
    val (xRange, yRange, zRange) = ranges.split(",").map {
      val (_, low, high) = it.split("=", "..")
      IntRange(low.toInt(), high.toInt())
    }
    RebootStep(on == "on", xRange, yRange, zRange)
  }
//  values.println()

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(steps: List<RebootStep>) {
  val pointsOn = mutableSetOf<TriplePoint>()

  val bounds = (-50..50).toSet()

  steps.forEach { step ->
    println("Processing $step")
    bounds.intersect(step.xRange).forEach { x ->
      bounds.intersect(step.yRange).forEach { y ->
        bounds.intersect(step.zRange).forEach { z ->
          if (step.on) {
            pointsOn += x by y by z
          } else {
            pointsOn -= x by y by z
          }
        }
      }
    }
  }

//  pointsOn.println()

  println("Problem 1: ${pointsOn.size}")
}

private fun problemTwo(steps: List<RebootStep>) {

  println("Problem 2:")
}

private data class RebootStep(val on: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)

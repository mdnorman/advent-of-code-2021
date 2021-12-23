package com.denormans.adventofcode

import com.denormans.adventofcode.utils.TriplePoint
import com.denormans.adventofcode.utils.TriplePointRange
import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(22, forTest = false).map {
    val (on, ranges) = it.split(" ")
    val (xRange, yRange, zRange) = ranges.split(",").map {
      val (_, low, high) = it.split("=", "..")
      IntRange(low.toInt(), high.toInt())
    }
    RebootStep(on == "on", TriplePointRange(xRange, yRange, zRange))
  }
//  values.println()

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(steps: List<RebootStep>) {
  val edgeBounds = (-50..50)
  val bounds = TriplePointRange(edgeBounds, edgeBounds, edgeBounds)

  val reactor = steps.fold(ReactorState(emptyList())) { reactor, step ->
    reactor.step(step.copy(range = step.range intersect bounds))
  }

  // correct:
  // 590784
  // 590784

  println("Problem 1: ${reactor.numOn}")
}

private fun problemTwo(steps: List<RebootStep>) {
  val reactor = steps.fold(ReactorState(emptyList())) { reactor, step -> reactor.step(step) }

  // correct:
  // 2758514936282235
  // 2758514936282235

  println("Problem 2: ${reactor.numOn}")
}

private data class RebootStep(val on: Boolean, val range: TriplePointRange)

private data class ReactorState(val ranges: List<TriplePointRange>) {
  val numOn by lazy { ranges.sumOf { it.size } }

  fun step(step: RebootStep): ReactorState {
    println("$step with ${ranges.size} ($numOn)")
    return when {
      step.range.isEmpty() -> this
      step.on -> {
        this + step.range
      }
      else -> {
        this - step.range
      }
    }
  }

  operator fun plus(range: TriplePointRange): ReactorState {
    val newRanges = ranges.toMutableSet()

    val rangesToProcess = mutableSetOf(range)
    while (rangesToProcess.isNotEmpty()) {
      val processingRange = rangesToProcess.first()
      rangesToProcess -= processingRange

      val overlapping = newRanges.find { it overlaps processingRange }
//      println("+ processing $processingRange (of ${rangesToProcess.size}) overlapping with $overlapping")
      if (overlapping == null) {
        newRanges += processingRange
        continue
      }

      newRanges -= overlapping
      rangesToProcess += overlapping + processingRange
    }

    return ReactorState(newRanges.toList())
  }

  operator fun minus(range: TriplePointRange): ReactorState {
    val newRanges = ranges.toMutableSet()

    val rangesToProcess = mutableSetOf(range)
    while (rangesToProcess.isNotEmpty()) {
      val processingRange = rangesToProcess.first()

      val overlapping = newRanges.find { it overlaps processingRange }
//      println("- processing $processingRange (of ${rangesToProcess.size}) overlapping with $overlapping")
      if (overlapping == null) {
        rangesToProcess -= processingRange
        continue
      }

      newRanges -= overlapping
      newRanges += overlapping - processingRange
    }

    return ReactorState(newRanges.toList())
  }

  operator fun contains(point: TriplePoint) = ranges.any { point in it }
}

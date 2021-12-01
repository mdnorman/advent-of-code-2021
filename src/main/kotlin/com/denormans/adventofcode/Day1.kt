package com.denormans.adventofcode

import java.io.File

fun main() {
  val numbers = File("input/day1.txt").readLines().map { it.toInt() }
  problemOne(numbers)
  problemTwo(numbers)
}

private fun problemOne(numbers: List<Int>) {
  val count = countIfMore(numbers)

  println("Problem 1: $count")
}

private fun problemTwo(numbers: List<Int>) {
  val count = countIfMore(numbers.windowed(3).map { it.sum() })

  println("Problem 2: $count")
}

private fun countIfMore(numbers: List<Int>): Int {
  var previous = Int.MAX_VALUE
  var count = 0
  numbers.forEach {
    if (it > previous) {
      count += 1
    }
    previous = it
  }
  return count
}

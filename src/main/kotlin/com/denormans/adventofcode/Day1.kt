package com.denormans.adventofcode

fun main() {
  val numbers = loadNumbers(1, forTest = false)

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

private fun countIfMore(numbers: List<Int>) = numbers.fold(0 to Int.MAX_VALUE) { (count, previous), current ->
  if (current > previous) {
    count + 1 to current
  } else {
    count to current
  }
}.first

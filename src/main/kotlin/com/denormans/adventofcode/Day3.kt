package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(3, forTest = false)

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(values: List<String>) {
  val numBits = values.first().length
  val gamma = (0 until numBits).sumOf { mostBits(values, it) shl (numBits - it - 1) }
  val epsilon = (0 until numBits).sumOf { leastBits(values, it) shl (numBits - it - 1) }

  println("Problem 1: epsilon=$epsilon, gamme=$gamma: ${gamma * epsilon}")
}

private fun mostBits(values: List<String>, bitIndex: Int): Int {
  val (ones, zeroes) = countBits(values, bitIndex)
  return if (ones >= zeroes) {
    1
  } else {
    0
  }
}

private fun leastBits(values: List<String>, bitIndex: Int): Int {
  val (ones, zeroes) = countBits(values, bitIndex)
  return if (ones >= zeroes) {
    0
  } else {
    1
  }
}

private fun countBits(values: List<String>, bitIndex: Int): Pair<Int, Int> =
  values.fold(0 to 0) { (ones, zeroes), current ->
    if (current[bitIndex] == '1') {
      (ones + 1) to zeroes
    } else {
      ones to (zeroes + 1)
    }
  }

private fun problemTwo(values: List<String>) {
  var oxygenGeneratorRatingValues = values
  var index = 0
  while (oxygenGeneratorRatingValues.size > 1) {
    val most = mostBits(oxygenGeneratorRatingValues, index)
    oxygenGeneratorRatingValues = oxygenGeneratorRatingValues.filter {
      most.toString() == it[index].toString()
    }
    index += 1
  }

  val oxygenGeneratorRating = oxygenGeneratorRatingValues.first().fromBinary()

  var co2ScrubberRatingValues = values
  index = 0
  while (co2ScrubberRatingValues.size > 1) {
    val least = leastBits(co2ScrubberRatingValues, index)
    co2ScrubberRatingValues = co2ScrubberRatingValues.filter { least.toString() == it[index].toString() }
    index += 1
  }

  val co2ScrubberRating = co2ScrubberRatingValues.first().fromBinary()

  println("Problem 2: oxygenGeneratorRating=$oxygenGeneratorRating, co2ScrubberRating=$co2ScrubberRating: ${oxygenGeneratorRating * co2ScrubberRating}")
}

fun String.fromBinary() = toCharArray().mapIndexed { index, ch -> (ch - '0') shl (length - index - 1) }.sum()

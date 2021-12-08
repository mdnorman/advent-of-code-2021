package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(8, forTest = false).map {
    val (input, output) = it.split(" | ").map { it.split(" ") }
    Data(input.sortedBy { it.length }, output)
  }

  problemOne(values)
  problemTwo(values)
}

data class Data(val input: List<String>, val output: List<String>) {
  val correctOutput by lazy {
    val knownNumbers = populateCorrections()

    output.map { knownNumbers.getValue(it.toSet()).toString() }.joinToString("").toInt()
  }

  private fun populateCorrections(): Map<Set<Char>, Int> {
    val foundNumbers = mutableMapOf<Int, String>()

    foundNumbers[1] = input.find { it.length == 2 } ?: throw Exception("No 1 found: $input")
    foundNumbers[7] = input.find { it.length == 3 } ?: throw Exception("No 7 found: $input")
    foundNumbers[4] = input.find { it.length == 4 } ?: throw Exception("No 4 found: $input")
    foundNumbers[8] = input.find { it.length == 7 } ?: throw Exception("No 8 found: $input")

    foundNumbers[3] = input.find {
      it !in foundNumbers.values && it.length == 5 && (it.toSet() - foundNumbers.getValue(7).toSet()).size == 2
    } ?: throw Exception("No 3 found: $input")

    foundNumbers[9] = input.find {
      it !in foundNumbers.values && it.length == 6 && (it.toSet() - foundNumbers.getValue(3).toSet()).size == 1
    } ?: throw Exception("No 9 found: $input")

    foundNumbers[5] = input.find {
      it !in foundNumbers.values && it.length == 5 && (it.toSet() - foundNumbers.getValue(4).toSet()).size == 2
    } ?: throw Exception("No 5 found: $input")

    foundNumbers[0] = input.find {
      it !in foundNumbers.values && it.length == 6 && (it.toSet() - foundNumbers.getValue(7).toSet()).size == 3
    } ?: throw Exception("No 0 found: $input")

    foundNumbers[6] = input.find {
      it !in foundNumbers.values && it.length == 6
    } ?: throw Exception("No 6 found: $input")

    foundNumbers[2] = input.find { it !in foundNumbers.values } ?: throw Exception("No 2 found: $input")

    return foundNumbers.map { (number, segments) -> segments.toSet() to number }.toMap()
  }
}

private fun problemOne(data: List<Data>) {
  val lengths = setOf(2, 3, 4, 7)
  val result = data.map { it.output.count { it.length in lengths } }.sum()

  println("Problem 1: $result")
}

private fun problemTwo(data: List<Data>) {
  val results = data.map { it.correctOutput }

  println("Problem 2: ${results.sum()}")
}

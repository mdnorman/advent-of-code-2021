package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.withCount

fun main() {
  val values = loadStrings(14, forTest = false)
//  values.println()

  val template = values.first().toCharArray().toList()
  val pairInsertions = values.subList(2, values.size).map {
    val (pair, insertion) = it.split(" -> ")
    val (a, b) = pair.toCharArray()
    (a to b) to insertion[0]
  }.toMap()

  val polymer = Polymer(template.toPairs().withCount(), pairInsertions, template.last())

  polymer.println()

  problemOne(polymer)
  problemTwo(polymer)
}

/*
store as pairs
NNCB

NN NC CB

NCNNBCCHB

NC CN NN NB BC CC CH HB
 */

private data class Polymer(val templatePairs: Map<Pair<Char, Char>, Long>, val pairInsertions: Map<Pair<Char, Char>, Char>, val lastChar: Char) {
  fun nextStep(): Polymer {
    val newPairs = mutableMapOf<Pair<Char, Char>, Long>()

    templatePairs.forEach { (pair, count) ->
      val (pair1, pair2) = pair.newPairs()
      newPairs[pair1] = newPairs.getOrDefault(pair1, 0) + count
      newPairs[pair2] = newPairs.getOrDefault(pair2, 0) + count
    }
    return copy(templatePairs = newPairs)
  }

  fun Pair<Char, Char>.newPairs(): List<Pair<Char, Char>> {
    val insertion = pairInsertions.getValue(this)
    return listOf(first to insertion, insertion to second)
  }

  fun println() = println(templatePairs)
}

private fun problemOne(polymer: Polymer) {
  val lastPolymer = (1..10).fold(polymer) { result, _ -> result.nextStep() }

  lastPolymer.println()

  val counts = mutableMapOf<Char, Long>()
  lastPolymer.templatePairs.forEach { (key, value) ->
    val ch = key.first
    counts[ch] = counts.getOrDefault(ch, 0) + value
  }
  counts[polymer.lastChar] = counts.getOrDefault(polymer.lastChar, 0) + 1

  val mostCommon = counts.maxOf { (k, v) -> v }
  val leastCommon = counts.minOf { (k, v) -> v }

  println("Problem 1: $mostCommon - $leastCommon = ${mostCommon - leastCommon}")
}

private fun problemTwo(polymer: Polymer) {
  val lastPolymer = (1..40).fold(polymer) { result, _ -> result.nextStep() }

  lastPolymer.println()

  val counts = mutableMapOf<Char, Long>()
  lastPolymer.templatePairs.forEach { (key, value) ->
    val ch = key.first
    counts[ch] = counts.getOrDefault(ch, 0) + value
  }
  counts[polymer.lastChar] = counts.getOrDefault(polymer.lastChar, 0) + 1

  val mostCommon = counts.maxOf { (k, v) -> v }
  val leastCommon = counts.minOf { (k, v) -> v }

  println("Problem 2: $mostCommon - $leastCommon = ${mostCommon - leastCommon}")
}

private fun <T> List<T>.toPairs() = subList(0, lastIndex).mapIndexed { index, c -> c to this[index+1] }

package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount

fun main() {
  val values = loadStrings(14, forTest = true)
//  values.println()

  val template = values.first().toCharArray().toList()
  val pairInsertions = values.subList(2, values.size).map {
    val (pair, insertion) = it.split(" -> ")
    val (a, b) = pair.toCharArray()
    (a to b) to insertion[0]
  }.toMap()

  val polymer = Polymer(template, pairInsertions)

  polymer.println()

  problemOne(polymer)
  problemTwo(polymer)
}

private data class Polymer(val template: List<Char>, val pairInsertions: Map<Pair<Char, Char>, Char>) {
  val pairs by lazy { template.subList(0, template.lastIndex).mapIndexed { index, c -> c to template[index+1] } }

  fun nextStep() =
    Polymer(pairs.flatMap { listOf(it.first, pairInsertions.getValue(it)) } + pairs.last().second, pairInsertions)

  fun println() = println(String(template.toCharArray()))
}

private fun problemOne(polymer: Polymer) {
  val lastPolymer = (1..10).fold(polymer) { result, _ -> result.nextStep() }

  lastPolymer.println()

  val counts = lastPolymer.template.withCount()
  val mostCommon = counts.maxOf { (k, v) -> v }
  val leastCommon = counts.minOf { (k, v) -> v }

  println("Problem 1: $mostCommon - $leastCommon = ${mostCommon - leastCommon}")
}

private fun problemTwo(polymer: Polymer) {
  val lastPolymer = (1..40).fold(polymer) { result, _ -> result.nextStep() }

  lastPolymer.println()

  val counts = lastPolymer.template.withCount()
  val mostCommon = counts.maxOf { (k, v) -> v }
  val leastCommon = counts.minOf { (k, v) -> v }

  println("Problem 2: $mostCommon - $leastCommon = ${mostCommon - leastCommon}")
}

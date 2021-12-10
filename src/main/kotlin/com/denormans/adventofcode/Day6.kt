package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.withCount

fun main() {
  val values = loadStrings(6, forTest = false).first().split(",").map { it.toInt() }

  problemOne(values)
  problemTwo(values)
}

private data class Lanternfish(var daysLeft: Int) {
  val readyForProcreation: Boolean
    get() = daysLeft == 0

  fun nextDay() {
    if(daysLeft > 0) {
      daysLeft -= 1
    } else {
      daysLeft = 6
    }
  }
}

private fun problemOne(values: List<Int>) {
  val fish = values.map { Lanternfish(it) }.toMutableList()
  (0..79).forEach { day ->
    val modDay = day % 7
    println("day: $day ($modDay)")
    val numAdditionalFish = fish.count { it.readyForProcreation }
    println("adding $numAdditionalFish")
    fish.forEach { it.nextDay() }
    fish.addAll((1..numAdditionalFish).map { Lanternfish(8) })

//    println("total: ${fish.size}")
  }

  println("Problem 1: ${fish.size}")
}

private fun problemTwo(values: List<Int>) {
  val fishByDay = values.map { it }.withCount()
  val addByDay = mutableMapOf<Int, Long>()
  (0..255).forEach { day ->
    val modDay = day % 7
    val addDay = (day + 2) % 7
    println("day: $day ($modDay, $addDay)")

    val numAdditionalFish = fishByDay.getOrDefault(modDay, 0)
    println("adding $numAdditionalFish")

    fishByDay[modDay] = fishByDay.getOrDefault(modDay, 0) + addByDay.getOrDefault(modDay, 0)
    addByDay[modDay] = 0
    addByDay[addDay] = numAdditionalFish

//    println("total: ${fishByDay.values.sum() + addByDay.getOrDefault((day + 1) % 7, 0) + addByDay.getOrDefault((day + 2) % 7, 0)}")
  }

  println("Problem 2: ${fishByDay.values.sum() + addByDay.values.sum()}")
}

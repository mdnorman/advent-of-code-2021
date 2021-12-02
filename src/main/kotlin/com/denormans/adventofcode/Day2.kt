package com.denormans.adventofcode

fun main() {
  val commands = loadStrings(2).map {
    val (direction, amount) = it.split(" ")
    direction to amount.toInt()
  }

  problemOne(commands)
  problemTwo(commands)
}

private fun problemOne(commands: List<Pair<String, Int>>) {
  var position = 0
  var depth = 0

  for ((direction, amount) in commands) {
    when (direction) {
      "forward" -> position += amount
      "down" -> depth += amount
      "up" -> depth -= amount
    }
  }

  println("Problem 1: position=$position,depth=$depth: ${position*depth}")
}

private fun problemTwo(commands: List<Pair<String, Int>>) {
  var aim = 0
  var position = 0
  var depth = 0

  for ((direction, amount) in commands) {
    when (direction) {
      "forward" -> {
        position += amount
        depth += aim * amount
      }
      "down" -> aim += amount
      "up" -> aim -= amount
    }
  }

  println("Problem 2: aim=$aim,position=$position,depth=$depth: ${depth * position}")
}

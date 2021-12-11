package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(11, forTest = false)

  problemOne(extractOctopuses(values))
  problemTwo(extractOctopuses(values))
}

private fun extractOctopuses(values: List<String>) =
  values.map { it.map { DumboOctopus(it - '0') } }

private fun problemOne(dumboOctopuses: List<List<DumboOctopus>>) {
//  printOctopuses(dumboOctopuses)

  var totalFlashes = 0
  for (i in 1..100) {
    totalFlashes += processStep(dumboOctopuses)
//    println(totalFlashes)
//    printOctopuses(dumboOctopuses)
  }

  println("Problem 1: $totalFlashes")
}

private fun printOctopuses(dumboOctopuses: List<List<DumboOctopus>>) {
  dumboOctopuses.forEach {
    it.forEach { print(it.level) }
    println()
  }
}

private fun processStep(dumboOctopuses: List<List<DumboOctopus>>): Int {
  val toProcess = dumboOctopuses.flatMapIndexed { y, line -> line.mapIndexed { x, _ -> x by y } }.toMutableList()
  var numFlashes = 0

  val lastY = dumboOctopuses.lastIndex
  val lastX = dumboOctopuses[0].lastIndex

  val flashed = mutableSetOf<Point>()

  while (toProcess.isNotEmpty()) {
    val (x, y) = toProcess.removeAt(0)

    val hadFlash = dumboOctopuses[x][y].increase() && flashed.add(x by y)
    if (hadFlash) {
      numFlashes += 1
    } else {
      continue
    }

    if (x > 0) {
      if (y > 0) {
        toProcess.add(x - 1 by y - 1)
      }
      toProcess.add(x - 1 by y)
      if (y < lastY) {
        toProcess.add(x - 1 by y + 1)
      }
    }

    if (y > 0) {
      toProcess.add(x by y - 1)
    }
    if (y < lastY) {
      toProcess.add(x by y + 1)
    }

    if (x < lastX) {
      if (y > 0) {
        toProcess.add(x + 1 by y - 1)
      }
      toProcess.add(x + 1 by y)
      if (y < lastY) {
        toProcess.add(x + 1 by y + 1)
      }
    }
  }

  flashed.forEach { (x,y) -> dumboOctopuses[x][y].level = 0 }

  return numFlashes
}

private data class DumboOctopus(var level: Int) {
  val readyToLight
    get() = level >= 9

  val justLit
    get() = level == 0

  fun increase(): Boolean {
    if (readyToLight) {
      level = 0
    } else {
      level += 1
    }
    return level == 0
  }
}

private fun problemTwo(dumboOctopuses: List<List<DumboOctopus>>) {
  var step = 0
  val numOctopuses = dumboOctopuses.size * dumboOctopuses[0].size
  do {
    val totalFlashes = processStep(dumboOctopuses)
//    println(totalFlashes)
//    printOctopuses(dumboOctopuses)
    step += 1
  } while (totalFlashes != numOctopuses)

  println("Problem 2: $step")
}

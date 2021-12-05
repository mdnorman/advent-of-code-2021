package com.denormans.adventofcode

import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings

fun main() {
  val values = loadStrings(4, forTest = true).filter { it.isNotBlank() }

  val numbers = values.first().split(",").map { it.toInt() }
  println(numbers)

  val boards = splitIntoBoards(values.subList(1, values.size))
  boards.forEach {
    displayGrid(it)
    println()
    displayGrid(transformBoard(it))
    println()
  }

  problemOne(numbers, boards)
  problemTwo(numbers, boards)
}

fun splitIntoBoards(values: List<String>): List<List<List<Int>>> =
  values
    .map {
      it.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    }
    .chunked(5)

private fun problemOne(numbers: List<Int>, boards: List<List<List<Int>>>) {
  val numbersSet = numbers.toSet()
  val winningCosts = boards.map { board ->
    val winningLines = linesForBoard(board).filter { line -> numbersSet.containsAll(line) }
    val winningLineCosts = winningLines
      .map { line -> line.map { lineValue -> numbers.indexOf(lineValue )}.maxOf { it }}
    winningLineCosts.minOf { it } to board
  }.toMap()

  println("winningCosts:${winningCosts.keys}")

  val winningCost = winningCosts.keys.minOf { it }
  val justCalled = numbers[winningCost]
  val winningBoard = winningCosts.getValue(winningCost)

  val winningNumbersSet = numbers.subList(0, winningCost + 1).toSet()

  val unmarkedNumbers = winningBoard.flatten().filter { !winningNumbersSet.contains(it) }

  println("Problem 1: winningBoard=$winningBoard, unmarkedNumbers=$unmarkedNumbers (${unmarkedNumbers.sum()}), justCalled:$justCalled: ${unmarkedNumbers.sum() * justCalled}")
}

private fun problemTwo(numbers: List<Int>, boards: List<List<List<Int>>>) {
  val numbersSet = numbers.toSet()
  val winningCosts = boards.map { board ->
    val winningLines = linesForBoard(board).filter { line -> numbersSet.containsAll(line) }
    val winningLineCosts = winningLines
      .map { line -> line.map { lineValue -> numbers.indexOf(lineValue )}.maxOf { it }}
    winningLineCosts.minOf { it } to board
  }.toMap()

  println("winningCosts:${winningCosts.keys}")

  val winningCost = winningCosts.keys.maxOf { it }
  val justCalled = numbers[winningCost]
  val winningBoard = winningCosts.getValue(winningCost)

  val winningNumbersSet = numbers.subList(0, winningCost + 1).toSet()

  val unmarkedNumbers = winningBoard.flatten().filter { !winningNumbersSet.contains(it) }

  println("Problem 2: winningBoard=$winningBoard, unmarkedNumbers=$unmarkedNumbers (${unmarkedNumbers.sum()}), justCalled:$justCalled: ${unmarkedNumbers.sum() * justCalled}")
}

fun linesForBoard(board: List<List<Int>>) = board + transformBoard(board)

fun transformBoard(board: List<List<Int>>)= (1..5).mapIndexed { index, _ ->
  board.map { it[index] }
}

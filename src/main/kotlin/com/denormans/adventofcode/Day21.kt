package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount
import kotlin.random.Random

fun main() {
  val values = loadStrings(21, forTest = false).map {
    val (name, position) = it.split(" starting position: ")
    PlayerData(name, position.toInt(), 0)
  }
  values.println()

  problemOne(values)
  problemTwo(values)
}

private const val numSpaces = 10

private fun problemOne(values: List<PlayerData>) {
  val dice = DeterministicDice()
  val scoreToWin = 1000

  val players = play(values, dice, scoreToWin)

  val loser = players.find { !it.isWinner(scoreToWin) } ?: error("No loser found!")
  println("Loser: $loser")

  println("Rolls: ${dice.numRolls}")

  println("Problem 1: ${loser.score * dice.numRolls}")
}

private fun play(values: List<PlayerData>, dice: Dice, scoreToWin: Int): MutableList<PlayerData> {
  val players = values.toMutableList()

  var winner: PlayerData? = null
  var playerIndex = 0
  while (winner == null) {
    val currentPlayer = players[playerIndex]
    val playerAfterMove = currentPlayer.nextMove(dice)

    if (playerAfterMove.isWinner(scoreToWin)) {
      winner = playerAfterMove
    }

    players[playerIndex] = playerAfterMove

    playerIndex = (playerIndex + 1) % players.size
  }
  return players
}

private fun problemTwo(players: List<PlayerData>) {
  val scoreToWin = 21

//  var player1Wins = 0L
//  var player2Wins = 0L
//  for (i in (1..10)) {
//    val dice = DiracDice()
//    val players = play(values, dice, scoreToWin)
//
//    if (players[0].isWinner(21)) {
//      player1Wins += 1
//    } else {
//      player2Wins += 1
//    }
//  }

//  println("player 1 wins: $player1Wins")
//  println("player 2 wins: $player2Wins")

  println(DiracDice.rollProbabilities)

  val playerProbabilities = winningProbabilities(scoreToWin, players, 0)

  println("player probabilities: $playerProbabilities")
  val max = playerProbabilities.maxOf { (playerIndex, universes) -> universes }

  println("Problem 2: $max")
}

/*
player1
444356092776315
444356092776315
player2
341960390180808
341960390180808
 */

private fun winningProbabilities(winningScore: Int, players: List<PlayerData>, playerIndex: Int): Map<Int, Long> {
  val currentPlayer = players[playerIndex]
  return DiracDice.rollProbabilities.map { (roll, probability) ->
    val newPlayer = currentPlayer.nextMove(roll)
    if (newPlayer.isWinner(winningScore)) {
      mapOf(playerIndex to probability)
    } else {
      val newPlayers = players.mapIndexed { index, player -> if (index == playerIndex) { newPlayer } else { player } }
      val nextPlayerIndex = (playerIndex + 1) % players.size
      winningProbabilities(winningScore, newPlayers, nextPlayerIndex).includeProbability(probability)
    }
  }.reduce { acc, probabilities -> (acc.keys + probabilities.keys)
    .associateWith { key -> acc.getOrDefault(key, 0) + probabilities.getOrDefault(key, 0) }
  }
}

private fun Map<Int, Long>.includeProbability(probability: Long) = mapValues { (_, value) -> value * probability }

private sealed class Dice {
  val numRolls
    get() = roll

  private var roll = 0

  open fun roll(): Int {
    roll += 1
    return roll
  }
}

private class DeterministicDice : Dice()

private class DiracDice : Dice() {
  override fun roll(): Int {
    super.roll()
    return Random.nextInt(1, 4)
  }

  companion object {
    val rollProbabilities: Map<Int, Long> by lazy {
      val rolls = (1..3)
      rolls.flatMap { a -> rolls.flatMap { b -> rolls.map { c -> a + b + c } } }.withCount()
    }
  }
}

private data class PlayerData(val name: String, val position: Int, val score: Int) {
  fun isWinner(winningScore: Int) = score >= winningScore

  fun nextMove(dice: Dice): PlayerData {
    val rolls = (1..3).map { dice.roll() }
    return nextMove(rolls)
  }

  fun nextMove(rolls: List<Int>): PlayerData {
    val rollAmount = rolls.sum()
    val newPlayer = nextMove(rollAmount)
//    println("${newPlayer.name} rolls ${rolls.joinToString("+")} to space ${newPlayer.position} with score ${newPlayer.score}")
    return newPlayer
  }

  fun nextMove(rollAmount: Int): PlayerData {
    val newPosition = nextPosition(position, rollAmount)
    val newScore = score + newPosition
    return copy(position = newPosition, score = newScore)
  }
}

private fun nextPosition(position: Int, roll: Int) = (position + roll - 1) % numSpaces + 1

package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println

fun main() {
  val pairs = loadStrings(12, forTest = false).map {
    val (from, to) = it.split("-")
    from to to
  }
//  pairs.println()

  problemOne(pairs)
  problemTwo(pairs)
}

private fun problemOne(nodes: List<Pair<String, String>>) {
  val paths = findPaths(nodes)
  paths.println()

  println("Problem 1: ${paths.size}")
}

private fun findPaths(connections: List<Pair<String, String>>): List<List<String>> {
  val links = connections.flatMap { (a, b) ->
    when {
      a == "start" -> listOf(a to b)
      b == "start" -> listOf(b to a)
      a == "end" -> listOf(b to a)
      b == "end" -> listOf(a to b)
      else -> listOf(a to b, b to a)
    }
  }
  return findPaths(emptyList(), "start", links, emptySet())
}

private fun findPaths(
  path: List<String>,
  node: String,
  links: List<Pair<String, String>>,
  seenSmallCaves: Set<String>
): List<List<String>> {
  if (node.isSmallCave && node in seenSmallCaves) {
    return emptyList()
  }

  val seen = if (node.isSmallCave) {
    seenSmallCaves + node
  } else {
    seenSmallCaves
  }

  val newPath = path + node

  println(newPath)

  if (node == "end") {
    return listOf(newPath)
  }

  val nextNodes = links.filter { (from, to) -> from == node }
  val paths = mutableListOf<List<String>>()
  nextNodes.forEach { (from, to) ->
    paths.addAll(findPaths(newPath, to, links, seen))
  }
  return paths
}

private val String.isSmallCave: Boolean
  get() = when (this) {
    "start" -> false
    "end" -> false
    else -> matches(smallCaveCheck)
  }

private val smallCaveCheck = Regex("[a-z]+")

private fun problemTwo(nodes: List<Pair<String, String>>) {
  val paths = findPaths2(nodes)
  paths.println()

  println("Problem 2: ${paths.size}")
}

private fun findPaths2(connections: List<Pair<String, String>>): List<List<String>> {
  val links = connections.flatMap { (a, b) ->
    when {
      a == "start" -> listOf(a to b)
      b == "start" -> listOf(b to a)
      a == "end" -> listOf(b to a)
      b == "end" -> listOf(a to b)
      else -> listOf(a to b, b to a)
    }
  }
  return findPaths2(emptyList(), "start", links, emptySet(), emptySet())
}

private fun findPaths2(
  path: List<String>,
  node: String,
  links: List<Pair<String, String>>,
  seenSmallCaves: Set<String>,
  seenSmallCaveAgain: Set<String>
): List<List<String>> {
  if (node.isSmallCave && node in seenSmallCaves) {
    if (seenSmallCaveAgain.isNotEmpty()) {
      return emptyList()
    }
  }

  val seen = if (node.isSmallCave) {
    seenSmallCaves + node
  } else {
    seenSmallCaves
  }

  val seenAgain = if (node.isSmallCave && node in seenSmallCaves) {
    seenSmallCaveAgain + node
  } else {
    seenSmallCaveAgain
  }

  val newPath = path + node

  println(newPath)

  if (node == "end") {
    return listOf(newPath)
  }

  val nextNodes = links.filter { (from, to) -> from == node }
  val paths = mutableListOf<List<String>>()
  nextNodes.forEach { (from, to) ->
    paths.addAll(findPaths2(newPath, to, links, seen, seenAgain))
  }
  return paths
}

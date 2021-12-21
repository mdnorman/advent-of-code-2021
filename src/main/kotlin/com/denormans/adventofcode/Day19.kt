package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Orientation
import com.denormans.adventofcode.utils.TriplePoint
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.to
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount

fun main() {
  val lines = loadStrings(19, forTest = false)
//  lines.println()

  val scannerData = mutableListOf<ScannerData>()
  var currentScannerName = ""
  var currentScannerPoints: MutableList<TriplePoint> = mutableListOf()
  for (line in lines) {
    if (line.startsWith("---")) {
      if (currentScannerName.isNotEmpty()) {
        scannerData.add(ScannerData(currentScannerName, currentScannerPoints.sorted()))
      }

      currentScannerName = line.substringAfter("--- ").substringBeforeLast(" ---")
      currentScannerPoints = mutableListOf()

      continue
    }

    if (line.isBlank()) {
      continue
    }

    currentScannerPoints.add(TriplePoint.parse(line))
  }

  scannerData.add(ScannerData(currentScannerName, currentScannerPoints.sorted()))
  scannerData.println()

  val (scanners, beaconPoints) = computeScanners(scannerData)

  problemOne(beaconPoints)
  problemTwo(scanners)
}

private fun problemOne(beaconPoints: Set<TriplePoint>) {
  println("all points: $beaconPoints")

  println("Problem 1: ${beaconPoints.size}")
}

private fun problemTwo(scanners: List<Scanner>) {
//  println("all scanners: $scanners")

  val (s1, s2) = scanners.flatMap { s1 -> scanners.filterNot { it == s1 }.map { s2 -> s1 to s2 } }
    .maxByOrNull { (s1, s2) -> s1.location.manhattanDistanceFrom(s2.location) } ?: error("No scanners found")

  println("s1: $s1")
  println("s2: $s2")

  val manhattanDistance = s1.location.manhattanDistanceFrom(s2.location)

  println("Problem 2: $manhattanDistance")
}

/*
initial: (+x, +y, +z)
rotate around x1: (+x, -z, +y)
rotate around x2: (+x, -y, -z)
rotate around x3: (+x, +z, -y)
rotate around y1: (-z, +y, +x)
rotate around y2: (-x, +y, -z)
rotate around y3: (+z, +y, -x)
rotate around z1: (-y, +x, +z)
rotate around z2: (-x, -y, +z)
rotate around z3: (+y, -x, +z)

rotate around x1z1: (-z, +x, -y)
 */

private data class Scanner(val name: String, val location: TriplePoint, val orientation: Orientation, val beaconPoints: List<TriplePoint>)

private data class ScannerData(val name: String, val beaconPoints: List<TriplePoint>) {
  val orientations by lazy {
    Orientation.all.map { orientation ->
      orientation to beaconPoints.map { point -> point.withOrientation(orientation) }
    }
  }

  fun findOrientationWithOverlap(otherPoints: List<TriplePoint>) =
    orientations.firstNotNullOfOrNull { (orientation, myPoints) ->
      val offset = getOverlapOffset(otherPoints, myPoints) ?: return@firstNotNullOfOrNull null
      orientation to offset to myPoints.map { p -> p - offset }
    }
}

private fun getOverlapOffset(points1: List<TriplePoint>, points2: List<TriplePoint>, neededCount: Int = 12): TriplePoint? {
  val offsetCounts = points1.flatMap { p1 -> points2.map { p2 -> p2 - p1 } }.withCount()
  val (offset, maxCount) = offsetCounts.maxByOrNull { (offset, count) -> count } ?: error("No offsets found")

  return if (maxCount >= neededCount) {
    offset
  } else {
    null
  }
}

private fun computeScanners(scannerData: List<ScannerData>): Pair<List<Scanner>, Set<TriplePoint>> {
  val firstScanner = scannerData.first()
  val scanners = mutableListOf(Scanner(firstScanner.name, 0 by 0 by 0, Orientation(1, 1, 1), firstScanner.beaconPoints))
  val beaconPoints = firstScanner.beaconPoints.toMutableSet()

  val scannersLeft = scannerData.subList(1, scannerData.size).toMutableList()
  while (scannersLeft.isNotEmpty()) {
    val (nextScanner, foundOrientation) = scannersLeft.map { scanner ->
      scanner to scanner.findOrientationWithOverlap(beaconPoints.toList())
    }.first { (scanner, overlap) -> overlap != null }

    val (orientation, offset, scannerPoints) = foundOrientation!!

    println("Found ${nextScanner.name} at $offset with orientation $orientation")

    beaconPoints.addAll(scannerPoints)
    scannersLeft.remove(nextScanner)

    scanners.add(Scanner(nextScanner.name, offset, orientation, scannerPoints))
  }

  return scanners to beaconPoints
}

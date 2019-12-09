package util

import scala.io.Source
import scala.util.Using

object Util {
  def loadDay(day: Int): String = {
    Using(Source.fromResource(s"$day.txt")) {
      source => source.mkString.strip()
    }.get
  }

  def loadDayLines(day: Int): List[String] = loadDay(day).split("\n").toList

  def loadDayInts(day: Int): List[Int] = loadDayLines(day).map(_.toInt)

  def loadDayProgram(day: Int): Map[Long, Long] =
    Util.loadDay(day)
    .split(",")
    .map { x => x.toLong }
    .zipWithIndex
    .map { x => (x._2.toLong, x._1) }.toMap
}


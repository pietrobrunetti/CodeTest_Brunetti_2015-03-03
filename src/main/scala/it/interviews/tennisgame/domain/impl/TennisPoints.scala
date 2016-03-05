package it.interviews.tennisgame.domain.impl

import it.interviews.tennisgame.domain.Points

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisPoints(override val sym:Option[Symbol],override val value: Int) extends Points(sym,value) {
  require(
    (this.isInstanceOf[FirstThreeTennisPoints] && value <= 3) || (this.isInstanceOf[TennisPoints] && value > 3)
    ,"TennisPoint with values between 0 and 3 should be instance of 'FirstThreeTennisPoints' class OR " +
      "should be instance of 'TennisPoint' if values is greater than 3")
}
object TennisPoints {
  def apply(sym:Option[Symbol],value: Int):TennisPoints = new TennisPoints(sym,value)
}

sealed class FirstThreeTennisPoints(override val sym:Option[Symbol],override val value: Int) extends TennisPoints(sym,value)
class Love extends FirstThreeTennisPoints(Some('love),0)
object Love {
  def apply():Love = new Love
}
class Fifteen extends FirstThreeTennisPoints(Some('fifteen),1)
object Fifteen {
  def apply():Fifteen = new Fifteen
}
class Thirty extends FirstThreeTennisPoints(Some('thirty),2)
object Thirty {
  def apply():Thirty = new Thirty
}
class Forty extends FirstThreeTennisPoints(Some('forty),3)
object Forty {
  def apply():Forty = new Forty
}

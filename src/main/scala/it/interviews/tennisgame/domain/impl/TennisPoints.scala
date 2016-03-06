package it.interviews.tennisgame.domain.impl

import it.interviews.tennisgame.domain.Points

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisPoints(override val value: Int,override val sym:Option[Symbol]) extends Points(value,sym) {
  require(
    (this.isInstanceOf[FirstThreeTennisPoints] && value <= 3) || (this.isInstanceOf[TennisPoints] && value > 3)
    ,"TennisPoint with values between 0 and 3 should be instance of 'FirstThreeTennisPoints' class OR " +
      "should be instance of 'TennisPoint' if values is greater than 3")
}
object TennisPoints {
  def apply(value: Int,sym:Option[Symbol]=None):TennisPoints = {
    value match {
      case v if(v<=3 && v>=0) => FirstThreeTennisPoints(v)
      case _ => new TennisPoints(value,sym)
    }
  }
}

sealed class FirstThreeTennisPoints(override val value: Int, override val sym:Option[Symbol]) extends TennisPoints(value,sym)
class Love extends FirstThreeTennisPoints(0,Some('love))
object Love {
  def apply():Love = new Love
}
class Fifteen extends FirstThreeTennisPoints(1,Some('fifteen))
object Fifteen {
  def apply():Fifteen = new Fifteen
}
class Thirty extends FirstThreeTennisPoints(2,Some('thirty))
object Thirty {
  def apply():Thirty = new Thirty
}
class Forty extends FirstThreeTennisPoints(3,Some('forty))
object Forty {
  def apply():Forty = new Forty
}
object FirstThreeTennisPoints {
  def apply(value:Int):FirstThreeTennisPoints = {
    value match {
      case 0 => Love()
      case 1 => Fifteen()
      case 2 => Thirty()
      case 3 => Forty()
    }
  }
}

package it.interviews.tennisgame.domain

/**
  * Created by Pietro Brunetti on 04/03/16.
  */

trait Scores {
  def getStringRep:String
}

abstract case class Points(val sym:Option[Symbol],val value: Int) {
  require(value>=0, s"Negative point value inserted: ${value}.")
}
abstract case class PlayerIdWithPoints(val playerId:String, val points:Points)
abstract case class GameStateData(val scores:Scores,val pPoints:PlayerIdWithPoints*) {
  override def toString:String = s"Game Actual State: << ${scores.getStringRep} >>\n Points:\n${pPoints.foreach(pp=>s"\t${pp.playerId} => ${pp.points}")}"
}

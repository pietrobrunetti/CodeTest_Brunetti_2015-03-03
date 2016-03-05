package it.interviews.tennisgame.domain.impl

import it.interviews.tennisgame.domain.PlayerIdWithPoints

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisPlayerIdWithPoints(override val playerId:String, override val points:TennisPoints) extends PlayerIdWithPoints(playerId,points)
object TennisPlayerIdWithPoints {
  def apply(playerId:String, points:TennisPoints):TennisPlayerIdWithPoints = new TennisPlayerIdWithPoints(playerId,points)
}

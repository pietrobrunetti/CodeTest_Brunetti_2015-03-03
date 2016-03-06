package it.interviews.tennisgame.domain.impl

import it.interviews.tennisgame.domain.{GameStateData, PlayerIdWithPoints, Points, Scores}

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
sealed trait TennisScores extends Scores

case class UpTo40Score(val pip1: TennisPlayerIdWithPoints, val pip2: TennisPlayerIdWithPoints) extends TennisScores {
  require(pip1.points.isInstanceOf[FirstThreeTennisPoints] && pip2.points.isInstanceOf[FirstThreeTennisPoints],"Points in UpTo40 TennisScore must be instances of 'FirstThreeTennisPoints' class")
  override def getStringRep:String = s"Player ${pip1.playerId.toString}: ${pip1.points} :: Player ${pip2.playerId.toString}: ${pip2.points}"
}
case class DeuceScore(val points: TennisPoints) extends TennisScores {
  require(points.value>=3,"Deuce TennisScore can't be < 3")
  override def getStringRep:String = s"Deuce at ${points.value} points"
}
case class AdvantageScore(val who:TennisPlayerIdWithPoints) extends TennisScores {
  require(who.points.value>3,"Advantage TennisScore can't be < OR == 3")
  override def getStringRep:String = s"Advantage for Player ${who.playerId.toString}"
}
case class WonScore(val who:TennisPlayerIdWithPoints) extends TennisScores {
  require(who.points.value>3,"Won TennisScore can't be < OR == 3")
  override def getStringRep:String = s"The Winner is Player ${who.playerId.toString} with ${who.points} points!"
}

object TennisScores {
  def apply(tpiwp1: TennisPlayerIdWithPoints, tpiwp2: TennisPlayerIdWithPoints):TennisScores = {

    def max(p1: TennisPlayerIdWithPoints, p2: TennisPlayerIdWithPoints):Option[TennisPlayerIdWithPoints] =
      p1.points.value match {
        case v if v > p2.points.value => Some(p1)
        case v if v < p2.points.value => Some(p2)
        case _ => None
      }

    if(tpiwp1.points.value < 3 && tpiwp2.points.value < 3)
      UpTo40Score(tpiwp1,tpiwp2)
    if( Math.abs(tpiwp1.points.value - tpiwp2.points.value) >= 2)
      WonScore(max(tpiwp1,tpiwp2).get)
    if(tpiwp1.points.value == tpiwp2.points.value) {
      tpiwp1.points.value match {
        case 3 => Forty
        case greater => TennisPoints(None,greater)
      }
    }
    AdvantageScore(max(tpiwp1,tpiwp2).get)
  }
}
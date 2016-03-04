package it.interviews.tennisgame.domain

/**
  * Created by Pietro Brunetti on 04/03/16.
  */

case class TennisPoints(override val sym:Option[Symbol],override val value: Int) extends Points(sym,value) {
  assume{
    (this.isInstanceOf[FirstThreeTennisPoints] && value <= 4) || (this.isInstanceOf[TennisPoints] && value > 4)
  }
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

sealed trait TennisScores extends Scores

case class UpTo40(val pip1: PlayerIdWithPoints,val pip2: PlayerIdWithPoints) extends TennisScores {
  require(pip1.points.isInstanceOf[FirstThreeTennisPoints] && pip2.points.isInstanceOf[FirstThreeTennisPoints])
  override def getStringRep:String = s"Player ${pip1.playerId.toString}: ${pip1.points} :: Player ${pip2.playerId.toString}: ${pip2.points}"
}
case class Deuce(val points: TennisPoints) extends TennisScores {
  require(points.value>=3)
  override def getStringRep:String = s"Deuce at ${points.value} points"
}
case class Advantage(val who:PlayerIdWithPoints) extends TennisScores {
  require(who.points.value>3)
  override def getStringRep:String = s"Advantage for Player ${who.playerId.toString}"
}
case class Won(val who:PlayerIdWithPoints) extends TennisScores {
  require(who.points.value>=3)
  override def getStringRep:String = s"The Winner is Player ${who.playerId.toString} with ${who.points} points!"
}

case class TennisGameStateData(override val scores:Scores,val p1P:PlayerIdWithPoints,val p2P:PlayerIdWithPoints) extends GameStateData(scores,p1P,p2P) {

}

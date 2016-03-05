package it.interviews.tennisgame.domain.impl

import it.interviews.tennisgame.domain.GameStateData

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisGameStateData(override val scores:TennisScores,val p1P:TennisPlayerIdWithPoints,val p2P:TennisPlayerIdWithPoints) extends GameStateData(scores,p1P,p2P) {

  require(p1P.playerId != p2P.playerId,s"Players with the same Id '${p1P.playerId}'")

  require (
    scores match {
      case s:UpTo40Score => (s.pip1 == p1P) && (s.pip2 == p2P)
      case s: DeuceScore => (p1P.points.value == p2P.points.value) && (p1P.points.value == s.points.value)
      case s: AdvantageScore => (s.who.playerId == p1P.playerId && s.who.points == p1P.points && s.who.points.value == p2P.points.value+1) || (s.who.playerId == p2P.playerId && s.who.points == p2P.points && s.who.points.value == p1P.points.value+1)
      case s: WonScore => (s.who.playerId == p1P.playerId && s.who.points == p1P.points && s.who.points.value >= p2P.points.value+2) || (s.who.playerId == p2P.playerId && s.who.points == p2P.points && s.who.points.value >= p1P.points.value+2)
      case _ => false
    }
    ,"Wrong correlations between scores and players in TennisGameStateData")
}
object TennisGameStateData {
  def apply(scores:TennisScores, p1P:TennisPlayerIdWithPoints, p2P:TennisPlayerIdWithPoints):TennisGameStateData = new TennisGameStateData(scores,p1P,p2P)
}

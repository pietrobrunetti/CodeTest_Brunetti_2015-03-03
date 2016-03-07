package it.interviews.tennisgame.domain.impl.actors

import akka.actor.ActorRef
import it.interviews.tennisgame.dal.impl.actors.TennisScoreboard
import it.interviews.tennisgame.domain.impl.{TennisPlayerIdWithPoints, TennisScores}
import it.interviews.tennisgame.dal.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */

sealed trait TennisGameFsmState
case object Idle extends TennisGameFsmState
case object Initial extends TennisGameFsmState
case object UpTo40 extends TennisGameFsmState
case object Deuce extends TennisGameFsmState
case object Advantage extends TennisGameFsmState
case object Won extends TennisGameFsmState

sealed trait TennisGameFsmData
case object Uninitialized extends TennisGameFsmData
case class MatchSnapshot(actualScores:TennisScores, player1Id:String, player2Id:String, scorer: ActorRef) extends TennisGameFsmData


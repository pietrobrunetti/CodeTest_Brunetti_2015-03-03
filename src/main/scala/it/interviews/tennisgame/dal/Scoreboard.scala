package it.interviews.tennisgame.dal

import it.interviews.tennisgame.boundary.ParticipantActor
import it.interviews.tennisgame.domain.impl.{TennisGameStateData, TennisScores}

import scala.collection.mutable

/**
  * Created by Pietro Brunetti on 06/03/16.
  */
trait Scoreboard {

  var subscriber = mutable.ListBuffer[ParticipantActor]()
  var scoresHistory = mutable.Stack[TennisGameStateData]()

  protected def updateInternalCache(scores:TennisScores):Unit
  protected def spawnUpdateToSubscribers():Unit
  protected def informAboutScoresState():TennisScores


}

package it.interviews.tennisgame.dal

import java.util.concurrent.Exchanger.Participant

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor}
import it.interviews.tennisgame.boundary.ParticipantActor
import it.interviews.tennisgame.domain.impl.{TennisScores, TennisGameStateData}

import scala.collection.mutable

/**
  * Created by Pietro Brunetti on 06/03/16.
  */
trait Scoreboard {

  var subscriber = mutable.ListBuffer[ParticipantActor]()
  var scoresHistory = mutable.Stack[TennisGameStateData]()

  protected def updateInternalCache(scores:TennisScores)
  protected def spawnUpdateToSubscribers


}

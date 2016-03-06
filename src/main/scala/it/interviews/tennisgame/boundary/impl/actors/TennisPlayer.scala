package it.interviews.tennisgame.boundary.impl.actors

import it.interviews.tennisgame.boundary.PlayerActor
import it.interviews.tennisgame.domain.impl.Love
import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisPlayer extends PlayerActor{

  override def preStart() = setInitialPoints(Love())

  override def receive: Receive = ???

  override protected def makePoint: Unit = ???

  override protected def joinGame: Unit = ???

  override protected def retrieveScores: Scores = ???

  override protected def retrieveGameState: GameStateData = ???

  override protected def retrievePlayerPoints(p: PlayerActor): Points = ???
}

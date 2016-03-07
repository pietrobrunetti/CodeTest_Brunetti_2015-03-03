package it.interviews.tennisgame.boundary.impl.actors

import akka.actor.{ActorSystem, Props, ActorRef, Actor}
import akka.actor.Actor.Receive
import it.interviews.tennisgame.boundary.PlayerActor
import it.interviews.tennisgame.domain.impl.{TennisGameStateData, Love}
import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisPlayer extends PlayerActor with Actor{

  override def playerId: String = self.path.name

  override protected def makePoint: Unit = ???

  override protected def retrieveScores: Scores = ???

  override protected def retrieveGameState: GameStateData = ???

  override protected def retrievePlayerPoints(p: PlayerActor): Points = ???

  override def receive: Actor.Receive = {
    case x:TennisGameStateData => context.system.log.info("Scores are been Updated! "+x)
  }

  override def getRef: ActorRef = self
}


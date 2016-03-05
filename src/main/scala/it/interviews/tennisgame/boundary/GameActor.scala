package it.interviews.tennisgame.boundary

import akka.actor.Actor
import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait GameActor extends Actor{
  protected def init(players: PlayerActor*)
  protected def start
  protected def managePointMade(p:PlayerActor)
  protected def getPointsForPlayer(p:PlayerActor):Points
  protected def getScores:Scores
  protected def getGameState:GameStateData
  protected def stop
}

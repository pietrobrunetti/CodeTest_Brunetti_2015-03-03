package it.interviews.tennisgame.boundary

import akka.actor.Actor
import it.interviews.tennisgame.domain.{PlayerIdWithPoints, GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait GameActor extends Actor{

  protected def playing:Receive

  protected def init(players: PlayerActor*)
  protected def start
  protected def managePointMade(p:PlayerActor)
  protected def getPointsForPlayer(p:PlayerActor):Points
  protected def getScores:Scores
  protected def getLeadPlayer:PlayerIdWithPoints
  protected def getGameState:GameStateData
  protected def stop
}

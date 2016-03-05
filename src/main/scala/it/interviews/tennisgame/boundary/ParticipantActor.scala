package it.interviews.tennisgame.boundary

import akka.actor.Actor
import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait ParticipantActor extends Actor{

  protected def retrievePlayerPoints(p:PlayerActor):Points
  protected def retrieveScores:Scores
  protected def retrieveGameState:GameStateData

}

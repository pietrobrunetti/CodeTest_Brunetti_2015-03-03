package it.interviews.tennisgame.boundary

import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait ParticipantActor {

  protected def retrievePlayerPoints(p:PlayerActor):Points
  protected def retrieveScores:Scores
  protected def retrieveGameState:GameStateData

}

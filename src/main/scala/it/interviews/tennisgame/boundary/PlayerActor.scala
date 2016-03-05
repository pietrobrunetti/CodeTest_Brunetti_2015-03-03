package it.interviews.tennisgame.boundary

import it.interviews.tennisgame.domain.Points

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait PlayerActor extends ParticipantActor{

  override protected def retrievePlayerPoints(p:PlayerActor):Points = super.retrievePlayerPoints(this)
  protected def makePoint
  protected def joinGame
}



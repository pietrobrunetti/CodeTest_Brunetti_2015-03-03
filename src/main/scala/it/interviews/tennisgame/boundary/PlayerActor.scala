package it.interviews.tennisgame.boundary

import it.interviews.tennisgame.domain.Points
import it.interviews.tennisgame.domain.impl.{Love, TennisPoints}

/**
  * Created by Pietro Brunetti on 05/03/16.
  */
trait PlayerActor extends ParticipantActor{

  var personalPoints:Points = null

  def playerId:String

  protected def setInitialPoints(p:Points) = personalPoints = p
  protected def retrievePlayerPoints(p:PlayerActor):Points
  protected def makePoint
}



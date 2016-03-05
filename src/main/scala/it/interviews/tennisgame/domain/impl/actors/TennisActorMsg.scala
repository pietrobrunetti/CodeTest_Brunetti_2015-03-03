package it.interviews.tennisgame.domain.impl.actors

import it.interviews.tennisgame.boundary.impl.actors.TennisPlayer

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
sealed trait GameActorMsg
case class InitGame(p1:TennisPlayer, p2:TennisPlayer) extends GameActorMsg
case object StartGame extends GameActorMsg
case object StopGame extends GameActorMsg




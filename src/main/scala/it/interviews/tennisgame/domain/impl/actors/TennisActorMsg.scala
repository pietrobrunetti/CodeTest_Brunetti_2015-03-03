package it.interviews.tennisgame.domain.impl.actors

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
sealed trait TennisActorMsg
case object Start extends TennisActorMsg
case class Init(p1:Player,p2:Player)




package it.interviews.tennisgame.dal.impl.actors

import akka.actor.Actor
import akka.actor.Actor.Receive
import it.interviews.tennisgame.dal.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisScoreboard extends Scoreboard{

  //var

  override def receive: Receive = {
    case _ =>
  }
}

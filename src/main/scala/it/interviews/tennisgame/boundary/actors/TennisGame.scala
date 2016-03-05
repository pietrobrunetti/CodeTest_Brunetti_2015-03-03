package it.interviews.tennisgame.boundary.actors

import akka.actor.{Actor, Props}
import it.interviews.tennisgame.controller.actors.GameController
import it.interviews.tennisgame.domain.impl.actors.Start
import it.interviews.tennisgame.storage.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisGame extends Actor{

  val ctrl = context.actorOf(Props[GameController],"GameController")
  val scorer = context.actorOf(Props[Scoreboard],"Scoreboard")

  override def preStart = ???

  override def receive: Receive = {
    case Init(p1:Player,p2:Player) => ???
    case Start => ??? //TODO init controller and scoreboard
  }

}

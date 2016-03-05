package it.interviews.tennisgame.boundary.impl.actors

import akka.actor.{Actor, Props}
import it.interviews.tennisgame.boundary.{PlayerActor, GameActor}
import it.interviews.tennisgame.controller.impl.actors.GameController
import it.interviews.tennisgame.domain.{GameStateData, Points, Scores}
import it.interviews.tennisgame.domain.impl.actors.{StopGame, StartGame, InitGame}
import it.interviews.tennisgame.storage.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisGame extends Actor with GameActor{

  val ctrl = context.actorOf(Props[GameController],"GameController")
  val scorer = context.actorOf(Props[Scoreboard],"Scoreboard")

  override def preStart = ???

  override def receive: Receive = {
    case InitGame(p1:TennisPlayer,p2:TennisPlayer) => init(p1,p2)
    case StartGame => start

    case StopGame => stop
  }

  override protected def init(players: PlayerActor*): Unit = {
    players.apply(2).asInstanceOf[TennisPlayer]
  }

  override def stop: Unit = ??? //TODO dispose controller and scoreboard and terminate it

  override def getGameState: GameStateData = ???

  override def getPointsForPlayer(p: PlayerActor): Points = ???

  override def getScores: Scores = ???

  override def start: Unit = ???

  override def managePointMade(p: PlayerActor): Unit = ???
}

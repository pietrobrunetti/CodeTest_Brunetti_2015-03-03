package it.interviews.tennisgame.boundary.impl.actors

import akka.actor.{ActorRef, Props}
import it.interviews.tennisgame.boundary.{PlayerActor, GameActor}
import it.interviews.tennisgame.controller.impl.actors.GameController
import it.interviews.tennisgame.domain.impl.TennisPlayerIdWithPoints
import it.interviews.tennisgame.domain.{PlayerIdWithPoints, GameStateData, Points, Scores}
import it.interviews.tennisgame.domain.impl.actors._
import it.interviews.tennisgame.dal.impl.actors.TennisScoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisGame extends GameActor{

  val ctrl = context.actorOf(Props[GameController],"GameController")
  val scorer = context.actorOf(Props[TennisScoreboard],"Scoreboard")

  override def preStart = ???

  override def receive: Receive = {
    case InitGame(p1:PlayerActor,p2:PlayerActor) => init(p1,p2)
    case StartGame => start; context.become(playing)
  }

  override protected def playing:Receive = {
    case PointMade => managePointMade(sender().asInstanceOf[PlayerActor])
    case StopGame => stop
  }

  override protected def init(players: PlayerActor*): Unit = {
    ctrl.tell(GameConfig(players(1).playerId, players(2).playerId,scorer),context.self)
  }

  override def stop: Unit = ??? //TODO dispose controller and scoreboard and terminate it

  override def getGameState: GameStateData = ???

  override def getPointsForPlayer(p: PlayerActor): Points = ???

  override def getScores: Scores = ???

  override def start: Unit = ctrl.forward(GameStarted)

  override def managePointMade(p: PlayerActor): Unit = ctrl.forward(LastPointMadeBy(p.playerId))

  override protected def getLeadPlayer: PlayerIdWithPoints = ???
}

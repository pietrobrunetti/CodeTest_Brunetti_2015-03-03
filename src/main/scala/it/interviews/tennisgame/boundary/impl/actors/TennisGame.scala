package it.interviews.tennisgame.boundary.impl.actors

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import it.interviews.tennisgame.boundary.{ParticipantActor, PlayerActor, GameActor}
import it.interviews.tennisgame.controller.impl.actors.GameController
import it.interviews.tennisgame.dal.Scoreboard
import it.interviews.tennisgame.domain.impl.{WonScore, TennisPlayerIdWithPoints}
import it.interviews.tennisgame.domain.{PlayerIdWithPoints, GameStateData, Points, Scores}
import it.interviews.tennisgame.domain.impl.actors._
import it.interviews.tennisgame.dal.impl.actors.TennisScoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisGame extends GameActor with Actor with ActorLogging{

  val scorer = context.actorOf(Props[TennisScoreboard],"Scoreboard")
  val ctrl = context.actorOf(Props[GameController],"GameController")

  override def receive: Receive = {
    case InitGame(p1:PlayerActor,p2:PlayerActor) => init(p1,p2)
    case StartGame => start; context.become(playing)
  }

  override protected def playing:Receive = {
    case WantToObserveGameState(pa:ParticipantActor) => registerNewParticipant(pa)
    case PointMade(null,p:PlayerActor) => managePointMade(p)
    case GameFinished => context.stop(ctrl)
    case StopGame => stop
  }

  override protected def init(players: PlayerActor*): Unit = {
    ctrl.tell(GameConfig(players(0).playerId, players(1).playerId,scorer),context.self)
  }

  override def start: Unit = {
    ctrl.forward(GameStarted)
  }

  override def getGameState: GameStateData = ???

  override def getPointsForPlayer(p: PlayerActor): Points = ???

  override def getScores: Scores = ???

  override protected def registerNewParticipant(participant:ParticipantActor) = {
    scorer ! ListenerSubscription(participant)
  }

  override def managePointMade(p: PlayerActor): Unit = {
    ctrl.forward(LastPointMadeBy(p.playerId))
  }

  override protected def getLeadPlayer: PlayerIdWithPoints = ???

  override def stop: Unit = {
    context.children.foreach(context.stop(_))
    context.system.terminate()
  }
}

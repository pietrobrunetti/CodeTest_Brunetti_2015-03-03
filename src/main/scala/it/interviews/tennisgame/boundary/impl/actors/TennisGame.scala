package it.interviews.tennisgame.boundary.impl.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import it.interviews.tennisgame.boundary.{GameActor, ParticipantActor, PlayerActor}
import it.interviews.tennisgame.controller.impl.actors.GameController
import it.interviews.tennisgame.dal.impl.actors.TennisScoreboard
import it.interviews.tennisgame.domain.impl.TennisScores
import it.interviews.tennisgame.domain.impl.actors._
import it.interviews.tennisgame.domain.{GameStateData, PlayerIdWithPoints, Points}

import scala.concurrent.Await

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
    case ScoresState => getScores()
    case GameFinished => context.stop(ctrl)
    case StopGame => stop
  }

  override protected def init(players: PlayerActor*): Unit = {
    val future = ctrl ? GameConfig(players(0).playerId, players(1).playerId,scorer)
    Await.result(future,defaultFiniteDureationAsk)
  }

  override def start: Unit = {
    ctrl.forward(GameStarted)
  }

  override def getGameState: GameStateData = ???

  override def getPointsForPlayer(p: PlayerActor): Points = ???

  override def getScores(): Unit = { sender() ! Await.result(ask(scorer, ScoresState).mapTo[TennisScores],defaultFiniteDureationAsk)}//.mapTo[TennisScores] }//Await.result(ask(scorer,ScoresState).asInstanceOf[TennisScores])

  override protected def registerNewParticipant(participant:ParticipantActor) = {
    scorer ! ListenerSubscription(participant)
  }

  override def managePointMade(p: PlayerActor): Unit = {
    sender() ! Await.result(ask(ctrl, LastPointMadeBy(p.playerId),sender()).mapTo[String],defaultFiniteDureationAsk)
  }

  override protected def getLeadPlayer: PlayerIdWithPoints = ???

  override def stop: Unit = {
    context.children.foreach(context.stop(_))
    context.system.terminate()
  }
}

package it.interviews.tennisgame.domain.impl.actors

import akka.actor.ActorRef
import it.interviews.tennisgame.boundary.{ParticipantActor, PlayerActor}
import it.interviews.tennisgame.domain.impl.{TennisPlayerIdWithPoints, TennisScores}

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
sealed trait GameActorMsg

sealed trait EnvGameActorMsg extends GameActorMsg
case class InitGame(p1:PlayerActor, p2:PlayerActor) extends EnvGameActorMsg
case object StartGame extends EnvGameActorMsg
case class WantToObserveGameState(pa:ParticipantActor)
case class PointMade(tennisPlayerIdWithPoints: TennisPlayerIdWithPoints = null,playerActor: PlayerActor = null) extends EnvGameActorMsg
case object StopGame extends EnvGameActorMsg

sealed trait InternalGameActorMsg extends GameActorMsg
case class GameConfig(p1Name:String,p2Name:String,ref: ActorRef = ActorRef.noSender) extends InternalGameActorMsg
case class SetPlayerInfo(p1Name:TennisPlayerIdWithPoints,p2Name:TennisPlayerIdWithPoints) extends InternalGameActorMsg
case class ListenerSubscription(pa:ParticipantActor) extends InternalGameActorMsg
case class ScoresUpdate(tennisScores: TennisScores) extends InternalGameActorMsg
case object ScoresState extends InternalGameActorMsg
case object GameFinished extends InternalGameActorMsg

sealed trait TennisGameFsmEvent
case class LastPointMadeBy(tennisPlayerId: String) extends TennisGameFsmEvent
case object GameStarted extends TennisGameFsmEvent





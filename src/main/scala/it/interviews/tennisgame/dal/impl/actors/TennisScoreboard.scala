package it.interviews.tennisgame.dal.impl.actors

import akka.actor.{Actor, ActorLogging}
import it.interviews.tennisgame.boundary.ParticipantActor
import it.interviews.tennisgame.dal.Scoreboard
import it.interviews.tennisgame.domain.impl._
import it.interviews.tennisgame.domain.impl.actors.{ScoresState, ListenerSubscription, ScoresUpdate, SetPlayerInfo}

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class TennisScoreboard extends Scoreboard with Actor with ActorLogging{

  private var player1:TennisPlayerIdWithPoints = null
  private var player2:TennisPlayerIdWithPoints = null

  override def receive: Receive = {

    case SetPlayerInfo(p1:TennisPlayerIdWithPoints,p2:TennisPlayerIdWithPoints) => {
      player1 = p1
      player2 = p2
      scoresHistory.push(TennisGameStateData(UpTo40Score(p1, p2), p1, p2))
    }
    case ListenerSubscription(pa:ParticipantActor) => {
      subscriber += pa
    }
    case ScoresUpdate(ts:TennisScores) => {
      updateInternalCache(ts)
      scoresHistory.push(TennisGameStateData(ts, player1, player2))
      sender() ! "Ok"
      spawnUpdateToSubscribers

    }
    case ScoresState => {
      sender ! informAboutScoresState
    }
  }

  override protected def spawnUpdateToSubscribers: Unit = {
    val currentGameStatus:TennisGameStateData = scoresHistory.top
    subscriber.foreach(sub=>sub.actor.tell(currentGameStatus,self ))
  }

  protected def informAboutScoresState: TennisScores = {
    scoresHistory.top.scores
  }

  override protected def updateInternalCache(scores:TennisScores) = {
    val pl1Tmp = player1.playerId
    val pl2Tmp = player2.playerId
    scores match {
      case UpTo40Score(p1Id,p2Id) => player1 = p1Id; player2 = p2Id
      case DeuceScore(equalScore) => {
        player1 = TennisPlayerIdWithPoints(player1.playerId,equalScore)
        player2 = TennisPlayerIdWithPoints(player2.playerId,equalScore)
      };
      case AdvantageScore(advPlayer) => advPlayer.playerId match {
        case adv if adv == pl1Tmp => player1 = advPlayer
        case adv if adv == pl2Tmp => player2 = advPlayer
      }
      case WonScore(wonPlayer) => wonPlayer.playerId match {
        case adv if adv == pl1Tmp => player1 = wonPlayer
        case adv if adv == pl2Tmp => player2 = wonPlayer
      }
    }
  }

}

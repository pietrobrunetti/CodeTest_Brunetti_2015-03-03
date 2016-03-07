package it.interviews.tennisgame.controller.impl.actors

import akka.actor.FSM.State
import akka.actor.{ActorRef, FSM}
import it.interviews.tennisgame.domain.impl._
import it.interviews.tennisgame.domain.impl.actors._
import it.interviews.tennisgame.dal.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class GameController extends FSM[TennisGameFsmState,TennisGameFsmData]{

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(GameConfig(p1:String,p2:String,scorer:ActorRef), Uninitialized) =>
      goto(Initial) using MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints(p1,Love()),TennisPlayerIdWithPoints(p2,Love())),p1,p2,scorer)
  }

  when(Initial) {
    case Event(GameStarted,fsmStateData) => goto(UpTo40) using fsmStateData
  }

  when(UpTo40) {
    case Event(LastPointMadeBy(pId:String),MatchSnapshot(UpTo40Score(p1,p2),p1Id,p2Id,scorer)) =>
      GameController.ctrlInputId(pId,stateData.asInstanceOf[MatchSnapshot]){
        TennisScores(p1,p2,Some(pId)) match {
          case s:UpTo40Score => stay using MatchSnapshot(s,p1Id,p2Id,scorer)
          case s:DeuceScore => goto(Deuce) using MatchSnapshot(s,p1Id,p2Id,scorer)
          case s:WonScore => goto(Won) using MatchSnapshot(s,p1Id,p2Id,scorer)
        }
      }(stop(FSM.Failure(), stateData))
  }

  when(Deuce) {
    case Event(LastPointMadeBy(pId:String),MatchSnapshot(DeuceScore(p),p1Id,p2Id,scorer)) =>
      GameController.ctrlInputId(pId,stateData.asInstanceOf[MatchSnapshot]) {
        goto(Advantage) using MatchSnapshot(AdvantageScore(TennisPlayerIdWithPoints(pId, TennisPoints(p.value + 1))), p1Id, p2Id, scorer)
      }(stop(FSM.Failure(), stateData))
  }

  when(Advantage) {
    case Event(LastPointMadeBy(pId:String),MatchSnapshot(AdvantageScore(p),p1Id,p2Id,scorer)) =>
      GameController.ctrlInputId(pId,stateData.asInstanceOf[MatchSnapshot]) {
        pId match {
          case p.playerId => goto(Won) using MatchSnapshot(WonScore(TennisPlayerIdWithPoints(p.playerId,TennisPoints(p.points.value+1))),p1Id,p2Id,scorer)
          case _ => goto(Deuce) using MatchSnapshot(DeuceScore(p.points),p1Id,p2Id,scorer)
        }
      }(stop(FSM.Failure(), stateData))
  }

  when(Won) {
    case _ => stay using stateData
  }

  whenUnhandled {
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stop(FSM.Failure(e), s)
  }

  onTransition {
    case Idle -> Initial =>
      nextStateData match {case MatchSnapshot(UpTo40Score(p1,p2),p1Id,p2Id,scorer) => scorer ! SetPlayerInfo(p1,p2)}

    case UpTo40 -> UpTo40 => nextStateData match {case MatchSnapshot(score,p1Id,p2Id,scorer) => scorer ! ScoresUpdate(score)}
    case UpTo40 -> Deuce => nextStateData match {case MatchSnapshot(score,p1Id,p2Id,scorer) => scorer ! ScoresUpdate(score)}
    case UpTo40 -> Won => nextStateData match {case MatchSnapshot(score,p1Id,p2Id,scorer) => scorer ! ScoresUpdate(score)}
    case Deuce -> Advantage => nextStateData match {case MatchSnapshot(score,p1Id,p2Id,scorer) => scorer ! ScoresUpdate(score)}
    case Advantage -> Deuce => nextStateData match {case MatchSnapshot(score,p1Id,p2Id,scorer) => scorer ! ScoresUpdate(score)}
    case Advantage -> Won => nextStateData match {
      case MatchSnapshot(score,p1Id,p2Id,scorer) =>
        scorer ! ScoresUpdate(score)
        context.parent ! GameFinished
    }

  }

  onTermination {
    case StopEvent(FSM.Normal, state, data)         => log.info("Stop in state {} for {}",state,FSM.Normal)
    case StopEvent(FSM.Shutdown, state, data)       => log.info("Shutdown")
    case StopEvent(FSM.Failure(cause), state, data) => log.info("Failure!! {} {}",stateName.toString, stateData.toString)
  }

  initialize()

}

object GameController {

  def ctrlInputId(id:String, currentFsmData: MatchSnapshot)
                 (okAction: => State[TennisGameFsmState, TennisGameFsmData])
                 (errorAction: => State[TennisGameFsmState, TennisGameFsmData]):State[TennisGameFsmState, TennisGameFsmData] = {
    if (id == currentFsmData.player1Id || id == currentFsmData.player2Id)
      okAction
    else errorAction
  }
}
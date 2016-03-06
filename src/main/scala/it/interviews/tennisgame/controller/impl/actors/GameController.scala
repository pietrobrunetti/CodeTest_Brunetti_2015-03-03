package it.interviews.tennisgame.controller.impl.actors

import akka.actor.{FSM}
import it.interviews.tennisgame.domain.impl._
import it.interviews.tennisgame.domain.impl.actors._
import it.interviews.tennisgame.dal.Scoreboard

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class GameController extends FSM[TennisGameFsmState,TennisGameFsmData]{

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(GameConfig(p1:String,p2:String,scorer:Scoreboard), Uninitialized) =>
      goto(Initial) using MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints(p1,Love()),TennisPlayerIdWithPoints(p2,Love())),scorer)
  }

  when(Initial) {
    case Event(GameStarted,fsmStateData) => goto(UpTo40) using fsmStateData
  }

  when(UpTo40) {
    case Event(LastPointMadeBy(pId:String),MatchSnapshot(UpTo40Score(p1,p2),scorer)) =>
      //updateInternalState:
      val tmpScores = TennisScores(p1,p2,pId)
      Seq(p1,p2).map(item => item match {case item.playerId == pId => TennisScores})
      p1.points.value < 3 && p2.points.value
      //se punti di entrambi < 3 => rimango in questo stato ed aggiorno
      //se punti >= 3 => cambio stato:
                            //se diff assoluta >= 2 => won
                            //se == => deuce
                            // => advantage
  }

/*
  when(Deuce) {
  }

  when(Advantage) {
  }

  // unhandled elided ...

  whenUnhandled {
    // common code for both states
    case Event(Queue(obj), t @ Todo(_, v)) =>
      goto(Active) using t.copy(queue = v :+ obj)

    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      //stay
      goto(actors.Error) using Uninitialized
  }

  when(actors.Error) {
    case Event("stop", _) => { log.info("in state {}:Event 'stop'",stateName,stateData)
      // do cleanup ...
      stop(FSM.Shutdown,stateData)
    }
  }

  onTransition {
    case Idle -> Initial =>
      nextStateData match {case MatchSnapshot(UpTo40Score(p1,p2),scorer) => scorer.self ! SetPlayerInfo(p1.playerId,p2.playerId)}

    case Initial -> UpTo40 =>
      stateData match {case MatchSnapshot(UpTo40Score(p1,p2),scorer) => scorer.self ! SetPlayerInitialPoints(p1,p2)}

  }

  onTermination {
    case StopEvent(FSM.Normal, state, data)         => log.info("stop in state {} for {}",state,FSM.Normal)
    case StopEvent(FSM.Shutdown, state, data)       => log.info("shutdown")
    case StopEvent(FSM.Failure(cause), state, data) => log.info("Failure!! {} {}",stateName.toString, stateData.toString)
  }
*/

  initialize()

}


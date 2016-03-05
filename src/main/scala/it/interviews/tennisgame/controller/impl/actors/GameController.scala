package it.interviews.tennisgame.controller.impl.actors

import akka.actor.FSM
import it.interviews.tennisgame.controller.actors
import it.interviews.tennisgame.domain.impl.actors
import it.interviews.tennisgame.domain.impl.actors._

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class GameController extends FSM[TennisGameFsmState,TennisGameFsmData]{

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(InitGame(p1,p2), Uninitialized) =>
      log.info("received Event "+InitGame.getClass.getName+" and Data "+Uninitialized.getClass.getName)
      goto(Initial) using //stay using Todo(ref, Vector.empty)
  }

  // transition elided ...

  when(Active, stateTimeout = 1 second) {
    case Event(Flush | StateTimeout, t: Todo) =>
      goto(Idle) using t.copy(queue = Vector.empty)
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

  /*
   * The transition callback is a partial function which takes as input a pair of states—the current and the next state.
   * The FSM trait includes a convenience extractor for these in form of an arrow operator,
   * which conveniently reminds you of the direction of the state change which is being matched.
   * During the state change, the old state data is available via stateData as shown,
   * and the new state data would be available as nextStateData.
   */
  onTransition {
    case Active -> Idle =>
      //old data
      stateData match {
        case Todo(ref, queue) => ref ! Batch(queue)
        case _                => // nothing to do
      }
      //nextStateData

      sender() ! "transition"

  }

  onTermination {
    case StopEvent(FSM.Normal, state, data)         => log.info("stop in state {} for {}",state,FSM.Normal)
    case StopEvent(FSM.Shutdown, state, data)       => log.info("shutdown")
    case StopEvent(FSM.Failure(cause), state, data) => log.info("Failure!! {} {}",stateName.toString, stateData.toString)
  }

  initialize()
}

}

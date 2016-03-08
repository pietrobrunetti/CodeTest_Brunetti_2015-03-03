package it.interviews.tennisgame

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.testkit._
import it.interviews.tennisgame.boundary.impl.actors.{TennisPlayer, TennisGame}
import it.interviews.tennisgame.boundary.{ParticipantActor, PlayerActor}
import it.interviews.tennisgame.controller.impl.actors.GameController
import it.interviews.tennisgame.dal.Scoreboard
import it.interviews.tennisgame.dal.impl.actors.TennisScoreboard
import it.interviews.tennisgame.domain.{Points, Scores, GameStateData}
import it.interviews.tennisgame.domain.impl._
import it.interviews.tennisgame.domain.impl.actors._
import org.scalatest.{FunSpecLike, BeforeAndAfterAll, BeforeAndAfter}

/**
  * Created by Pietro Brunetti on 07/03/16.
  */
class ActorBehaviourTest extends TestKit(ActorSystem("componentsSystem"))
  //with BeforeAndAfter //with BeforeAndAfterAll
  with ImplicitSender
  with FunSpecLike {

  describe("Tennis Scoreboard Behavior") {

    it("should store current player info and scores") {
      val tsRef = TestActorRef[TennisScoreboard]
      tsRef ! SetPlayerInfo(TennisPlayerIdWithPoints("dummy1",TennisPoints(0)),TennisPlayerIdWithPoints("dummy2",TennisPoints(0)))

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(TennisScores(TennisPlayerIdWithPoints("dummy1",TennisPoints(0)),TennisPlayerIdWithPoints("dummy2",TennisPoints(0))),
        TennisPlayerIdWithPoints("dummy1",TennisPoints(0)),TennisPlayerIdWithPoints("dummy2",TennisPoints(0))))
    }

    it("should update players' scores") {
      val tsRef = TestActorRef[TennisScoreboard]
      tsRef ! SetPlayerInfo(TennisPlayerIdWithPoints("dummy1",TennisPoints(0)),TennisPlayerIdWithPoints("dummy2",TennisPoints(0)))
      tsRef ! ScoresUpdate(DeuceScore(Forty()))
      assertResult(tsRef.underlyingActor.scoresHistory.top.scores)(DeuceScore(Forty()))
    }

    it("should inform particpant when an pdate occour") {
      val tsRef = TestActorRef[TennisScoreboard]

      val tsSub01 = new MyPlayerTestProbe(system,"dum01")
      val tsSub02 = new MyPlayerTestProbe(system,"dum02")
      tsRef ! SetPlayerInfo(TennisPlayerIdWithPoints("dum01",TennisPoints(0)),TennisPlayerIdWithPoints("dum02",TennisPoints(0)))

      tsRef ! ListenerSubscription(tsSub01)
      tsRef ! ListenerSubscription(tsSub02)

      tsRef ! ScoresUpdate(UpTo40Score(TennisPlayerIdWithPoints("dum01",TennisPoints(3)),TennisPlayerIdWithPoints("dum02",TennisPoints(1))))

      tsSub01.expectMsg(TennisGameStateData(UpTo40Score(TennisPlayerIdWithPoints("dum01",TennisPoints(3)),TennisPlayerIdWithPoints("dum02",TennisPoints(1))),
        TennisPlayerIdWithPoints("dum01",TennisPoints(3)),TennisPlayerIdWithPoints("dum02",TennisPoints(1))))

      tsSub02.expectMsg(TennisGameStateData(UpTo40Score(TennisPlayerIdWithPoints("dum01",TennisPoints(3)),TennisPlayerIdWithPoints("dum02",TennisPoints(1))),
        TennisPlayerIdWithPoints("dum01",TennisPoints(3)),TennisPlayerIdWithPoints("dum02",TennisPoints(1))))
    }
  }



  describe("Game Controller Behavior") {
    it("should move between state while changing internal state") {
      val fsm = TestFSMRef(new GameController)

      assert(fsm.stateName==Idle)
      assert(fsm.stateData==Uninitialized)

      val tsRef = TestActorRef[TennisScoreboard]

      fsm ! GameConfig("p1","p2",tsRef)

      assert(fsm.stateName==Initial)
      assert(fsm.stateData==MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Love()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      fsm ! GameStarted
      assert(fsm.stateName == UpTo40)
      assert(fsm.stateData == MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Love()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(UpTo40Score(TennisPlayerIdWithPoints("p1",TennisPoints(0)),TennisPlayerIdWithPoints("p2",TennisPoints(0))),
        TennisPlayerIdWithPoints("p1",TennisPoints(0)),TennisPlayerIdWithPoints("p2",TennisPoints(0))))

      fsm ! LastPointMadeBy("p1")

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(UpTo40Score(TennisPlayerIdWithPoints("p1",TennisPoints(1)),TennisPlayerIdWithPoints("p2",TennisPoints(0))),
        TennisPlayerIdWithPoints("p1",TennisPoints(1)),TennisPlayerIdWithPoints("p2",TennisPoints(0))))

      assert(fsm.stateName == UpTo40)
      assert(fsm.stateData == MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Fifteen()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      fsm.setState(UpTo40,MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Forty()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p1")

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),
        TennisPlayerIdWithPoints("p1",TennisPoints(4)),TennisPlayerIdWithPoints("p2",TennisPoints(0))))

      assert(fsm.stateName == Won)
      assert(fsm.stateData == MatchSnapshot(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))

      fsm.setState(UpTo40,MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Forty()),TennisPlayerIdWithPoints("p2",Thirty())),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p2")
      assert(fsm.stateName == Deuce)
      assert(fsm.stateData == MatchSnapshot(DeuceScore(TennisPoints(3)),"p1","p2",tsRef))

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(DeuceScore(TennisPoints(3)),
        TennisPlayerIdWithPoints("p1",TennisPoints(3)),TennisPlayerIdWithPoints("p2",TennisPoints(3))))

      fsm ! LastPointMadeBy("p1")

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),
        TennisPlayerIdWithPoints("p1",TennisPoints(4)),TennisPlayerIdWithPoints("p2",TennisPoints(3))))

      assert(fsm.stateName == Advantage)
      assert(fsm.stateData == MatchSnapshot(AdvantageScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p1")

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(5))),
        TennisPlayerIdWithPoints("p1",TennisPoints(5)),TennisPlayerIdWithPoints("p2",TennisPoints(3))))

      assert(fsm.stateName == Won)
      assert(fsm.stateData == MatchSnapshot(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(5))),"p1","p2",tsRef))

      fsm.setState(Advantage,MatchSnapshot(AdvantageScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))
      fsm ! LastPointMadeBy("p2")

      assertResult(tsRef.underlyingActor.scoresHistory.top)(TennisGameStateData(DeuceScore(TennisPoints(4)),
        TennisPlayerIdWithPoints("p1",TennisPoints(4)),TennisPlayerIdWithPoints("p2",TennisPoints(4))))

      assert(fsm.stateName == Deuce)
      assert(fsm.stateData == MatchSnapshot(DeuceScore(TennisPoints(4)),"p1","p2",tsRef))

    }
  }

  describe("Tennis Game Actor Behavior") {
    it("should act as boundary compoent of the system") {

      val tsRef = TestActorRef[TennisGame]

      val p1 = new MyPlayerTestProbe(system,"pp1")
      val p2 = new MyPlayerTestProbe(system,"pp2")

      tsRef ! InitGame(p1,p2)

      tsRef ! StartGame

      tsRef.tell(WantToObserveGameState(p1),p1.actor)
      tsRef.tell(WantToObserveGameState(p2),p2.actor)

      tsRef.tell(PointMade(null,p1),p1.actor)

      p1.expectMsg(TennisGameStateData(
        UpTo40Score(TennisPlayerIdWithPoints("pp1-2",TennisPoints(1)),TennisPlayerIdWithPoints("pp2-3",TennisPoints(0))),
        TennisPlayerIdWithPoints("pp1-2",TennisPoints(1)),TennisPlayerIdWithPoints("pp2-3",TennisPoints(0))))

    }

  }

}


package it.interviews.tennisgame

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.testkit._
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
      val tsSub01 = TestProbe()
      val tsSub02 = TestProbe()
      tsRef ! SetPlayerInfo(TennisPlayerIdWithPoints("dum01",TennisPoints(0)),TennisPlayerIdWithPoints("dum02",TennisPoints(0)))

      tsRef ! ListenerSubscription(tsSub01.ref)
      tsRef ! ListenerSubscription(tsSub02.ref)

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

      fsm ! LastPointMadeBy("p1")
      assert(fsm.stateName == UpTo40)
      assert(fsm.stateData == MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Fifteen()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      fsm.setState(UpTo40,MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Forty()),TennisPlayerIdWithPoints("p2",Love())),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p1")
      assert(fsm.stateName == Won)
      assert(fsm.stateData == MatchSnapshot(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))

      fsm.setState(UpTo40,MatchSnapshot(UpTo40Score(TennisPlayerIdWithPoints("p1",Forty()),TennisPlayerIdWithPoints("p2",Thirty())),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p2")
      assert(fsm.stateName == Deuce)
      assert(fsm.stateData == MatchSnapshot(DeuceScore(TennisPoints(3)),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p1")
      assert(fsm.stateName == Advantage)
      assert(fsm.stateData == MatchSnapshot(AdvantageScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))

      fsm ! LastPointMadeBy("p1")
      assert(fsm.stateName == Won)
      assert(fsm.stateData == MatchSnapshot(WonScore(TennisPlayerIdWithPoints("p1",TennisPoints(5))),"p1","p2",tsRef))

      fsm.setState(Advantage,MatchSnapshot(AdvantageScore(TennisPlayerIdWithPoints("p1",TennisPoints(4))),"p1","p2",tsRef))
      fsm ! LastPointMadeBy("p2")
      assert(fsm.stateName == Deuce)
      assert(fsm.stateData == MatchSnapshot(DeuceScore(TennisPoints(4)),"p1","p2",tsRef))

    }
  }

  describe("Tennis Game Actor Behavior") {


  }



  //override def beforeAll() = ???
  //override def afterAll() = ???

}


package it.interviews.tennisgame

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import it.interviews.tennisgame.boundary.impl.actors.TennisGame
import it.interviews.tennisgame.domain.impl._
import it.interviews.tennisgame.domain.impl.actors._
import org.scalatest.FunSpecLike

import scala.concurrent.ExecutionContext
import scala.util.{Success => ScalaSuccess}
import scala.concurrent.duration._

/**
  * Created by Pietro Brunetti on 06/03/16.
  */
class SystemBehaviourTestSuite extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with FunSpecLike {

  describe("System Behavior Test") {



    it("the game should be initialized with players id") {
      val tsRef = TestActorRef[TennisGame]

      val p1 = new MyPlayerTestProbe(system,"pp1")
      val p2 = new MyPlayerTestProbe(system,"pp2")

      tsRef ! InitGame(p1,p2)
      tsRef ! StartGame

      tsRef.tell(ScoresState,p1.actor)

      p1.expectMsg(UpTo40Score(TennisPlayerIdWithPoints("pp1",TennisPoints(0)),TennisPlayerIdWithPoints("pp2",Love())))
    }

    it("when the game start parcipants such as player can be informed by the scoreboard about the state of the game") {
      val tsRef = TestActorRef[TennisGame]

      val p1 = new MyPlayerTestProbe(system,"pp1")
      val p2 = new MyPlayerTestProbe(system,"pp2")

      tsRef ! InitGame(p1,p2)
      tsRef ! StartGame

      implicit val ec = ExecutionContext.global
      val defaultFiniteDureationAsk = 5.seconds
      implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

      tsRef.tell(PointMade(null,p1),p1.actor)

      ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
        case ScalaSuccess(result) => assert(result == UpTo40Score(TennisPlayerIdWithPoints("pp1",TennisPoints(1)),TennisPlayerIdWithPoints("pp2",TennisPoints(0))))
      }
    }

    describe("Point making") {
      it("points can be added to each player one at a time based on the player id that had made such point") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result == UpTo40Score(TennisPlayerIdWithPoints("pp1",TennisPoints(1)),TennisPlayerIdWithPoints("pp2",TennisPoints(1))))
        }
      }

      it("the zero point of each player should be associated to Love score") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[UpTo40Score].pip1.points.sym.get.name == "love")
        }
      }

      it("the firt point of each player should be associated to Fifteen score") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)

        Thread.sleep(3000)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[UpTo40Score].pip1.points.sym.get.name == "fifteen")
        }
      }

      it("the second point of each player should be associated to Thirty score") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[UpTo40Score].pip1.points.sym.get.name == "thirty")
        }
      }

      it("the third point of each player should be associated to Forty score") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[UpTo40Score].pip1.points.sym.get.name == "forty")
        }
      }
    }

    describe("Game Score") {
      it("When both of the two player has reach at least 3 points with equal points the score is Deuce") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)

        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[DeuceScore].points.value == "3")
        }
      }

      it("When both of the two player has reach at least 3 points and one of the two has gained one more point the score is Advantage for the lead player") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)
        tsRef.tell(PointMade(null,p1),p1.actor)

        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[AdvantageScore].who.playerId == "pp2")
        }
      }

      it("When one of the players has reach 4 points and the absolute differents between points is equal or grater than 2 the score is Won for the lead player ") {
        val tsRef = TestActorRef[TennisGame]

        val p1 = new MyPlayerTestProbe(system,"pp1")
        val p2 = new MyPlayerTestProbe(system,"pp2")

        tsRef ! InitGame(p1,p2)
        tsRef ! StartGame

        implicit val ec = ExecutionContext.global
        val defaultFiniteDureationAsk = 5.seconds
        implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)

        tsRef.tell(PointMade(null,p1),p1.actor)

        tsRef.tell(PointMade(null,p2),p2.actor)

        tsRef.tell(PointMade(null,p1),p1.actor)

        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)

        tsRef.tell(PointMade(null,p1),p1.actor)

        tsRef.tell(PointMade(null,p2),p2.actor)
        tsRef.tell(PointMade(null,p2),p2.actor)

        ask(tsRef,ScoresState,p1.actor).mapTo[TennisScores].onComplete{
          case ScalaSuccess(result) => assert(result.asInstanceOf[WonScore].who.playerId == "pp2")
        }
      }
    }

  }





}

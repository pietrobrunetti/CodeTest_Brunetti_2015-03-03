package it.interviews.tennisgame

import it.interviews.tennisgame.domain._
import it.interviews.tennisgame.domain.impl._
import org.scalatest.FunSpec

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
class DomainTestSuite extends FunSpec {

  describe("TennisPoints in Tennis Game") {

    it("must be not less than zero",DomainTest,TennisPointsTest) {
      intercept[IllegalArgumentException] {
        TennisPoints(Some('everything),-1)
      }
    }

    it("must be of type 'FirstThreePoints' if it is included " +
      "between 0 and 3, 'TennisPoints' otherwise",DomainTest,TennisPointsTest) {
      intercept[IllegalArgumentException] {
        (0 to 3).foreach(TennisPoints(None,_))
      }
    }

    it("it should has a symbol associated whit if it is included " +
      "in (0 to 3) points range called 'FirstThreeTennisPoints'",DomainTest,TennisPointsTest) {
      val zero:FirstThreeTennisPoints = Love()
      assert(zero.sym.get=='love && zero.value==0)

      val uno:FirstThreeTennisPoints = Fifteen()
      assert(uno.sym.get=='fifteen && uno.value==1)

      val due:FirstThreeTennisPoints = Thirty()
      assert(due.sym.get=='thirty && due.value==2)

      val tre:FirstThreeTennisPoints = Forty()
      assert(tre.sym.get=='forty && tre.value==3)
    }
  }




  describe("Scores in Tennis Game") {

    describe("can be UpTo40 type (love,fifteen,thirty,forty)") {

      it("should be displayed in this way", DomainTest,TennisScoresTest) {
        val upTo40 = UpTo40Score(TennisPlayerIdWithPoints("DummyPlayer1", Love()), TennisPlayerIdWithPoints("DummyPlayer2", Fifteen()))
        assert(upTo40.getStringRep == s"Player ${upTo40.pip1.playerId}: ${upTo40.pip1.points} :: Player ${upTo40.pip2.playerId}: ${upTo40.pip2.points}")

      }

      it("should has Points of type 'FirstThreeTennisPoints'", DomainTest,TennisScoresTest) {
        intercept[IllegalArgumentException] {
          UpTo40Score(TennisPlayerIdWithPoints("DummyPlayer3", TennisPoints(None, 1)), TennisPlayerIdWithPoints("DummyPlayer4", Fifteen()))
        }
      }
    }


    describe("can be Deuce type") {
      it("should be displayed in this way", DomainTest,TennisScoresTest) {
        val deuce = DeuceScore(Forty())
        assert(deuce.getStringRep == s"Deuce at ${deuce.points.value} points")
      }

      it("should has 3 points at least as value", DomainTest,TennisScoresTest) {
        intercept[IllegalArgumentException] {
          DeuceScore(Love())
        }
      }
    }


    describe("can be Advantage type") {
      it("should be displayed in this way", DomainTest,TennisScoresTest) {
        val adv = AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer1", TennisPoints(None, 5)))
        assert(adv.getStringRep == s"Advantage for Player ${adv.who.playerId}")
      }

      it("should has 4 points at least as value", DomainTest,TennisScoresTest) {
        intercept[IllegalArgumentException] {
          AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer2", Thirty()))
        }
      }
    }


    describe("can be Won type") {
      it("should be displayed in this way", DomainTest,TennisScoresTest) {
        val won = WonScore(TennisPlayerIdWithPoints("WinnerPlayer", TennisPoints(None,4)))
        assert(won.getStringRep == s"The Winner is Player ${won.who.playerId.toString} with ${won.who.points} points!")
      }

      it("should has 4 points at least as value", DomainTest,TennisScoresTest) {
        intercept[IllegalArgumentException] {
          WonScore(TennisPlayerIdWithPoints("WinnerPlayer", Thirty()))
        }
      }
    }

    it("shoud be created following the requirements roles prooven in tests above",DomainTest,TennisScoresTest) {
      val tp01 = TennisPlayerIdWithPoints("DummyPlayer1", Love())
      val tp02 = TennisPlayerIdWithPoints("DummyPlayer2", Love())
      assertResult(UpTo40Score(tp01,tp02))(TennisScores(tp01,tp02))

      val tp03 = TennisPlayerIdWithPoints("DummyPlayer3", Thirty())
      val tp04 = TennisPlayerIdWithPoints("DummyPlayer4", TennisPoints(None,4))
      assertResult(WonScore(tp04))(TennisScores(tp03,tp04))

      val tp05 = TennisPlayerIdWithPoints("DummyPlayer5", Forty())
      val tp06 = TennisPlayerIdWithPoints("DummyPlayer6", Forty())
      assertResult(DeuceScore(Forty()))(TennisScores(tp05,tp06))

      val tp07 = TennisPlayerIdWithPoints("DummyPlayer7", Forty())
      val tp08 = TennisPlayerIdWithPoints("DummyPlayer8", TennisPoints(None,4))
      assertResult(AdvantageScore(tp08))(TennisScores(tp07,tp08))
    }

  }




  describe("TennisGameStateDate in Tennis Game") {

    it("must be displayed with the score of the game " +
      "and the associated points of the two players",DomainTest,TennisGameStateDataTest) {
      val tgsd = TennisGameStateData(DeuceScore(TennisPoints(None,5)),TennisPlayerIdWithPoints("DummyPlayer1", TennisPoints(None, 5)),TennisPlayerIdWithPoints("DummyPlayer2", TennisPoints(None, 5)))
      assert(tgsd.toString == s"Game Actual State: << ${tgsd.scores.getStringRep} >>\n Points:\n${Seq(tgsd.p1P,tgsd.p2P).foreach(pp=>s"\t${pp.playerId} => ${pp.points}")}")
    }

    it("Its scores of type 'UpTo40' must be correlated " +
      "to the corresponding Players' point types",DomainTest,TennisGameStateDataTest) {
      intercept[IllegalArgumentException] {
        val tpwp01 = TennisPlayerIdWithPoints("DummyPlayer2",Love())
        val tpwp02 = TennisPlayerIdWithPoints("DummyPlayer3",Thirty())
        TennisGameStateData(UpTo40Score(tpwp01,tpwp01),tpwp01,tpwp02)
      }
    }

    it("Its scores of type 'Deuce' must be correlated " +
      "to the corresponding Players' point types",DomainTest,TennisGameStateDataTest) {
      intercept[IllegalArgumentException] {
        val tpwp03 = TennisPlayerIdWithPoints("DummyPlayer4",Thirty())
        val tpwp04 = TennisPlayerIdWithPoints("DummyPlayer5",Thirty())
        TennisGameStateData(DeuceScore(Forty()),tpwp03,tpwp04)
      }

      intercept[IllegalArgumentException] {
        val tpwp03 = TennisPlayerIdWithPoints("DummyPlayer4",Forty())
        val tpwp04 = TennisPlayerIdWithPoints("DummyPlayer5",Thirty())
        TennisGameStateData(DeuceScore(Forty()),tpwp03,tpwp04)
      }
    }

    it("Its scores of type 'Advantage' must be correlated " +
      "to the corresponding Players' point types",DomainTest,TennisGameStateDataTest) {

      TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer00",TennisPoints(None,4))),TennisPlayerIdWithPoints("DummyPlayer00",TennisPoints(None,4)),TennisPlayerIdWithPoints("DummyPlayer01",Forty()))
      TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer01",TennisPoints(None,4))),TennisPlayerIdWithPoints("DummyPlayer00",Forty()),TennisPlayerIdWithPoints("DummyPlayer01",TennisPoints(None,4)))

      intercept[IllegalArgumentException] {
        val tpwp05 = TennisPlayerIdWithPoints("DummyPlayer6",Forty())
        val tpwp06 = TennisPlayerIdWithPoints("DummyPlayer7",Forty())
        TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer6-7",Forty())),tpwp05,tpwp06)
      }

      intercept[IllegalArgumentException] {
        val tpwp03 = TennisPlayerIdWithPoints("DummyPlayer8",Forty())
        val tpwp04 = TennisPlayerIdWithPoints("DummyPlayer9",Thirty())
        TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer8-9",TennisPoints(None,4))),tpwp03,tpwp04)
      }
    }

    it("Its scores of type 'Won' must be correlated " +
      "to the corresponding Players' point types",DomainTest,TennisGameStateDataTest) {

      TennisGameStateData(WonScore(TennisPlayerIdWithPoints("DummyPlayer02",TennisPoints(None,4))),TennisPlayerIdWithPoints("DummyPlayer02",TennisPoints(None,4)),TennisPlayerIdWithPoints("DummyPlayer03",Thirty()))
      TennisGameStateData(WonScore(TennisPlayerIdWithPoints("DummyPlayer03",TennisPoints(None,6))),TennisPlayerIdWithPoints("DummyPlayer02",Forty()),TennisPlayerIdWithPoints("DummyPlayer03",TennisPoints(None,6)))

      intercept[IllegalArgumentException] {
        val tpwp05 = TennisPlayerIdWithPoints("DummyPlayer6",Forty())
        val tpwp06 = TennisPlayerIdWithPoints("DummyPlayer7",Forty())
        TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer6-7",Forty())),tpwp05,tpwp06)
      }

      intercept[IllegalArgumentException] {
        val tpwp03 = TennisPlayerIdWithPoints("DummyPlayer8",Forty())
        val tpwp04 = TennisPlayerIdWithPoints("DummyPlayer9",Thirty())
        TennisGameStateData(AdvantageScore(TennisPlayerIdWithPoints("DummyPlayer8-9",TennisPoints(None,4))),tpwp03,tpwp04)
      }
    }

  }

}

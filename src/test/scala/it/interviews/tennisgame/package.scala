package it.interviews

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.testkit.TestProbe
import it.interviews.tennisgame.boundary.PlayerActor
import it.interviews.tennisgame.domain.{Points, Scores, GameStateData}
import org.scalatest.Tag

/**
  * Created by Pietro Brunetti on 04/03/16.
  */
package object tennisgame {

  object DomainTest extends Tag("it.interviews.tags.DomainTest")
  object TennisPointsTest extends Tag("it.interviews.domain.TennisPointsTest")
  object TennisScoresTest extends Tag("it.interviews.tags.domain.TennisScoresTest")
  object TennisGameStateDataTest extends Tag("it.interviews.tags.domain.TennisGameStateDataTest")

  class MyProbePlayerActor(as:ActorSystem,name:String) extends TestProbe(as,name) with PlayerActor{
    override def playerId: String = name
    override protected def retrievePlayerPoints(p: PlayerActor): Points = ???
    override protected def makePoint: Unit = ???
    override protected def retrieveScores: Scores = ???
    override protected def retrieveGameState: GameStateData = ???

    override def getRef: ActorRef = ref
  }

}

package it.interviews.tennisgame

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, FunSpecLike, BeforeAndAfterAll}

/**
  * Created by Pietro Brunetti on 06/03/16.
  */
class SystemBehaviourTestSuite extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with FunSpecLike {

  describe("System Behavior Test") {



  }





}

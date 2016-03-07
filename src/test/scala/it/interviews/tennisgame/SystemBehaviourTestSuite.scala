package it.interviews.tennisgame

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, FunSpecLike, BeforeAndAfterAll}

/**
  * Created by Pietro Brunetti on 07/03/16.
  */
class SystemBehaviourTestSuite extends TestKit(ActorSystem("testSystem"))
  //with BeforeAndAfter with BeforeAndAfterAll
  with ImplicitSender
  with FunSpecLike {


/*
  before {

  }

  after {

  }
*/
  //override def beforeAll() = ???
  //override def afterAll() = ???




}

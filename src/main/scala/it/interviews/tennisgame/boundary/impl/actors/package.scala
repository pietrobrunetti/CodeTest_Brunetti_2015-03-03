package it.interviews.tennisgame.boundary.impl

import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by Pietro Brunetti on 08/03/16.
  */
package object actors {

  val defaultFiniteDureationAsk = 5.seconds
  implicit val actorAskTimeout = Timeout(defaultFiniteDureationAsk)
}

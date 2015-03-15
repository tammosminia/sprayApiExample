import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.duration._

object SprayApiApp extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("SprayApiApp")

  // create apiActor
  val apiActor = system.actorOf(Props[ApiActor], "apiActor")

  //timeout needs to be set as an implicit val
  implicit val timeout = Timeout(5.seconds)

  // start a new HTTP server on port 8080 with apiActor as the handler
  IO(Http) ? Http.Bind(apiActor, interface = "localhost", port = 8080)
}
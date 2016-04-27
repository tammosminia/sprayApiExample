import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

//Our domain class
case class Robot(naam: String, kleur: Option[String], aantalArmen: Int) {
  require(aantalArmen >= 0, "Robots kunnen geen negatief aantal armen hebben!")
}

object RobotsApiApp extends App with SprayJsonSupport with DefaultJsonProtocol {
  implicit val system = ActorSystem("RobotSystem")

  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)
  val port = system.settings.config.getInt("port")

  //We use the default json marshalling for Robot.
  //There are multiple jsonFormat methods in DefaultJsonProtocol. Depending on how many parameters the model class has.
  //Robot has just one, so we use jsonFormat1
  implicit val RobotFormat = jsonFormat3(Robot)

  //A list of our domain objects
  var robots = List(Robot("R2D2", Some("wit"), 0), Robot("Asimo", None, 2))

  val route: Route = logRequestResult("RobotsAPI") {
    pathPrefix("robots") {
      get {
        //with get we will return our current list of robots
        complete {
          //complete will return the result in an appropriate format
          //With SprayJsonSupport it knows how to marshall a List to json
          //With RobotFormat it knows how to marshall Robot
          robots
        }
      } ~ post {
        //With post we will add a robot
        handleWith { robot: Robot => //handleWith will unmarshall the input
          robots = robot :: robots
          system.log.info(s"We hebben nu ${robots.size} robots.")
          robot //handleWith will also marshall the result. Here we simply return the new robot.
        }
      } ~ delete {
        path(Segment) { naam =>
          robots = robots.filter { _.naam != naam }
          complete(s"robot $naam verwijderd")
        }
      }
    } ~ path("") {
      //When we go to localhost:8080/ we can show documentation
      complete("Robots API documentatie")
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", port)

  println(s"Robots API - http://localhost:$port/")
}

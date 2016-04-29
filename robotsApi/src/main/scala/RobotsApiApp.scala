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

case class Robot(naam: String, kleur: Option[String], aantalArmen: Int) {
  require(aantalArmen >= 0, "Robots kunnen geen negatief aantal armen hebben!")
}

object RobotsApiApp extends App with SprayJsonSupport with DefaultJsonProtocol {
  implicit val system = ActorSystem("RobotSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)
  val port = system.settings.config.getInt("port")

  implicit val RobotFormat = jsonFormat3(Robot)

  var robots = List(Robot("R2D2", Some("wit"), 0), Robot("Asimo", None, 2))

  val route: Route = logRequestResult("RobotsAPI") {
    pathPrefix("robots") {
      get {
        complete(robots)
      } ~ post {
        handleWith { robot: Robot =>
          robots = robot :: robots
          system.log.info(s"We hebben nu ${robots.size} robots.")
          robot
        }
      } ~ delete {
        path(Segment) { naam =>
          robots = robots.filter { _.naam != naam }
          complete(s"robot $naam verwijderd")
        }
      }
    } ~ path("") {
      complete("Robots API documentatie")
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", port)
  println(s"Robots API - http://localhost:$port/")
}

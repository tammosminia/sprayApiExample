import akka.actor.{ActorLogging, Actor}
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.routing._

object RobotProtocol extends DefaultJsonProtocol {
  //Our domain class
  case class Robot(name: String)

  //We use the default json marshalling for Robot.
  //There are multiple jsonFormat methods in DefaultJsonProtocol. Depending on how many parameters the model class has.
  //Robot has just one, so we use jsonFormat1
  implicit val RobotFormat = jsonFormat1(Robot)
}
import RobotProtocol._

class ApiActor extends Actor with HttpService with ActorLogging {
  //A list of our domain objects
  var robots = List(Robot("R2D2"), Robot("Asimo"))

  //The HttpService trait defines only one abstract member, which
  //connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  //This actor only runs our route, but you could add
  //other things here, like request stream processing or timeout handling
  def receive = runRoute(apiRoute)

  //Notice that both path methods return a Route. We need to chain them together with ~
  val apiRoute: Route =
    path("robots") {
      get { //with get we will return our current list of robots
        log.info("Building get route")
        complete {
          log.info("Executing get route")
          //complete will return the result in an appropriate format
          //With SprayJsonSupport it knows how to marshall a List to json
          //With RobotFormat it knows how to marshall Robot
          robots
        }
      } ~ post { //With post we will add a robot
        log.info("Building post route")
        handleWith { robot: Robot =>  //handleWith will unmarshall the input
          log.info("Executing post route")
          robots = robot :: robots
          robot //handleWith will also marshall the result. Here we simply return the new robot.
        }
      }
    } ~ path("") { //When we go to localhost:8080/ just show a link to localhost:8080/robots
      respondWithMediaType(MediaTypes.`text/html`) { //XML is marshalled to `text/xml` by default, so we simply override here
        complete {
          <html>
            <body>
              <a href="/robots">The list of robots</a>
            </body>
          </html>
        }
      }
    }
}
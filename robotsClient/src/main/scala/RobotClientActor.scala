import akka.actor.{ActorLogging, Actor}
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.pipe

//The JSON classes we use with the API. Notice that we can use the same object and the same JSON mapping as we defined in the API
object RobotProtocol extends DefaultJsonProtocol {
  //Our domain class
  case class Robot(name: String, color: Option[String], amountOfArms: Int)

  implicit val RobotFormat = jsonFormat3(Robot)
}
import RobotProtocol._


class RobotClientActor extends Actor with ActorLogging {
  val apiLocation = "http://localhost:8080" //Make sure robotsAPI is running here

  import context.dispatcher // execution context for futures

  def getRobots() = {
    log.info("getting all robots")
    val pipeline: HttpRequest => Future[List[Robot]] = sendReceive ~> unmarshal[List[Robot]]
    val f: Future[List[Robot]] = pipeline(Get(s"$apiLocation/robots"))
    f.pipeTo(self)
  }

  def postRobot() = {
    val newRobot = Robot("Data", Some("white"), 2)
    log.info("posting a new robot")
    val pipeline: HttpRequest => Future[Robot] = sendReceive ~> unmarshal[Robot]
    val f: Future[Robot] = pipeline(Post(s"$apiLocation/robots", newRobot))
    f.pipeTo(self)
  }

  def receive = {
    case "go" =>
      getRobots()
    case robots: List[Robot] =>
      log.info(s"Got the list of robots: $robots")
      postRobot()
    case robot: Robot =>
      log.info(s"got response $robot")
  }

}
import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

//The JSON classes we use with the API. Notice that we can use the same object and the same JSON mapping as we defined in the API
object RobotProtocol extends DefaultJsonProtocol {
  //Our domain class
  case class Robot(name: String, color: Option[String], amountOfArms: Int)

  implicit val RobotFormat = jsonFormat3(Robot)
}
import RobotProtocol._

object RobotClientApp extends App {
  val apiLocation = "http://localhost:8080" //Make sure robotsAPI is running here

  val timeout = 5.seconds

  //Spray needs an implicit ActorSystem and ExecutionContext
  implicit val system = ActorSystem("robotClient")
  import system.dispatcher

  def getRobots() = {
    println("getting all robots")
    val pipeline: HttpRequest => Future[List[Robot]] = sendReceive ~> unmarshal[List[Robot]]
    val f: Future[List[Robot]] = pipeline(Get(s"$apiLocation/robots"))
    val robots = Await.result(f, timeout)
    println(s"Got the list of robots: $robots")
  }

  def postRobot() = {
    val newRobot = Robot("Data", Some("white"), 2)
    println("posting a new robot")
    val pipeline: HttpRequest => Future[Robot] = sendReceive ~> unmarshal[Robot]
    val f: Future[Robot] = pipeline(Post(s"$apiLocation/robots", newRobot))
    val robot = Await.result(f, timeout)
    println(s"got response $robot")
  }

  postRobot()
  getRobots()

  system.shutdown()
}

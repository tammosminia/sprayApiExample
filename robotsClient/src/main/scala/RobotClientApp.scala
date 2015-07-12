import akka.actor.{Props, ActorSystem}

object RobotClientApp extends App {
  implicit val system = ActorSystem("robotClient")
  val robotClientActor = system.actorOf(Props[RobotClientActor], "RobotClientActor")
  robotClientActor ! "go"

}

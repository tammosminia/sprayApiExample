import akka.actor.{ActorRef, ActorLogging, Actor}
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object ThrottleActor {
  object OneSecondLater
  object Accepted
  object ExceededMaxTps
}
import ThrottleActor._

class ThrottleActor (target: ActorRef, maxTps: Int) extends Actor with ActorLogging {
  implicit val executionContext: ExecutionContext = context.dispatcher
  context.system.scheduler.schedule(1.second, 1.second, self, OneSecondLater)

  var messagesThisSecond: Int = 0

  def receive = {
    case OneSecondLater =>
      log.info(s"OneSecondLater ${DateTime.now} $messagesThisSecond requests.")
      messagesThisSecond = 0
    case message if messagesThisSecond >= maxTps =>
      sender ! ExceededMaxTps
      messagesThisSecond += 1
      log.info(s"ExceededMaxTps ${DateTime.now} $messagesThisSecond requests.")
    case message =>
      sender ! Accepted
      target ! message
      messagesThisSecond += 1
  }
}

class Printer extends Actor with ActorLogging {
  def receive = {
    case message => log.info(s"${DateTime.now} $message")
  }
}


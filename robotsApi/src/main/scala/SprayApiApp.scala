import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.servlet.WebBoot
import scala.concurrent.duration._

trait SprayApi {
  implicit val system = ActorSystem("SprayApiApp")
  val apiActor = system.actorOf(Props[ApiActor], "apiActor")
}

//for use with spray-servlet
class SprayApiServlet extends WebBoot with SprayApi {
  override val serviceActor = apiActor
}

//for use with spray-can
object SprayApiCan extends App with SprayApi {
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(apiActor, interface = "localhost", port = 8080)
}

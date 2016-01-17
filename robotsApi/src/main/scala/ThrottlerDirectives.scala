import org.joda.time.DateTime
import shapeless.HNil
import spray.routing.{ValidationRejection, Directives}

object ThrottlerDirectives extends Directives {
  def throttle(maxTps: Int) = {
    def currentSecond = DateTime.now.withMillisOfSecond(0)
    var measuredSecond = currentSecond
    var messagesThisSecond: Int = 0
    println("create throttler")

    extract { ctx =>
      val s = currentSecond
      println(s"throttle $s $measuredSecond")
      if (s == measuredSecond) {
        messagesThisSecond += 1
        messagesThisSecond
      } else {
        measuredSecond = s
        messagesThisSecond = 1
        messagesThisSecond
      }
    }.flatMap[HNil] { m: Int =>
      if (m <= maxTps) pass
      else reject(ValidationRejection(s"Exceeded maximum tps! measured tps = $messagesThisSecond, only $maxTps allowed."))
    }
  }
}


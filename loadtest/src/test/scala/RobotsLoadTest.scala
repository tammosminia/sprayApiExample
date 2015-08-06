import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RobotsLoadTest extends Simulation {
	val baseUrl = "http://localhost:8080"

	val httpProtocol = http
		.baseURL(baseUrl)
		.inferHtmlResources()
		.acceptEncodingHeader("gzip,deflate")
		.contentTypeHeader("application/json")
		.userAgentHeader("Apache-HttpClient/4.1.1 (java 1.5)")

	val s = scenario("Simulation")
		.exec(http("request_0")
		.post("/robots")
		.body(StringBody("""{
						|  "name": "C3PO",
						|  "amountOfArms": 2
						|}""".stripMargin))
		)

	setUp(s.inject(constantUsersPerSec(1000) during(10 seconds))).protocols(httpProtocol)
}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RobotsLoadTest extends Simulation {

	lazy val baseUrl = "http://localhost:8081"

	val httpProtocol = http
		.baseURL(baseUrl)
		.inferHtmlResources()
		.acceptEncodingHeader("gzip,deflate")
		.contentTypeHeader("application/json")
		.userAgentHeader("Apache-HttpClient/4.1.1 (java 1.5)")

	val s = scenario("Simulation")
		.exec(http("request_0")
		.post("/robots")
		.body(RawFileBody("request.json"))
		)

	setUp(s.inject(constantUsersPerSec(100) during(2 minutes))
	).protocols(httpProtocol)

}